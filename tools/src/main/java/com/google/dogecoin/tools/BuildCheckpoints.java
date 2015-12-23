/*
 * Copyright 2013 Google Inc.
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.dogecoin.tools;

import com.google.dogecoin.core.*;
import com.google.dogecoin.params.MainNetParams;
import com.google.dogecoin.store.BlockStore;
import com.google.dogecoin.store.MemoryBlockStore;
import com.google.dogecoin.utils.BriefLogFormatter;
import com.google.dogecoin.utils.Threading;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.Date;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkState;

/**
 * Downloads and verifies a full chain from your local peer, emitting checkpoints at each difficulty transition period
 * to a file which is then signed with your key.
 */
public class BuildCheckpoints {

    private static final NetworkParameters PARAMS = MainNetParams.get();
    private static final File CHECKPOINTS_FILE = new File("checkpoints");

    public static void main(String[] args) throws Exception {
        BriefLogFormatter.init();

        // Sorted map of UNIX time of block to StoredBlock object.
        final TreeMap<Integer, StoredBlock> checkpoints = new TreeMap<Integer, StoredBlock>();

        // Configure bitcoinj to fetch only headers, not save them to disk, connect to a local fully synced/validated
        // node and to save block headers that are on interval boundaries, as long as they are <1 month old.
        final BlockStore store = new MemoryBlockStore(PARAMS);
        final BlockChain chain = new BlockChain(PARAMS, store);
        final PeerGroup peerGroup = new PeerGroup(PARAMS, chain);
        peerGroup.addAddress(InetAddress.getLocalHost());
        long now = new Date().getTime() / 1000;
        peerGroup.setFastCatchupTimeSecs(now);

        final long oneMonthAgo = now - (86400 * 7); // DOGE has much more blocks so a week is still safe enough

        chain.addListener(new AbstractBlockChainListener() {
            @Override
            public void notifyNewBestBlock(StoredBlock block) throws VerificationException {
                int height = block.getHeight();
                if (height % PARAMS.getInterval() == 0 && block.getHeader().getTimeSeconds() <= oneMonthAgo) {
                    System.out.println(String.format("Checkpointing block %s at height %d",
                            block.getHeader().getHash(), block.getHeight()));
                    checkpoints.put(height, block);
                }
            }
        }, Threading.SAME_THREAD);

        peerGroup.startAsync();
        peerGroup.awaitRunning();
        peerGroup.downloadBlockChain();

        checkState(checkpoints.size() > 0);

        // Write checkpoint data out.
        final FileOutputStream fileOutputStream = new FileOutputStream(CHECKPOINTS_FILE, false);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final DigestOutputStream digestOutputStream = new DigestOutputStream(fileOutputStream, digest);
        digestOutputStream.on(false);
        final DataOutputStream dataOutputStream = new DataOutputStream(digestOutputStream);
        dataOutputStream.writeBytes("CHECKPOINTS 1");
        dataOutputStream.writeInt(0);  // Number of signatures to read. Do this later.
        digestOutputStream.on(true);
        dataOutputStream.writeInt(checkpoints.size());
        ByteBuffer buffer = ByteBuffer.allocate(StoredBlock.COMPACT_SERIALIZED_SIZE);
        for (StoredBlock block : checkpoints.values()) {
            block.serializeCompact(buffer);
            dataOutputStream.write(buffer.array());
            buffer.position(0);
        }
        dataOutputStream.close();
        Sha256Hash checkpointsHash = new Sha256Hash(digest.digest());
        System.out.println("Hash of checkpoints data is " + checkpointsHash);
        digestOutputStream.close();
        fileOutputStream.close();

        peerGroup.stopAsync();
        peerGroup.awaitTerminated();
        store.close();

        // Sanity check the created file.
        CheckpointManager manager = new CheckpointManager(PARAMS, new FileInputStream(CHECKPOINTS_FILE));
        checkState(manager.numCheckpoints() == checkpoints.size());

        if (PARAMS.getId() == NetworkParameters.ID_MAINNET) {
        StoredBlock test = manager.getCheckpointBefore(1394276500);  // Just after block 131760
        checkState(test.getHeight() == 131760);
            checkState(test.getHeader().getHashAsString()
                    .equals("8335a8d29326c09f35efe982e640b834e383db26e770d0fdbedabc718fb89272"));
        } else if (PARAMS.getId() == NetworkParameters.ID_TESTNET) { // FIXME
            StoredBlock test = manager.getCheckpointBefore(1390500000); // Thu Jan 23 19:00:00 CET 2014
            checkState(test.getHeight() == 167328);
            checkState(test.getHeader().getHashAsString().equals("000000000000002e00a243fe9aa49c78f573091d17372c2ae0ae5e0f24f55b52"));
        }

        System.out.println("Checkpoints written to '" + CHECKPOINTS_FILE.getCanonicalPath() + "'.");
    }
}
