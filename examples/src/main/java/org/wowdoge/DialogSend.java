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

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import java.awt.Insets;

import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BoxLayout;

import java.awt.Component;

import javax.swing.JSpinner;

import java.awt.Dimension;

import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

import java.awt.Font;

import javax.swing.Box;

import com.google.dogecoin.core.Address;
import com.google.dogecoin.core.AddressFormatException;
import com.google.dogecoin.core.InsufficientMoneyException;
import com.google.dogecoin.core.NetworkParameters;
import com.google.dogecoin.uri.BitcoinURIParseException;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.EventObject;

import javax.swing.JPopupMenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

public class DialogSend extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JSpinner spinnerAmount;
	private JTextField textFieldToAddress;
	private boolean dialogResultOK = false;
	private Address address;
	private NetworkParameters networkParams;
	private ButtonQRScan btnScan;
	private JCheckBox chckbxAll;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogSend dialog = new DialogSend();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setMaximum(float max) {
		spinnerAmount.setModel(new SpinnerNumberModel(new Float(0), new Float(0), new Float(max), new Float(1f)));
		spinnerAmount.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		JTextField tf = ((JSpinner.DefaultEditor) spinnerAmount.getEditor()).getTextField();
		tf.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		tf.setHorizontalAlignment(SwingConstants.CENTER);
		JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinnerAmount.getEditor();  
        DecimalFormat format = editor.getFormat();  
        format.setMinimumFractionDigits(8);  
        
		//tf.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		//tf.setHorizontalAlignment(SwingConstants.CENTER);
        //DecimalFormat format = editor.getFormat();  
        //format.setMinimumFractionDigits(8);  
	}
	
	public void setNetworkParameters(NetworkParameters networkParams) {
		this.networkParams = networkParams;
	}
	
	public boolean showDialog() {
		setModal(true);
		setVisible(true);
		return dialogResultOK;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.textFieldToAddress.setText(address);
	}
	
	public boolean shouldSendAll() {
		return chckbxAll.isSelected();
	}
	
	public float getAmount() {
		return (Float) spinnerAmount.getValue();
	}
	
	public void setAmount(float amount) {
		spinnerAmount.setValue(new Float(amount));
	}
	
//	public boolean isFeeUsed() {
//		return chckbxUseNetworkRecommendedFee.isSelected();
//	}

	/**
	 * Create the dialog.
	 */
	public DialogSend() {
		setTitle("Send "+ Version.coinNameLong + "s");
		setBounds(100, 100, 501, 493);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panelToAddress = new JPanel();
			contentPanel.add(panelToAddress, BorderLayout.CENTER);
			panelToAddress.setLayout(new BorderLayout(0, 0));
			{
				JPanel panel = new JPanel();
				panelToAddress.add(panel, BorderLayout.NORTH);
				panel.setLayout(new BorderLayout(0, 0));
				{
					JLabel lblToAddress = new JLabel("Recipient");
					lblToAddress.setHorizontalAlignment(SwingConstants.CENTER);
					lblToAddress.setAlignmentX(0.5f);
					panel.add(lblToAddress, BorderLayout.CENTER);
				}
			}
			{
				JPanel panel = new JPanel();
				panelToAddress.add(panel, BorderLayout.CENTER);
				panel.setLayout(new BorderLayout(0, 0));
				{
					textFieldToAddress = new TextField();
					textFieldToAddress.setPreferredSize(new Dimension(14, 50));
					panel.add(textFieldToAddress, BorderLayout.NORTH);
					textFieldToAddress.setHorizontalAlignment(SwingConstants.CENTER);
					textFieldToAddress.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
					textFieldToAddress.setColumns(10);
				}
				{
					JPanel panel_2 = new JPanel();
					panel.add(panel_2, BorderLayout.SOUTH);
					panel_2.setPreferredSize(new Dimension(18, 45));
					panel_2.setLayout(new BorderLayout(0, 0));
					{
						JLabel lblAmount = new JLabel(" Amount: ");
						panel_2.add(lblAmount, BorderLayout.WEST);
						lblAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
					}
					spinnerAmount = new JSpinner(new SpinnerNumberModel(new Float(0), new Float(0), new Float(Float.MAX_VALUE), new Float(1f)));
					panel_2.add(spinnerAmount, BorderLayout.CENTER);
					spinnerAmount.setPreferredSize(new Dimension(175, 28));
					spinnerAmount.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
					JTextField tf = ((JSpinner.DefaultEditor) spinnerAmount.getEditor()).getTextField();
					JSpinner.NumberEditor editor = (JSpinner.NumberEditor)spinnerAmount.getEditor();  
					{
						JLabel lblExe = new JLabel(" " + Version.coinNameWithSpace);
						panel_2.add(lblExe, BorderLayout.EAST);
					}
				}
				{
					JScrollPane scrollPane = new JScrollPane();
					panel.add(scrollPane, BorderLayout.CENTER);
					{
						btnScan = new ButtonQRScan("Click to Scan QR Code via Webcam or Drop or Paste QR Code Image...");
						btnScan.setText("Click to Scan QR Code via Webcam or Drop or Paste QR Code Image");
						scrollPane.setViewportView(btnScan);
						btnScan.setHorizontalTextPosition(SwingConstants.CENTER);
						btnScan.setVerticalTextPosition(SwingConstants.BOTTOM);
					}
					{
						JButton btnDonate = new JButton("Click here to donate. We depend on your donations");
						scrollPane.setColumnHeaderView(btnDonate);
						btnDonate.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								textFieldToAddress.setText(Version.donationAddress);
							}
						});
						btnDonate.setForeground(Color.RED);
						btnDonate.setToolTipText("");
					}
					btnScan.addEventListener(new EventQRScannedListener() {

						@Override
						public void handleEventQRScanned(EventObject e) {
							if (btnScan.isURL()) {
								try {
									textFieldToAddress.setText(btnScan.getAddress(networkParams));
								} catch (BitcoinURIParseException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								try {
									spinnerAmount.setValue(new Float(btnScan.getAmount(networkParams)));
								} catch (BitcoinURIParseException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							} else
								textFieldToAddress.setText(btnScan.getScannedQRText());
						}
						
					});
				}
			}
			{
				chckbxAll = new JCheckBox("All"); // + Version.coinNameLong + "s"
				chckbxAll.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						spinnerAmount.setEnabled(!chckbxAll.isSelected());
					}
				});
				chckbxAll.setToolTipText("Allows to sends all your coins to specified address");
				panelToAddress.add(chckbxAll, BorderLayout.SOUTH);
				chckbxAll.setHorizontalAlignment(SwingConstants.CENTER);
			}
		}
		JPanel panelAmount = new JPanel();
		contentPanel.add(panelAmount, BorderLayout.SOUTH);
		panelAmount.setLayout(new BorderLayout(0, 0));
		JButton okButton = new JButton("Send Now!");
		okButton.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		okButton.setPreferredSize(new Dimension(75, 100));
		panelAmount.add(okButton, BorderLayout.SOUTH);
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					address = new Address(networkParams, textFieldToAddress.getText());
					dialogResultOK = true;
					dispose();
				} catch (AddressFormatException e1) {
					JOptionPane.showMessageDialog(getRootPane(),
						    "Incorrect address format!", "Address", 0);
					return;
				}
			}
		});
		getRootPane().setDefaultButton(okButton);
		{
			Component verticalStrut = Box.createVerticalStrut(20);
			panelAmount.add(verticalStrut, BorderLayout.NORTH);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BorderLayout(0, 0));
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dialogResultOK = false;
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton, BorderLayout.EAST);
			}
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
