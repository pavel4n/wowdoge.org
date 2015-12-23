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
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import java.awt.SystemColor;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;

public class DialogAddress extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private boolean dialogResultOK = false;
	private JSpinner spinnerNumber;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogAddress dialog = new DialogAddress();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogAddress() {
		setTitle("Create New Addresses");
		setBounds(100, 100, 486, 326);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panelInfo = new JPanel();
			contentPanel.add(panelInfo, BorderLayout.NORTH);
			panelInfo.setLayout(new BorderLayout(0, 0));
			{
				JLabel lblHowManyAddresses = new JLabel("How many addresses would you like to create?");
				lblHowManyAddresses.setFont(new Font("Lucida Grande", Font.BOLD, 14));
				panelInfo.add(lblHowManyAddresses, BorderLayout.NORTH);
				lblHowManyAddresses.setHorizontalAlignment(SwingConstants.CENTER);
			}
			{
				JTextArea txtrMoreAddressesHelp = new JTextArea();
				txtrMoreAddressesHelp.setEditable(false);
				txtrMoreAddressesHelp.setBackground(Version.getInactiveTextFieldColor());
				panelInfo.add(txtrMoreAddressesHelp, BorderLayout.CENTER);
				txtrMoreAddressesHelp.setLineWrap(true);
				txtrMoreAddressesHelp.setWrapStyleWord(true);
				txtrMoreAddressesHelp.setRows(3);
				txtrMoreAddressesHelp.setText("\nCreating multiple addresses helps the wallet holder to organize transactions and can help achieve better anonymity. A lower amount allows greater transparency and using a single address alone, creates the most transparent wallet.");
			}
		}
		{
			JTextArea txtrWalletValueCan = new JTextArea();
			txtrWalletValueCan.setWrapStyleWord(true);
			txtrWalletValueCan.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
			txtrWalletValueCan.setBackground(Version.getInactiveTextFieldColor());
			txtrWalletValueCan.setLineWrap(true);
			txtrWalletValueCan.setEditable(false);
			txtrWalletValueCan.setRows(4);
			txtrWalletValueCan.setText("\nYour " + Version.coinNameLong + " can be stored among multiple addresses to achieve a desired anonymity to transparency ratio in the payment network. The distribution of this value among addresses is done during send transactions. This also happens each time a different address is used for receiving coins. The use of a new address for receiving and sending coins or utilizing multiple addresses helps you to achieve anonymity in the payment network and is generally recommended for users, particularly in a case where anonymity is desired.\n");
			contentPanel.add(txtrWalletValueCan, BorderLayout.CENTER);
		}
		{
			JPanel panelAmount = new JPanel();
			contentPanel.add(panelAmount, BorderLayout.SOUTH);
			panelAmount.setLayout(new BorderLayout(0, 0));
			{
				JPanel panel = new JPanel();
				panelAmount.add(panel, BorderLayout.NORTH);
				panel.setLayout(new GridLayout(0, 3, 0, 0));
				{
					JLabel lblAmountOfAddresses = new JLabel("Create ");
					panel.add(lblAmountOfAddresses);
					lblAmountOfAddresses.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				{
					spinnerNumber = new JSpinner(new SpinnerNumberModel(new Integer(1), new Integer(1), new Integer(100), new Integer(1)));
					panel.add(spinnerNumber);
					spinnerNumber.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
					spinnerNumber.setPreferredSize(new Dimension(105, 28));
				}
				
				spinnerNumber.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
				{
					JLabel lblAddresses = new JLabel(" Addresses (100 max.)");
					lblAddresses.setHorizontalAlignment(SwingConstants.LEFT);
					panel.add(lblAddresses);
				}
				JTextField tf = ((JSpinner.DefaultEditor) spinnerNumber.getEditor()).getTextField();
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dialogResultOK = true;
						dispose();
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
	
	private void setSpinnerNumber() {
		spinnerNumber.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		JTextField tf = ((JSpinner.DefaultEditor) spinnerNumber.getEditor()).getTextField();
		tf.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		tf.setHorizontalAlignment(SwingConstants.CENTER); 
	}
	
	public boolean showDialog() {
		setSpinnerNumber();
		setModal(true);
		setVisible(true);
		return dialogResultOK;
	}
	
	public int getNumberOfAddressToCreate() {
		return (Integer) spinnerNumber.getValue();
	}

}
