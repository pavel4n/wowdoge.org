package org.wowdoge;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import java.awt.SystemColor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.border.LineBorder;

import java.awt.Color;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DecimalFormat;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.text.DefaultFormatter;
import javax.swing.JPopupMenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;

import com.google.dogecoin.core.Utils;
import com.google.dogecoin.uri.BitcoinURI;
import javax.swing.UIManager;

public class DialogRequestPayment extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private JSpinner spinnerAmount;
	private JTextField textFieldToAddress;
	private JButton btnQRCode;
	private JTextField textFieldLabel;
	private String paymentURL;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogRequestPayment dialog = new DialogRequestPayment();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogRequestPayment() {
		setTitle("Request " + Version.coinNameLong + "s Payment");
		setBounds(100, 100, 594, 493);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panelToAddress = new JPanel();
			contentPanel.add(panelToAddress);
			panelToAddress.setLayout(new BorderLayout(0, 0));
			{
				JPanel panelHeader = new JPanel();
				panelToAddress.add(panelHeader, BorderLayout.NORTH);
				panelHeader.setLayout(new BorderLayout(0, 0));
				{
					JLabel lblToAddress = new JLabel("To Address:");
					lblToAddress.setHorizontalAlignment(SwingConstants.CENTER);
					lblToAddress.setAlignmentX(0.5f);
					panelHeader.add(lblToAddress, BorderLayout.NORTH);
				}
				{
					JPanel panelAmountBase = new JPanel();
					panelHeader.add(panelAmountBase, BorderLayout.CENTER);
					panelAmountBase.setLayout(new BorderLayout(0, 0));
					{
						JPanel panelAmount = new JPanel();
						panelAmountBase.add(panelAmount, BorderLayout.SOUTH);
						panelAmount.setPreferredSize(new Dimension(18, 50));
						panelAmount.setLayout(new BorderLayout(0, 0));
						{
							JLabel lblAmount = new JLabel(" Amount: ");
							panelAmount.add(lblAmount, BorderLayout.WEST);
							lblAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
						}
						spinnerAmount = new JSpinner(new SpinnerNumberModel(new Float(0), new Float(0), new Float(Float.MAX_VALUE), new Float(1f)));
						spinnerAmount.addChangeListener(new ChangeListener() {
							public void stateChanged(ChangeEvent arg0) {
								updateQRCode();
							}
						});
						panelAmount.add(spinnerAmount, BorderLayout.CENTER);
						spinnerAmount.setPreferredSize(new Dimension(175, 28));
						spinnerAmount.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
						JTextField tf = ((JSpinner.DefaultEditor) spinnerAmount.getEditor()).getTextField();
						JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinnerAmount.getEditor();  
						{
							JLabel lblExe = new JLabel(" " + Version.coinNameWithSpace);
							panelAmount.add(lblExe, BorderLayout.EAST);
						}
					}
					{
						textFieldToAddress = new TextField();
						textFieldToAddress.setBorder(null);
						panelAmountBase.add(textFieldToAddress, BorderLayout.NORTH);
						textFieldToAddress.setBackground(Version.getInactiveTextFieldColor());
						textFieldToAddress.setEditable(false);
						textFieldToAddress.setPreferredSize(new Dimension(14, 50));
						textFieldToAddress.setHorizontalAlignment(SwingConstants.CENTER);
						textFieldToAddress.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
						textFieldToAddress.setColumns(10);
					}
				}
				{
					JPanel panelLabel = new JPanel();
					panelHeader.add(panelLabel, BorderLayout.SOUTH);
					panelLabel.setLayout(new BorderLayout(0, 0));
					{
						JLabel lblLabel = new JLabel("Label, Name or Description:");
						lblLabel.setHorizontalAlignment(SwingConstants.CENTER);
						panelLabel.add(lblLabel, BorderLayout.NORTH);
					}
					{
						textFieldLabel = new JTextField();
						textFieldLabel.getDocument().addDocumentListener(new DocumentListener() {
							  public void changedUpdate(DocumentEvent e) {
								  	  updateQRCode();
								  }
								  public void removeUpdate(DocumentEvent e) {
									  updateQRCode();
								  }
								  public void insertUpdate(DocumentEvent e) {
									  updateQRCode();
								  }
								});
						textFieldLabel.setHorizontalAlignment(SwingConstants.CENTER);
						textFieldLabel.setPreferredSize(new Dimension(14, 35));
						panelLabel.add(textFieldLabel, BorderLayout.SOUTH);
						textFieldLabel.setColumns(10);
					}
				}
			}
			{
				JPanel panel = new JPanel();
				panelToAddress.add(panel, BorderLayout.CENTER);
				panel.setLayout(new BorderLayout(0, 0));
				{
					btnQRCode = new JButton("Click to Scan QR Code via Webcam or Drop or Paste QR Code Image...");
					btnQRCode.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String sub = "Payment Request";
							String mailBody = "Please send payment to " + getPaymentLongerURL();
							String forUri = String.format("mailto:?subject=%s&body=%s",  urlEncode(sub), urlEncode(mailBody));
			                try {
								Desktop.getDesktop().mail(new URI(forUri));
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (URISyntaxException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					});
					btnQRCode.setHorizontalTextPosition(SwingConstants.CENTER);
					btnQRCode.setToolTipText("Click to Request Payment via E-Mail");
					btnQRCode.setActionCommand("Click to Request Payment via E-Mail");
					panel.add(btnQRCode);
					btnQRCode.addComponentListener(new ComponentAdapter() {
						@Override
						public void componentResized(ComponentEvent arg0) {
							updateQRCode();
						}
					});
					btnQRCode.setText("Click to Request Payment via E-Mail");
					btnQRCode.setVerticalTextPosition(SwingConstants.BOTTOM);
					{
						JPopupMenu popupMenu = new JPopupMenu();
						addPopup(btnQRCode, popupMenu);
						{
							{
								JMenuItem mntmCopyUrl = new JMenuItem("Copy Payment Request URL");
								mntmCopyUrl.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										copyURL();
									}
								});
								JMenuItem mntmCopy = new JMenuItem("Copy Payment Request QR Code");
								mntmCopy.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										copyQRCodeImage();
									}
								});
								popupMenu.add(mntmCopy);
								popupMenu.add(mntmCopyUrl);
							}
						}
					}
				}
			}
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnCopyPaymentRequest = new JButton("Copy Payment Request URL");
				btnCopyPaymentRequest.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						copyURL();
					}
				});
				btnCopyPaymentRequest.setToolTipText("Copy Payment Request URL");
				buttonPane.add(btnCopyPaymentRequest);
			}
			{
				JButton btnCopyPymentRequestQRCode = new JButton("Copy Payment Request QR Code");
				btnCopyPymentRequestQRCode.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						copyQRCodeImage();
					}
				});
				btnCopyPymentRequestQRCode.setToolTipText("Copy Payment Request QR Code");
				btnCopyPymentRequestQRCode.setActionCommand("Cancel");
				buttonPane.add(btnCopyPymentRequestQRCode);
			}
			{
				JButton buttonClose = new JButton("Close");
				buttonClose.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				buttonClose.setActionCommand("Cancel\n");
				buttonPane.add(buttonClose);
				getRootPane().setDefaultButton(buttonClose);
			}
		}
		setSpinner();
	}
	
	private String updatePaymentURL() {
		paymentURL = BitcoinURI.convertToBitcoinURI(textFieldToAddress.getText(),  Utils.toNanoCoins(spinnerAmount.getValue().toString()), textFieldLabel.getText(), ""); //Version.coinNameLongSmall + ":" + textFieldToAddress.getText() + "?amount="+spinnerAmount.getValue() + "&label="+textFieldLabel.getText();
		//System.out.println(paymentURL);
		return paymentURL;
	}
	
	private void updateQRCode() {
		if ((btnQRCode.getWidth() != 0) && (btnQRCode.getHeight() != 0))
				btnQRCode.setIcon(new ImageIcon(new QRImage(updatePaymentURL(), btnQRCode.getWidth() -4, btnQRCode.getHeight() - 24)));
	}
	
	private String getPaymentLongerURL() {
		paymentURL = BitcoinURI.convertToBitcoinURI(textFieldToAddress.getText(),  Utils.toNanoCoins(spinnerAmount.getValue().toString()), textFieldLabel.getText(), ""); //Version.coinNameLongSmall + "://" + textFieldToAddress.getText() + "?amount="+spinnerAmount.getValue() + "&label="+textFieldLabel.getText();
		paymentURL = paymentURL.replace(":", "://");
		return paymentURL;
	}
	
	private void setSpinner() {
		spinnerAmount.setModel(new SpinnerNumberModel(new Float(0), new Float(0), new Float(Float.MAX_VALUE), new Float(1f)));
		spinnerAmount.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		JTextField tf = ((JSpinner.DefaultEditor) spinnerAmount.getEditor()).getTextField();
		tf.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		tf.setHorizontalAlignment(SwingConstants.CENTER);
		JFormattedTextField field = (JFormattedTextField) spinnerAmount.getEditor().getComponent(0);
		DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
	    formatter.setCommitsOnValidEdit(true);
		JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinnerAmount.getEditor();  
        DecimalFormat format = editor.getFormat();  
        format.setMinimumFractionDigits(8);  
        
		//tf.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		//tf.setHorizontalAlignment(SwingConstants.CENTER);
        //DecimalFormat format = editor.getFormat();  
        //format.setMinimumFractionDigits(8);  
	}
	
	private static final String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
	
	private void copyURL() {
		StringSelection stringSelection = new StringSelection (paymentURL);//getPaymentLongerURL());
		Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
		clpbrd.setContents (stringSelection, null);
	}
	
	private void copyQRCodeImage() {
		int size = (btnQRCode.getWidth() < btnQRCode.getHeight()) ? btnQRCode.getWidth() -8 : btnQRCode.getHeight() - 30;
		TransferableImage trans = new TransferableImage( new QRImage(updatePaymentURL(), size, size) );
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents( trans, null );
	}

	public void setAddress(String address) {
		textFieldToAddress.setText(address);
	}
	
	public void setLabel(String label) {
		textFieldLabel.setText(label);
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
