package org.wowdoge;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.TransferHandler;

import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JSpinner;

import java.awt.Dimension;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;

import com.google.dogecoin.core.AddressFormatException;
import com.google.dogecoin.core.DumpedPrivateKey;
import com.google.dogecoin.core.ECKey;
import com.google.dogecoin.core.NetworkParameters;
import com.google.dogecoin.params.MainNetParams;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Hashtable;

import javax.swing.JTextArea;

import java.awt.SystemColor;
import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.JPopupMenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

public class DialogImportPrivate extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private boolean dialogResultOK = false;
	private JSpinner spinnerSeconds;
	private JSpinner spinnerDateAndTime;
	private NetworkParameters params;
	private JTextField textFieldAddress;
	private JCheckBox chckbxResynchronize;
	private ButtonQRScan btnDragOrPaste;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogImportPrivate dialog = new DialogImportPrivate(new MainNetParams());
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogImportPrivate(NetworkParameters p) {
		params = p;
		setTitle("Import Paper Wallet Private Key");
		setBounds(100, 100, 539, 723);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[]{476, 0};
			gbl_panel.rowHeights = new int[] {28, 28, 0, 100, 0, 0, 0, 0, 0, 28, 28};
			gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
			gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
			panel.setLayout(gbl_panel);
			{
				JLabel lblNewLabel = new JLabel("Private Key");
				GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
				gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
				gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
				gbc_lblNewLabel.gridx = 0;
				gbc_lblNewLabel.gridy = 0;
				panel.add(lblNewLabel, gbc_lblNewLabel);
				lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			}
			{
				textField = new TextField();
				textField.getDocument().addDocumentListener(new DocumentListener() {
					  public void changedUpdate(DocumentEvent e) {
						    warn();
						  }
						  public void removeUpdate(DocumentEvent e) {
						    warn();
						  }
						  public void insertUpdate(DocumentEvent e) {
						    warn();
						  }

						  public void warn() {
								//System.out.println(textField.getText());
								try {
									ECKey key = getECKey();
									textFieldAddress.setText(key.toAddress(
											params).toString());
								} catch (AddressFormatException e) {
									textFieldAddress.setText("Not correct private key yet entered!");
								}
							}
						});
				textField.addInputMethodListener(new InputMethodListener() {
					public void caretPositionChanged(InputMethodEvent arg0) {
					}
					public void inputMethodTextChanged(InputMethodEvent arg0) {
					}
				});
				textField.setHorizontalAlignment(SwingConstants.CENTER);
				GridBagConstraints gbc_textField = new GridBagConstraints();
				gbc_textField.fill = GridBagConstraints.BOTH;
				gbc_textField.insets = new Insets(0, 0, 5, 0);
				gbc_textField.gridx = 0;
				gbc_textField.gridy = 1;
				panel.add(textField, gbc_textField);
				textField.setColumns(10);
			}
			{
				JButton btnScanQrCode = new JButton("Scan QR Code via Webcam");
				btnScanQrCode.setVisible(false);
				GridBagConstraints gbc_btnScanQrCode = new GridBagConstraints();
				gbc_btnScanQrCode.insets = new Insets(0, 0, 5, 0);
				gbc_btnScanQrCode.gridx = 0;
				gbc_btnScanQrCode.gridy = 2;
				panel.add(btnScanQrCode, gbc_btnScanQrCode);
			}
			{
				JScrollPane scrollPane = new JScrollPane();
				scrollPane.setMaximumSize(new Dimension(32767, 600));
				GridBagConstraints gbc_scrollPane = new GridBagConstraints();
				gbc_scrollPane.fill = GridBagConstraints.BOTH;
				gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
				gbc_scrollPane.gridx = 0;
				gbc_scrollPane.gridy = 3;
				panel.add(scrollPane, gbc_scrollPane);
				{
					JPanel panelDragOrPaste = new JPanel();
					panelDragOrPaste.setMaximumSize(new Dimension(32767, 600));
					scrollPane.setViewportView(panelDragOrPaste);
					panelDragOrPaste.setLayout(new BorderLayout(0, 0));
					{
						btnDragOrPaste = new ButtonQRScan("Click to Scan QR Code via Webcam or\nDrop or Paste QR Code Image Here...");
						btnDragOrPaste.setVerticalTextPosition(SwingConstants.BOTTOM);
						btnDragOrPaste.setHorizontalTextPosition(SwingConstants.CENTER);
						panelDragOrPaste.add(btnDragOrPaste, BorderLayout.CENTER);
						{
						
						}
					}
					btnDragOrPaste.addEventListener(new EventQRScannedListener() {

						@Override
						public void handleEventQRScanned(EventObject e) {
							textField.setText(btnDragOrPaste.getScannedQRText());
						}
						
					});
				}
			}
			{
				JLabel lblAddress = new JLabel("Address");
				GridBagConstraints gbc_lblAddress = new GridBagConstraints();
				gbc_lblAddress.insets = new Insets(0, 0, 5, 0);
				gbc_lblAddress.gridx = 0;
				gbc_lblAddress.gridy = 4;
				panel.add(lblAddress, gbc_lblAddress);
			}
			{
				textFieldAddress = new TextField();
				textFieldAddress.setHorizontalAlignment(SwingConstants.CENTER);
				textFieldAddress.setBackground(SystemColor.window);
				textFieldAddress.setEditable(false);
				GridBagConstraints gbc_textFieldAddress = new GridBagConstraints();
				gbc_textFieldAddress.insets = new Insets(0, 0, 5, 0);
				gbc_textFieldAddress.fill = GridBagConstraints.HORIZONTAL;
				gbc_textFieldAddress.gridx = 0;
				gbc_textFieldAddress.gridy = 5;
				panel.add(textFieldAddress, gbc_textFieldAddress);
				textFieldAddress.setColumns(10);
			}
			{
				Component verticalStrut = Box.createVerticalStrut(20);
				GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
				gbc_verticalStrut.insets = new Insets(0, 0, 5, 0);
				gbc_verticalStrut.gridx = 0;
				gbc_verticalStrut.gridy = 6;
				panel.add(verticalStrut, gbc_verticalStrut);
			}
			{
				JLabel lblSecondsFormat = new JLabel("Private Key Creation Date and Time in Seconds");
				lblSecondsFormat.setHorizontalAlignment(SwingConstants.CENTER);
				GridBagConstraints gbc_lblSecondsFormat = new GridBagConstraints();
				gbc_lblSecondsFormat.fill = GridBagConstraints.BOTH;
				gbc_lblSecondsFormat.insets = new Insets(0, 0, 5, 0);
				gbc_lblSecondsFormat.gridx = 0;
				gbc_lblSecondsFormat.gridy = 7;
				panel.add(lblSecondsFormat, gbc_lblSecondsFormat);
			}
			JPanel panelSpinnerSeconds = new JPanel();
			GridBagConstraints gbc_panelSpinnerSeconds = new GridBagConstraints();
			gbc_panelSpinnerSeconds.insets = new Insets(0, 0, 5, 0);
			gbc_panelSpinnerSeconds.fill = GridBagConstraints.BOTH;
			gbc_panelSpinnerSeconds.gridx = 0;
			gbc_panelSpinnerSeconds.gridy = 8;
			panel.add(panelSpinnerSeconds, gbc_panelSpinnerSeconds);
			panelSpinnerSeconds.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			Long value = new Long(0);
			Long min = new Long(0);
			Long max = new Long(Long.MAX_VALUE);
			Long step = new Long(1);
			SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
			spinnerSeconds = new JSpinner(model);
			JComponent comp1 = spinnerSeconds.getEditor();
			JTextField tf = ((JSpinner.DefaultEditor) spinnerSeconds.getEditor()).getTextField();
			spinnerSeconds.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					//System.out.println(((Long)spinnerSeconds.getValue()).longValue());
					spinnerDateAndTime.setValue(new Date(((Long)spinnerSeconds.getValue()).longValue() * 1000));
				}
			});
			panelSpinnerSeconds.add(spinnerSeconds);
			{
				JLabel lblKeyCreationDate = new JLabel("Private Key Creation Date and Time");
				lblKeyCreationDate.setHorizontalAlignment(SwingConstants.CENTER);
				GridBagConstraints gbc_lblKeyCreationDate = new GridBagConstraints();
				gbc_lblKeyCreationDate.fill = GridBagConstraints.BOTH;
				gbc_lblKeyCreationDate.insets = new Insets(0, 0, 5, 0);
				gbc_lblKeyCreationDate.gridx = 0;
				gbc_lblKeyCreationDate.gridy = 9;
				panel.add(lblKeyCreationDate, gbc_lblKeyCreationDate);
			}
			JPanel panelSpinnerDateTime = new JPanel();
			GridBagConstraints gbc_panelSpinnerDateTime = new GridBagConstraints();
			gbc_panelSpinnerDateTime.fill = GridBagConstraints.BOTH;
			gbc_panelSpinnerDateTime.gridx = 0;
			gbc_panelSpinnerDateTime.gridy = 10;
			panel.add(panelSpinnerDateTime, gbc_panelSpinnerDateTime);
			panelSpinnerDateTime.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			spinnerDateAndTime = new JSpinner(new SpinnerDateModel(new Date(0), null, null, Calendar.MINUTE));
			JComponent comp = spinnerDateAndTime.getEditor();
			JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
			spinnerDateAndTime.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					//System.out.println(((Date)spinnerDateAndTime.getValue()));
					spinnerSeconds.setValue(new Long(((Date)spinnerDateAndTime.getValue()).getTime()/1000));
				}
			});
			panelSpinnerDateTime.add(spinnerDateAndTime);
			spinnerDateAndTime.setLocale(Locale.getDefault());
			JSpinner.DateEditor de_spinnerDateAndTime = new JSpinner.DateEditor(spinnerDateAndTime, "MMM dd, yyyy HH:mm a");
			//de.getTextField().setEditable( false );
			spinnerDateAndTime.setEditor(de_spinnerDateAndTime);
			{
				JButton btnNow = new JButton("Now");
				btnNow.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						spinnerDateAndTime.setValue(new Date());
					}
				});
				panelSpinnerDateTime.add(btnNow);
			}
			{
				JPanel panel_1 = new JPanel();
				contentPanel.add(panel_1, BorderLayout.SOUTH);
				panel_1.setLayout(new BorderLayout(0, 0));
				{
					JTextArea textArea = new JTextArea();
					textArea.setWrapStyleWord(true);
					textArea.setText("Note: If you don't know Key Creation Date and Time, you can enter date and time about a day earlier when address was first time used in the block chain. Entering correct creation date and time helps to optimize synchronization process.");
					textArea.setRows(2);
					textArea.setLineWrap(true);
					textArea.setEditable(false);
					textArea.setBorder(null);
					textArea.setBackground(SystemColor.window);
					panel_1.add(textArea, BorderLayout.SOUTH);
				}
				{
					chckbxResynchronize = new JCheckBox("Resynchronize after Import");
					chckbxResynchronize.setSelected(true);
					panel_1.add(chckbxResynchronize, BorderLayout.NORTH);
				}
				{
					Component verticalStrut = Box.createVerticalStrut(20);
					panel_1.add(verticalStrut, BorderLayout.CENTER);
				}
			}
			{
				{
				    JFormattedTextField field1 = (JFormattedTextField) comp1.getComponent(0);
				    DefaultFormatter formatter = (DefaultFormatter) field1.getFormatter();
				    formatter.setCommitsOnValidEdit(true);
					tf.setHorizontalAlignment(SwingConstants.CENTER);
				}
			}
			{
				{
				    DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
				    formatter.setCommitsOnValidEdit(true);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Import");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							getECKey();
							dialogResultOK = true;
							dispose();
						} catch (AddressFormatException e1) {
							JOptionPane.showMessageDialog(getContentPane(),
        						    "Private key in not correct format!\nTry to verify what you entered, please!", "Import private key", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialogResultOK = false;
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public boolean showDialog() {
		setModal(true);
		setVisible(true);
		return dialogResultOK;
	}
	
	public ECKey getECKey() throws AddressFormatException {
		DumpedPrivateKey dk = new DumpedPrivateKey(params, textField.getText());
		ECKey key = dk.getKey();
		long creationTime = ((Long)spinnerSeconds.getValue()).longValue();
		key.setCreationTimeSeconds((creationTime < 0) ? 0 : creationTime);
		return key;
	}

	public boolean shouldResynchronize() {
		return chckbxResynchronize.isSelected();
	}
	
	private void scanQR(BufferedImage i) throws ReaderException, ChecksumException, FormatException {
		Image img = i.getScaledInstance(200, -1, Image.SCALE_SMOOTH);
		// Create a buffered image with transparency
	    i = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
	    
	    // Draw the image on to the buffered image
	    Graphics2D bGr = i.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();
	    
	    btnDragOrPaste.setIcon(new ImageIcon(i));
	    BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(i)));
	    //Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>(2);
        //hints.put(EncodeHintType.CHARACTER_SET, "ISO-8859-1");
	    //Vector decodeFormats = new Vector<BarcodeFormat>();
	    //decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
	    //Hashtable hints = new Hashtable<DecodeHintType, Object>(3);
	    //hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
	    Result result = new QRCodeReader().decode(binaryBitmap);//, hints);
	    //System.out.println("QR Code : "+result.getText());
	    textField.setText(result.getText());
	    //System.out.println( s );
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

