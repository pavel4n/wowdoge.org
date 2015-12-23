/**
 * Copyright 2014 wowdoge.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wowdoge;

import com.google.dogecoin.crypto.KeyCrypter;
import com.google.common.util.concurrent.Service.State;
import com.google.dogecoin.core.Wallet;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.bitcoinj.wallet.Protos.Wallet.EncryptionType;
import org.spongycastle.crypto.params.KeyParameter;
import org.wowdoge.crypto.KeyCrypterOpenSSL;
import org.wowdoge.file.PrivateKeyAndDate;
import org.wowdoge.file.PrivateKeysHandler;
import org.wowdoge.file.PrivateKeysHandlerException;

import com.google.dogecoin.core.AbstractPeerEventListener;
import com.google.dogecoin.core.AbstractWalletEventListener;
import com.google.dogecoin.core.Address;
import com.google.dogecoin.core.Block;
import com.google.dogecoin.core.ECKey;
import com.google.dogecoin.core.InsufficientMoneyException;
import com.google.dogecoin.core.Message;
import com.google.dogecoin.core.NetworkParameters;
import com.google.dogecoin.core.Peer;
import com.google.dogecoin.core.ScriptException;
import com.google.dogecoin.core.Transaction;
import com.google.dogecoin.core.TransactionInput;
import com.google.dogecoin.core.TransactionOutput;
import com.google.dogecoin.core.Utils;
//import com.google.dogecoin.kits.WalletAppKit;
import com.google.dogecoin.params.MainNetParams;
import com.google.dogecoin.utils.Threading;
import com.google.dogecoin.wallet.WalletTransaction;
import com.google.dogecoin.crypto.KeyCrypterException;

public class CoreWallet {

	private CoreWalletAppKit appKit = null;
	private boolean dirty = false;
	private int synchronizing = 0;
	private String walletFilePath;
	private Preferences preferences;
	private Map <String, String> mapAddressName;
	//private String applicationDataDirectory;

	public static void main(String[] args) throws Exception {
		new CoreWallet().run();
	}
	
	public CoreWallet() {
		preferences = Preferences.userRoot().node(Version.preferencesName); //org.wowdoge //this.getClass().getName());
	}
	
	public void run() throws Exception {
		if (getWalletFilePath() == null)
			run(new File("."), Version.defaultWalletFileName);
		else {
			File f = new File(getWalletFilePath());
			run(f.getParentFile(), f.getName());
		}
	}

	public void run(final File directory, final String fileName) throws Exception {
		dirty = false;
		synchronizing = 0;
		NetworkParameters params = MainNetParams.get();
		
		walletFilePath = new File(directory, fileName).getAbsolutePath();
		String spvFilePath = getSPVFilePath(new File(directory,Version.spvChainFileName).getAbsolutePath()); //dogecoins.dogespvchain
		System.out.println("SPVFilePath: " + spvFilePath);
		boolean exists = new File(walletFilePath).exists();
		
		// If wallet files does not exist, try to create wallet file to make sure it is in writable location. Before doing anything else. 
		if (!exists) {
			File temp = new File(directory, fileName);
			Wallet vWallet = new Wallet(params);
			vWallet.saveToFile(temp);
		}
		
		appKit = new CoreWalletAppKit(params, directory, fileName, spvFilePath) {
			
//			protected void addWalletExtensions() throws Exception {
//				File temp = new File(vWalletFile.getParentFile(), "TEMP" + vWalletFile.getName());
//				vWallet.saveToFile(temp, vWalletFile);
//			}
			
			@Override
			protected void onSetupCompleted() {
				if (wallet().getKeychainSize() < 1) {
					ECKey key = new ECKey();
					wallet().addKey(key);
				}
				
				setWalletFilePath(walletFilePath);
				
				peerGroup().setConnectTimeoutMillis(1000);
				//peerGroup().setFastCatchupTimeSecs(0);//wallet().getEarliestKeyCreationTime());
				
				System.out.println(appKit.wallet());
				
				peerGroup().addEventListener(new AbstractPeerEventListener() {
					@Override
					public void onPeerConnected(Peer peer, int peerCount) {
						super.onPeerConnected(peer, peerCount);
						System.out.println(String.format("onPeerConnected: %s %s",peer,peerCount));
					}
					@Override
					public void onPeerDisconnected(Peer peer, int peerCount) {
						super.onPeerDisconnected(peer, peerCount);
						System.out.println(String.format("onPeerDisconnected: %s %s",peer,peerCount));
					}
					@Override public void onBlocksDownloaded(Peer peer, Block block, int blocksLeft) {
						super.onBlocksDownloaded(peer, block, blocksLeft);
						synchronizing = blocksLeft;
						System.out.println(String.format("%s blocks left (downloaded %s)",blocksLeft,block.getHashAsString()));
					}
					
					@Override public Message onPreMessageReceived(Peer peer, Message m) {
						System.out.println(String.format("%s -> %s",peer,m.getClass()));
						return super.onPreMessageReceived(peer, m);
					}
				},Threading.SAME_THREAD);
				
				wallet().addEventListener(new AbstractWalletEventListener() {
		            @Override
		            public void onWalletChanged(Wallet wallet) {
		            	dirty = true;
		            }
		            
		            @Override
		            public void onCoinsReceived(Wallet wallet, Transaction tx, java.math.BigInteger prevBalance, java.math.BigInteger newBalance) {
		            	playSoundFile("/org/wowdoge/coins-drop-1.wav");
		            }
		            
		            @Override
		            public void onKeysAdded(Wallet wallet, java.util.List<ECKey> keys) {
		            	dirty = true;
		            }
				});
				dirty = true;
			}
		};
		
		if (!exists)
			appKit.setCheckpoints(CoreWallet.class.getResourceAsStream(Version.checkpointsFileName));
		
		//appKit.saveWallet();
		
		appKit.setBlockingStartup(false);
		appKit.startAndWait(); //startAndWait();
	}
	
	protected void readAddressMap() {
		//getWallet().setDescription(null);
		if (getWallet().getDescription() == null) {
			//getWallet().setDescription("");
			mapAddressName = new HashMap<String, String>();
		} else {
			XMLDecoder xmlDecoder = new XMLDecoder(new ByteArrayInputStream(getWallet().getDescription().getBytes()));
			mapAddressName = (Map<String, String>) xmlDecoder.readObject();

			/*
			for (String key : map.keySet()) {
				Assert.assertEquals(mapAddressName.get(key), map.get(key));
			}
			 */
		}
	}
	
	protected void writeAddressMap() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		XMLEncoder xmlEncoder = new XMLEncoder(bos);
		xmlEncoder.writeObject(mapAddressName);
		xmlEncoder.close(); //flush();

		String serializedMap = bos.toString();
		//System.out.println(serializedMap);
		getWallet().setDescription(serializedMap);
	}
	
	public String getName(String address) {
		if (mapAddressName == null)
			readAddressMap();
		
		return mapAddressName.get(address);
	}
	
	public void putName(String address, String name) {
		if (mapAddressName == null)
			readAddressMap(); //mapAddressName = new HashMap<String, String>();
		
		mapAddressName.put(address, name);
		writeAddressMap();
	}
	
	public Preferences getPreferences() {
		return preferences;
	}
	
	public String getWalletFilePath() {
		walletFilePath = preferences.get("walletFilePath", null);
		return walletFilePath;
	}
	
	public void setWalletFilePath(String path) {
		preferences.put("walletFilePath", path);
		walletFilePath = path;
	}
	
	public void removeWalletFilePath() {
		preferences.remove("walletFilePath");
	}
	
	public String getSPVFilePath(String path) {
		path = preferences.get("spvFilePath", path);
		preferences.put("spvFilePath", path);
		return path;
	}

	public void stop() {
		appKit.stopAndWait();
	}
	
//	public void shutDown() {
//		try {
//			appKit.stopAndWait();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		try {
//			appKit.store().close();
//		} catch (Exception e) {
//		}
//		appKit.reset();
//	}
	
	public void open(File f) throws Exception {
		stop();
		run(f.getParentFile(), f.getName());
	}
	
	public boolean isDirty() {
		if (appKit == null)
			return false;
		if (appKit.state() == State.STOPPING || appKit.state() == State.TERMINATED)
			return false;
		else
			return dirty;
	}
	
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public NetworkParameters getNetworkParameters() {
		return appKit.wallet().getNetworkParameters();
    }
	
	public Wallet getWallet() {
		return appKit.wallet();
	}
	
	public boolean isEncrypted() {
		return appKit.wallet().isEncrypted();
	}
	
	public void encrypt(CharSequence password) throws KeyCrypterException {
		appKit.wallet().encrypt(password);
	}
	
	public void decrypt(CharSequence password) throws KeyCrypterException {
		appKit.wallet().decrypt(appKit.wallet().getKeyCrypter().deriveKey(password));
	}
	
	public void createNewKeys(int number) throws KeyCrypterException {
		createNewKeys(number, null);
	}
	
	//System.out.println("keyProto.getPrivateKey().toByteArray() :" + keyProto.getPrivateKey());
	//System.out.println("keyProto.getEncryptedPrivateKey(): " + keyProto.getEncryptedPrivateKey());
	public void createNewKeys(int number, final CharSequence walletPassword) throws KeyCrypterException {
		List<ECKey> keys = new ArrayList<ECKey>();
		for (int i = 0; i < number; i++) {
			ECKey key = new ECKey();
			if (isEncrypted()) {
				final KeyCrypter walletKeyCrypter = getWallet().getKeyCrypter();
				KeyParameter aesKey = walletKeyCrypter.deriveKey(walletPassword);
				key = key.encrypt(walletKeyCrypter, aesKey);
			}
			keys.add(key);
		}
		appKit.wallet().addKeys(keys);
	}
	
	public java.util.List<ECKey> getKeys() {
		return appKit.wallet().getKeys();
	}
	
//	public java.util.List<ECKey> getKeys(CharSequence password) throws KeyCrypterException {
//		if (password != null)
//			decrypt(password);
//		List<ECKey> keys = appKit.wallet().getKeys();
//		if (password != null)
//			encrypt(password);
//		return keys;
//	}
	
	public ECKey getDecryptedKey(int index, CharSequence password) throws KeyCrypterException {
		List<ECKey> keys = appKit.wallet().getKeys();
		ECKey key = keys.get(index);
		if (key.isEncrypted()) {
			final KeyCrypter walletKeyCrypter = key.getKeyCrypter();
			KeyParameter aesKey = walletKeyCrypter.deriveKey(password);
			key = key.decrypt(key.getKeyCrypter(), aesKey);
		}
		return key;
	}
	
	public void exportPrivateKeysToFile(String fileName, CharSequence password, boolean performEncryptionOfExportFile, CharSequence exportPassword) throws IOException, KeyCrypterException, PrivateKeysHandlerException {
		PrivateKeysHandler h = new PrivateKeysHandler(getNetworkParameters());
		h.exportPrivateKeys(new File(fileName), getWallet(), appKit.chain(), performEncryptionOfExportFile, exportPassword, password);
		
//		boolean isEncrypted = isEncrypted();
//		if (isEncrypted)
//			decrypt(password);
//		try {
//			PrintWriter out = new PrintWriter(fileName);
//			List<ECKey> keys = appKit.wallet().getKeys();
//			for (ECKey k : keys) {
//				out.println(k.getPrivateKeyEncoded(getNetworkParameters())
//						.toString());
//			}
//			out.close();
//		} finally {
//			if (isEncrypted)
//				encrypt(password);
//		}
	}
	
	public void importPrivateKeys(Collection<PrivateKeyAndDate> privateKeyAndDateArray, CharSequence walletPassword) throws KeyCrypterException, PrivateKeysHandlerException, Exception {
		boolean keyEncryptionRequired = false;
		Collection<byte[]> unencryptedWalletPrivateKeys = new ArrayList<byte[]>();
        Date earliestTransactionDate = new Date(org.wowdoge.utils.DateUtils.nowUtc().getMillis());
        if (appKit.wallet().getEncryptionType() != EncryptionType.UNENCRYPTED) {
            keyEncryptionRequired = true;
        }
        
        try {
        	// Work out what the unencrypted private keys are.
            KeyCrypter walletKeyCrypter = appKit.wallet().getKeyCrypter();
            KeyParameter aesKey = null;
            if (keyEncryptionRequired) {
                if (walletKeyCrypter == null) {
                    System.err.println("Missing KeyCrypter. Could not decrypt private keys.");
                }
                aesKey = walletKeyCrypter.deriveKey(CharBuffer.wrap(walletPassword));
            }
            
            for (ECKey ecKey : getKeys()) {
                if (keyEncryptionRequired) {
                    if (ecKey.getEncryptedPrivateKey() == null
                            || ecKey.getEncryptedPrivateKey().getEncryptedBytes() == null
                            || ecKey.getEncryptedPrivateKey().getEncryptedBytes().length == 0) {

                    	System.err.println("Missing encrypted private key bytes for key " + ecKey.toString()
                                + ", enc.priv = "
                                + Utils.bytesToHexString(ecKey.getEncryptedPrivateKey().getEncryptedBytes()));
                    } else {
                        byte[] decryptedPrivateKey = ecKey.getKeyCrypter().decrypt(
                                ecKey.getEncryptedPrivateKey(), aesKey);
                        unencryptedWalletPrivateKeys.add(decryptedPrivateKey);
                    }

                } else {
                    // Wallet is not encrypted.
                    unencryptedWalletPrivateKeys.add(ecKey.getPrivKeyBytes());
                }
            }
            
         // Keep track of earliest transaction date go backwards from now.
            if (privateKeyAndDateArray != null) {
                for (PrivateKeyAndDate privateKeyAndDate : privateKeyAndDateArray) {
                    ECKey keyToAdd = privateKeyAndDate.getKey();
                    if (keyToAdd != null) {
                        if (privateKeyAndDate.getDate() != null) {
                            keyToAdd.setCreationTimeSeconds(privateKeyAndDate.getDate().getTime()
                                    / 1000); //NUMBER_OF_MILLISECONDS_IN_A_SECOND
                        }

                        if (!keyChainContainsPrivateKey(unencryptedWalletPrivateKeys, keyToAdd, walletPassword)) {
                            if (keyEncryptionRequired) {
                                ECKey encryptedKey = new ECKey(walletKeyCrypter.encrypt(
                                        keyToAdd.getPrivKeyBytes(), aesKey), keyToAdd.getPubKey(),
                                        walletKeyCrypter);
                                appKit.wallet().addKey(encryptedKey);
                            } else {
                            	appKit.wallet().addKey(keyToAdd);
                            }

                            // Update earliest transaction date.
                            if (privateKeyAndDate.getDate() == null) {
                                // Need to go back to the genesis block.
                                earliestTransactionDate = null;
                            } else {
                                if (earliestTransactionDate != null) {
                                    earliestTransactionDate = earliestTransactionDate.before(privateKeyAndDate
                                            .getDate()) ? earliestTransactionDate : privateKeyAndDate.getDate();
                                }
                            }
                        }
                    }
                }
            }
        } finally {
        	// Wipe the work collection of private key bytes to remove it from memory.
            for (byte[] privateKeyBytes : unencryptedWalletPrivateKeys) {
                if (privateKeyBytes != null) {
                    for (int i = 0; i < privateKeyBytes.length; i++) {
                        privateKeyBytes[i] = 0;
                    }
                }
            }
        }
	}
	
    /**
     * Determine whether the key is already in the wallet.
     * @throws KeyCrypterException
     */
    private boolean keyChainContainsPrivateKey(Collection<byte[]> unencryptedPrivateKeys, ECKey keyToAdd, CharSequence walletPassword) throws KeyCrypterException {
        if (unencryptedPrivateKeys == null || keyToAdd == null) {
            return false;
        } else {
            byte[] unencryptedKeyToAdd = new byte[0];
            if (keyToAdd.isEncrypted()) {
                unencryptedKeyToAdd = keyToAdd.getKeyCrypter().decrypt(keyToAdd.getEncryptedPrivateKey(), keyToAdd.getKeyCrypter().deriveKey(walletPassword));
            }
            for (byte[] loopEncryptedPrivateKey : unencryptedPrivateKeys) { 
                if (Arrays.equals(unencryptedKeyToAdd, loopEncryptedPrivateKey)) {
                    return true;
                }
            }
            return false;
        }
    }
	
	public Collection<PrivateKeyAndDate> readInPrivateKeysFromFile(String fileName, CharSequence privateKeysFilePassword) throws IOException, KeyCrypterException, PrivateKeysHandlerException {
		PrivateKeysHandler h = new PrivateKeysHandler(getNetworkParameters());
		Collection<PrivateKeyAndDate> p = h.readInPrivateKeys(new File(fileName), privateKeysFilePassword);
		return p;
	}
	
	public boolean isImportedPrivateKeyFileEncrypted(File file) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
        String firstLine = reader.readLine();

        return (firstLine != null && firstLine.startsWith(new KeyCrypterOpenSSL().getOpenSSLMagicText()));
		} finally {
			reader.close();
		}
	}
	
	public void sync() throws Exception { //Date from
//		System.out.println("About to restart PeerGroup.");   
//		appKit.peerGroup().stopAndWait();
//		System.out.println("PeerGroup is now stopped.");
//		// Close the blockstore and recreate a new one.
//		appKit.store()
		//synchronizing = 0;
		
		stop();
		File f = new File(getWalletFilePath());
		String spvFilePath = getSPVFilePath(new File(f.getParentFile(), Version.spvChainFileName).getAbsolutePath()); //"dogecoins.dogespvchain"
		new File(spvFilePath).delete();
		run();
	}
	
	public java.math.BigInteger getBalance() {
		return appKit.wallet().getBalance();
	}
	
	public java.lang.Iterable<WalletTransaction> getWalletTransactions() {
		return appKit.wallet().getWalletTransactions();
	}
	
	public List<Transaction> getTransactionsByTime() {
		return appKit.wallet().getTransactionsByTime();
	}
	
	public BigInteger getBalance(String address) { //ECKey key) {
		BigInteger balance = BigInteger.ZERO;
		List<Transaction> transactions = getTransactionsByTime();
		for (Transaction t: transactions) {
			BigInteger value = t.getValue(appKit.wallet());
			if (value.compareTo(BigInteger.ZERO) < 0) {
				List<TransactionInput> ins = t.getInputs();
    			for (TransactionInput i : ins) {
    				try {
    					TransactionOutput output = i.getOutpoint().getConnectedOutput();
    					if (i.getFromAddress().toString().equals(address)) {
    						balance = balance.subtract(output.getValue());
    					}
    				} catch (ScriptException e) {
    					e.printStackTrace();
    					//tempText = tempText + "COINBASE" + " ";
    				}
    			}
    			List<TransactionOutput> outs = t.getOutputs();
    			for (TransactionOutput o : outs) {
    				if  (o.getScriptPubKey().getToAddress(o.getParams()).toString().equals(address)) {
    					balance = balance.add(o.getValue());
    				}
    			}
			} else {
				List<TransactionOutput> outs = t.getOutputs();
    			for (TransactionOutput o : outs) {
    				if  (o.getScriptPubKey().getToAddress(o.getParams()).toString().equals(address)) {
    					balance = balance.add(o.getValue());
    				}
    			}
			}
		}
		return balance;
	}
	
	public Wallet.SendResult sendCoins(Address address, float amount) throws InsufficientMoneyException {
		BigInteger value = Utils.toNanoCoins(new Float(amount).toString());
		// Send with a small fee attached to ensure rapid confirmation.
		final BigInteger amountToSend = value; //value.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
		final Wallet.SendResult sendResult = appKit.wallet().sendCoins(appKit.peerGroup(), address, amountToSend);
		return sendResult;
	}
	
	public Wallet.SendResult sendCoins(Address address, float amount, CharSequence password) throws InsufficientMoneyException {
		BigInteger value = Utils.toNanoCoins(new Float(amount).toString());
		// Send with a small fee attached to ensure rapid confirmation.
		final BigInteger amountToSend = value; //value.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
		// Make sure this code is run in a single thread at once.
		Wallet.SendRequest request = Wallet.SendRequest.to(address, amountToSend);
		// The SendRequest object can be customized at this point to modify how the transaction will be created.
		request.aesKey = appKit.wallet().getKeyCrypter().deriveKey(password);
		final Wallet.SendResult sendResult = appKit.wallet().sendCoins(appKit.peerGroup(), request);
		return sendResult;
	}

	public Wallet.SendResult sendAll(Address address) throws InsufficientMoneyException {
		Wallet.SendRequest request = Wallet.SendRequest.emptyWallet(address);
		// The SendRequest object can be customized at this point to modify how the transaction will be created.
		final Wallet.SendResult sendResult = appKit.wallet().sendCoins(appKit.peerGroup(), request);
		return sendResult;
	}
	
	public Wallet.SendResult sendAll(Address address, CharSequence password) throws InsufficientMoneyException {
		Wallet.SendRequest request = Wallet.SendRequest.emptyWallet(address);
		// The SendRequest object can be customized at this point to modify how the transaction will be created.
		request.aesKey = appKit.wallet().getKeyCrypter().deriveKey(password);
		final Wallet.SendResult sendResult = appKit.wallet().sendCoins(appKit.peerGroup(), request);
		return sendResult;
	}
	
	public boolean checkPassword(CharSequence password) {
		return appKit.wallet().checkAESKey(appKit.wallet().getKeyCrypter().deriveKey(password));
	}
	
	public void saveToFile(java.io.File temp, java.io.File destFile) throws java.io.IOException {
		appKit.wallet().saveToFile(temp, destFile);
	}
	
	public final State state() {
		if (appKit != null)
			return appKit.state();
		else
			return State.NEW;
	}
	
	public final boolean isRunning() {
		if (appKit != null)
			return appKit.isRunning();
		else
			return false;
	}
	
	public final int isSynchronizing() {
		return synchronizing;
	}
	
	public final boolean isStoreFileLocked() {
		if (appKit != null)
			return appKit.isStoreFileLocked();
		else
			return false;
	}
	
	public void playSoundFile(String soundFilePath) {
		try
	    {
	        Clip clip = AudioSystem.getClip();
	        clip.open(AudioSystem.getAudioInputStream(CoreWallet.class.getResourceAsStream(soundFilePath)));//new File(soundFilePath)));
	        clip.start();
	    }
	    catch (Exception exc)
	    {
	        exc.printStackTrace();
	    }
	}
}
