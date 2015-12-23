package org.wowdoge;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import com.google.dogecoin.core.NetworkParameters;
import com.google.dogecoin.core.Utils;
import com.google.dogecoin.uri.BitcoinURI;
import com.google.dogecoin.uri.BitcoinURIParseException;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

public class ButtonQRScan extends JButton {
	private String scannedQRText;
	private List _listeners = new ArrayList();
	
	public ButtonQRScan(String title) {
		super(title);
		
		final ButtonQRScan b = this;
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DialogWebcamQRScan d = new DialogWebcamQRScan();
				d.setLocationRelativeTo(b);
				if (d.showDialog()) {
					scannedQRText = d.getScannedQRCodeText();
					Image img = d.getScannedImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH);
					// Create a buffered image with transparency
					BufferedImage i = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				    
				    // Draw the image on to the buffered image
				    Graphics2D bGr = i.createGraphics();
				    bGr.drawImage(img, 0, 0, null);
				    bGr.dispose();
				    
				    setIcon(new ImageIcon(i));
				    fireEvent();
				}
				d.dispose();
			}
		});
		
		setTransferHandler(new TransferHandler(  ){
		      @Override
		      public boolean importData( JComponent comp, Transferable aTransferable ) {
		        try {
		          if (aTransferable.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.imageFlavor)) {
		        	  Object transferData = aTransferable.getTransferData( DataFlavor.imageFlavor );
			          scanQR((BufferedImage) transferData);
			          //btnDragOrPaste.setIcon( new ImageIcon( ( Image ) transferData ) );  
		          } else if (aTransferable.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor)) {
                      List<File> fileList = (List<File>) aTransferable.getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
                      Iterator<File> iterator = fileList.iterator();
                      if (iterator.hasNext()) {
                          File file = iterator.next();
                          scanQR(ImageIO.read(file));
                          //btnDragOrPaste.setIcon(new ImageIcon(file.getAbsolutePath()));
                      }
		          }
		        } catch ( UnsupportedFlavorException e ) {
		        	e.printStackTrace();
		        } catch ( IOException e ) {
		        	e.printStackTrace();
		        } catch (ChecksumException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ReaderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        return true;
		      }

		      @Override
		      public boolean canImport( JComponent comp, DataFlavor[] transferFlavors ) {
		        return true;
		      }
		    });
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(this, popupMenu);
		{
			JMenuItem mntmPaste = new JMenuItem("Paste");
			mntmPaste.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					/* -- get system clipboard */

					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

					/* -- get clipboard context */

					Transferable data = clipboard.getContents(null);

					/* -- is context string type ? */

					boolean bIsImage = ( ( data != null ) && ( data.isDataFlavorSupported( DataFlavor.imageFlavor ) ) );

					/* -- if yes, translate context to string type and write it */

					if ( bIsImage ) {

					  try {
						BufferedImage i = (BufferedImage)data.getTransferData( DataFlavor.imageFlavor );
						scanQR(i);
					  } catch (UnsupportedFlavorException ex) {
					    ex.printStackTrace();
					  } catch (IOException ex) {
						  ex.printStackTrace();
					  } catch (ReaderException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					  }
					}
				}
			});
			popupMenu.add(mntmPaste);
		}
	}
	
	public synchronized void addEventListener(EventQRScannedListener listener) {
		_listeners.add(listener);
	}

	public synchronized void removeEventListener(EventQRScannedListener listener) {
		_listeners.remove(listener);
	}

	// call this method whenever you want to notify
	// the event listeners of the particular event
	private synchronized void fireEvent() {
		EventQRScanned event = new EventQRScanned(this);
		Iterator i = _listeners.iterator();
		while (i.hasNext()) {
			((EventQRScannedListener) i.next()).handleEventQRScanned(event);
		}
	}
	
	private void scanQR(BufferedImage i) throws ReaderException, ChecksumException, FormatException {
		Image img = i.getScaledInstance(200, -1, Image.SCALE_SMOOTH);
		// Create a buffered image with transparency
	    i = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    
	    // Draw the image on to the buffered image
	    Graphics2D bGr = i.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
	    
	    setIcon(new ImageIcon(i));
	    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(i)));
	    //Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(2);
        //hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
	    //Vector decodeFormats = new Vector<BarcodeFormat>();
	    //decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
	    //Hashtable hints = new Hashtable<DecodeHintType, Object>(3);
	    //hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
	    Result result = new QRCodeReader().decode(binaryBitmap);//, hints);
	    //System.out.println("QR Code : "+result.getText());
	    scannedQRText = result.getText();
	    fireEvent();
	    //System.out.println( s );
	}
	
	public String getScannedQRText() {
		return scannedQRText;
	}
	
	public boolean isURL() {
		//vertcoin://VusUcH7P6RanH6yBmj37dok2wR8JKLeYwU?amount=0.0&label=Donation
		return scannedQRText.toLowerCase().startsWith(Version.coinNameLongSmall + ":");
	}
	
	public String getAddress(NetworkParameters p) throws BitcoinURIParseException {
		if (isURL()) {
			BitcoinURI uri = new BitcoinURI(p, scannedQRText);
			return uri.getAddress().toString();
		} else {
			throw new BitcoinURIParseException(scannedQRText);
		}
	}
	
	public String getAmount(NetworkParameters p) throws BitcoinURIParseException {
		if (isURL()) {
			BitcoinURI uri = new BitcoinURI(p, scannedQRText);
			return Utils.bitcoinValueToFriendlyString(uri.getAmount());
		} else {
			throw new BitcoinURIParseException(scannedQRText);
		}
	}
	
	public String getLabelNameDescription (NetworkParameters p) throws BitcoinURIParseException {
		if (isURL()) {
			BitcoinURI uri = new BitcoinURI(p, scannedQRText);
			return uri.getLabel();
		} else {
			throw new BitcoinURIParseException(scannedQRText);
		}
	}
	
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
