package com.google.dogecoin.examples;

import com.google.dogecoin.core.*;
import com.google.dogecoin.kits.WalletAppKit;
import com.google.dogecoin.params.RegTestParams;
import com.google.dogecoin.utils.BriefLogFormatter;
import com.google.dogecoin.utils.Threading;

import java.io.File;
import java.math.BigInteger;

/**
 * This is a little test app that waits for a coin on a local regtest node, then  generates two transactions that double
 * spend the same output and sends them. It's useful for testing double spend codepaths but is otherwise not something
 * you would normally want to do.
 */
public class DoubleSpend {
    public static void main(String[] args) throws Exception {
        BriefLogFormatter.init();
        final RegTestParams params = RegTestParams.get();
        WalletAppKit kit = new WalletAppKit(params, new File("."), "doublespend");
        kit.connectToLocalHost();
        kit.setAutoSave(false);
        kit.startAndWait();
        //kit.startAsync();
        //kit.awaitRunning();

        System.out.println(kit.wallet());

        kit.wallet().getBalanceFuture(Utils.COIN, Wallet.BalanceType.AVAILABLE).get();
        Transaction tx1 = kit.wallet().createSend(new Address(params, "muYPFNCv7KQEG2ZLM7Z3y96kJnNyXJ53wm"), Utils.CENT);
        Transaction tx2 = kit.wallet().createSend(new Address(params, "muYPFNCv7KQEG2ZLM7Z3y96kJnNyXJ53wm"), Utils.CENT.add(BigInteger.TEN));
        final Peer peer = kit.peerGroup().getConnectedPeers().get(0);
        peer.addEventListener(new AbstractPeerEventListener() {
            @Override
            public Message onPreMessageReceived(Peer peer, Message m) {
                System.err.println("Got a message!" + m.getClass().getSimpleName() + ": " + m);
                return m;
            }
        }, Threading.SAME_THREAD);
        peer.sendMessage(tx1);
        peer.sendMessage(tx2);

        Thread.sleep(5000);
        kit.startAndWait();
        //kit.stopAsync();
        //kit.awaitTerminated();
    }
}
