/**
 * Copyright 2011 Noa Resare
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

package com.google.dogecoin.core;


import com.google.dogecoin.params.MainNetParams;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;

public class BitcoinSerializerTest {
    private final byte[] addrMessage = Hex.decode("c0c0c0c06164647200000000000000001f00000081de49a4019c36d" +
            "052010000000000000000000000000000000000ffff614df4e2581c");

    private final byte[] txMessage = Hex.decode(
            "c0 c0 c0 c0 74 78 00 00  00 00 00 00 00 00 00" +
            "00 54 01 00 00 1a bf fa  af 01 00 00 00 02 3d" +
            "45 a8 84 b4 6a 2b b1 dd  b3 fc 6c df 30 87 9f" +
            "58 ff 3e 79 2a dc 9e 22  ea 73 fd b5 46 35 04" +
            "a9 00 00 00 00 6b 48 30  45 02 21 00 bb 81 c6" +
            "b5 e8 93 57 60 4b 63 b6  18 7f db 79 d6 6b 8e" +
            "e3 0c 94 6c bb 8c 64 93  d8 b1 59 ee 05 d0 02" +
            "20 0a 99 f1 d3 c0 49 03  49 f5 f7 dd 54 82 96" +
            "53 5b 94 ba 0f 8e 1e d6  7e 88 f5 14 df bd 31" +
            "90 e5 ee 01 21 02 40 fa  16 cb a9 87 a6 a4 25" +
            "11 9e cf ec d8 e9 b8 c3  18 03 f3 12 18 9c e2" +
            "0c f2 a1 f6 a9 33 35 b6  ff ff ff ff 5e 10 56" +
            "94 41 23 69 2a db f6 c4  e2 48 44 e6 3c a8 bf" +
            "cc ff cc d4 58 f5 50 49  36 d6 c8 cd 70 d1 01" +
            "00 00 00 6b 48 30 45 02  21 00 ec 2e 8d 05 3d" +
            "d1 5a 29 f3 a5 58 9c 3d  d7 bb 68 8c 2b e5 8f" +
            "29 b9 b2 dd e5 75 93 e6  31 39 6c de 02 20 2a" +
            "c1 21 b3 e4 26 df c5 d8  fd 60 1c 79 60 b3 a8" +
            "af 20 f6 c8 14 7b 8d 61  d9 8b 5d ce 57 9a e7" +
            "19 01 21 02 40 fa 16 cb  a9 87 a6 a4 25 11 9e" +
            "cf ec d8 e9 b8 c3 18 03  f3 12 18 9c e2 0c f2" +
            "a1 f6 a9 33 35 b6 ff ff  ff ff 01 00 36 ca 91" +
            "1d 00 00 00 19 76 a9 14  5d 25 b8 54 51 04 4e" +
            "b3 51 1e 1a 2b f2 15 b2  ec 71 2d f4 26 88 ac" +
            "00 00 00 00");

    @Test
    public void testAddr() throws Exception {
        BitcoinSerializer bs = new BitcoinSerializer(MainNetParams.get());
        // the actual data from https://en.bitcoin.it/wiki/Protocol_specification#addr
        AddressMessage a = (AddressMessage)bs.deserialize(ByteBuffer.wrap(addrMessage));
        assertEquals(1, a.getAddresses().size());
        PeerAddress pa = a.getAddresses().get(0);
        assertEquals(22556, pa.getPort());
        assertEquals("97.77.244.226", pa.getAddr().getHostAddress());
        ByteArrayOutputStream bos = new ByteArrayOutputStream(addrMessage.length);
        bs.serialize(a, bos);

        //this wont be true due to dynamic timestamps.
        //assertTrue(LazyParseByteCacheTest.arrayContains(bos.toByteArray(), addrMessage));
    }

    @Test
    public void testLazyParsing()  throws Exception {
        BitcoinSerializer bs = new BitcoinSerializer(MainNetParams.get(), true, false);

    	Transaction tx = (Transaction)bs.deserialize(ByteBuffer.wrap(txMessage));
        assertNotNull(tx);
        assertEquals(false, tx.isParsed());
        assertEquals(true, tx.isCached());
        tx.getInputs();
        assertEquals(true, tx.isParsed());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bs.serialize(tx, bos);
        assertEquals(true, Arrays.equals(txMessage, bos.toByteArray()));
    }

    @Test
    public void testCachedParsing()  throws Exception {
        testCachedParsing(true);
        testCachedParsing(false);
    }

    private void testCachedParsing(boolean lazy)  throws Exception {
        BitcoinSerializer bs = new BitcoinSerializer(MainNetParams.get(), lazy, true);
        
        //first try writing to a fields to ensure uncaching and children are not affected
        Transaction tx = (Transaction)bs.deserialize(ByteBuffer.wrap(txMessage));
        assertNotNull(tx);
        assertEquals(!lazy, tx.isParsed());
        assertEquals(true, tx.isCached());

        tx.setLockTime(1);
        //parent should have been uncached
        assertEquals(false, tx.isCached());
        //child should remain cached.
        assertEquals(true, tx.getInputs().get(0).isCached());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bs.serialize(tx, bos);
        assertEquals(true, !Arrays.equals(txMessage, bos.toByteArray()));

      //now try writing to a child to ensure uncaching is propagated up to parent but not to siblings
        tx = (Transaction)bs.deserialize(ByteBuffer.wrap(txMessage));
        assertNotNull(tx);
        assertEquals(!lazy, tx.isParsed());
        assertEquals(true, tx.isCached());

        tx.getInputs().get(0).setSequenceNumber(1);
        //parent should have been uncached
        assertEquals(false, tx.isCached());
        //so should child
        assertEquals(false, tx.getInputs().get(0).isCached());

        bos = new ByteArrayOutputStream();
        bs.serialize(tx, bos);
        assertEquals(true, !Arrays.equals(txMessage, bos.toByteArray()));

      //deserialize/reserialize to check for equals.
        tx = (Transaction)bs.deserialize(ByteBuffer.wrap(txMessage));
        assertNotNull(tx);
        assertEquals(!lazy, tx.isParsed());
        assertEquals(true, tx.isCached());
        bos = new ByteArrayOutputStream();
        bs.serialize(tx, bos);
        assertEquals(true, Arrays.equals(txMessage, bos.toByteArray()));

      //deserialize/reserialize to check for equals.  Set a field to it's existing value to trigger uncache
        tx = (Transaction)bs.deserialize(ByteBuffer.wrap(txMessage));
        assertNotNull(tx);
        assertEquals(!lazy, tx.isParsed());
        assertEquals(true, tx.isCached());

        tx.getInputs().get(0).setSequenceNumber(tx.getInputs().get(0).getSequenceNumber());

        bos = new ByteArrayOutputStream();
        bs.serialize(tx, bos);
        assertEquals(true, Arrays.equals(txMessage, bos.toByteArray()));

    }

//TODO These make no sense, even in bitcoin? The message seems malformed...
/*    *//**
     * Get 1 header of the block number 1 (the first one is 0) in the chain
     *//*
    @Test
    public void testHeaders1() throws Exception {
        BitcoinSerializer bs = new BitcoinSerializer(MainNetParams.get());

        HeadersMessage hm = (HeadersMessage) bs.deserialize(ByteBuffer.wrap(Hex.decode("f9beb4d9686561" +
                "646572730000000000520000005d4fab8101010000006fe28c0ab6f1b372c1a6a246ae6" +
                "3f74f931e8365e15a089c68d6190000000000982051fd1e4ba744bbbe680e1fee14677b" +
                "a1a3c3540bf7b1cdb606e857233e0e61bc6649ffff001d01e3629900")));

        // The first block after the genesis
        // http://blockexplorer.com/b/1
        Block block = hm.getBlockHeaders().get(0);
        String hash = block.getHashAsString();
        assertEquals(hash, "00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048");

        assertNull(block.transactions);

        assertEquals(Utils.bytesToHexString(block.getMerkleRoot().getBytes()),
                "0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098");
    }


    @Test
    *//**
     * Get 6 headers of blocks 1-6 in the chain
     *//*
    public void testHeaders2() throws Exception {
        BitcoinSerializer bs = new BitcoinSerializer(MainNetParams.get());

        HeadersMessage hm = (HeadersMessage) bs.deserialize(ByteBuffer.wrap(Hex.decode("f9beb4d96865616465" +
                "72730000000000e701000085acd4ea06010000006fe28c0ab6f1b372c1a6a246ae63f74f931e" +
                "8365e15a089c68d6190000000000982051fd1e4ba744bbbe680e1fee14677ba1a3c3540bf7b1c" +
                "db606e857233e0e61bc6649ffff001d01e3629900010000004860eb18bf1b1620e37e9490fc8a" +
                "427514416fd75159ab86688e9a8300000000d5fdcc541e25de1c7a5addedf24858b8bb665c9f36" +
                "ef744ee42c316022c90f9bb0bc6649ffff001d08d2bd610001000000bddd99ccfda39da1b108ce1" +
                "a5d70038d0a967bacb68b6b63065f626a0000000044f672226090d85db9a9f2fbfe5f0f9609b387" +
                "af7be5b7fbb7a1767c831c9e995dbe6649ffff001d05e0ed6d00010000004944469562ae1c2c74" +
                "d9a535e00b6f3e40ffbad4f2fda3895501b582000000007a06ea98cd40ba2e3288262b28638cec" +
                "5337c1456aaf5eedc8e9e5a20f062bdf8cc16649ffff001d2bfee0a9000100000085144a84488e" +
                "a88d221c8bd6c059da090e88f8a2c99690ee55dbba4e00000000e11c48fecdd9e72510ca84f023" +
                "370c9a38bf91ac5cae88019bee94d24528526344c36649ffff001d1d03e4770001000000fc33f5" +
                "96f822a0a1951ffdbf2a897b095636ad871707bf5d3162729b00000000379dfb96a5ea8c81700ea4" +
                "ac6b97ae9a9312b2d4301a29580e924ee6761a2520adc46649ffff001d189c4c9700")));

        int nBlocks = hm.getBlockHeaders().size();
        assertEquals(nBlocks, 6);

        // index 0 block is the number 1 block in the block chain
        // http://blockexplorer.com/b/1
        Block zeroBlock = hm.getBlockHeaders().get(0);
        String zeroBlockHash = zeroBlock.getHashAsString();

        assertEquals("00000000839a8e6886ab5951d76f411475428afc90947ee320161bbf18eb6048",
                zeroBlockHash);
        assertEquals(zeroBlock.getNonce(), 2573394689L);


        Block thirdBlock = hm.getBlockHeaders().get(3);
        String thirdBlockHash = thirdBlock.getHashAsString();

        // index 3 block is the number 4 block in the block chain
        // http://blockexplorer.com/b/4
        assertEquals("000000004ebadb55ee9096c9a2f8880e09da59c0d68b1c228da88e48844a1485",
                thirdBlockHash);
        assertEquals(thirdBlock.getNonce(), 2850094635L);
    }*/

    @Test
    public void testBitcoinPacketHeader() {
        try {
            new BitcoinSerializer.BitcoinPacketHeader(ByteBuffer.wrap(new byte[]{0}));
            fail();
        } catch (BufferUnderflowException e) {
        }

        // Message with a Message size which is 1 too big, in little endian format.
        byte[] wrongMessageLength = Hex.decode("000000000000000000000000010000020000000000");
        try {
            new BitcoinSerializer.BitcoinPacketHeader(ByteBuffer.wrap(wrongMessageLength));
            fail();
        } catch (ProtocolException e) {
            // expected
        }
    }

    @Test
    public void testSeekPastMagicBytes() {
        // Fail in another way, there is data in the stream but no magic bytes.
        byte[] brokenMessage = Hex.decode("000000");
        try {
            new BitcoinSerializer(MainNetParams.get()).seekPastMagicBytes(ByteBuffer.wrap(brokenMessage));
            fail();
        } catch (BufferUnderflowException e) {
            // expected
        }
    }

    @Test
    /**
     * Tests serialization of an unknown message.
     */
    public void testSerializeUnknownMessage() {
        BitcoinSerializer bs = new BitcoinSerializer(MainNetParams.get());

        UnknownMessage a = new UnknownMessage();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(addrMessage.length);
        try {
            bs.serialize(a, bos);
            fail();
        } catch (Throwable e) {
        }
    }

    /**
     * Unknown message for testSerializeUnknownMessage.
     */
    class UnknownMessage extends Message {
        @Override
        void parse() throws ProtocolException {
        }

        @Override
        protected void parseLite() throws ProtocolException {
        }
    }

}
