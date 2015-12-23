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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextField;
import javax.swing.JLabel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.Dimension;

import javax.swing.SwingConstants;

import java.awt.Font;

import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.JSlider;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JTextArea;
import java.awt.SystemColor;

public class DialogNew extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldWalletFileName;
	private JTextField textFieldFolder;
	private String extension = Version.walletFileExtensionWithDot; //".dogewallet";
	private boolean dialogResultOK = false;
	private JLabel lbldogewallet;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogNew dialog = new DialogNew();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogNew() {
		setTitle("New Wallet");
		setBounds(100, 100, 450, 188);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.NORTH);
			panel.setLayout(new GridLayout(2, 0, 0, 0));
			{
				JPanel panelWalletFile = new JPanel();
				panel.add(panelWalletFile);
				panelWalletFile.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				{
					JLabel lblWalletName = new JLabel("Wallet Name:");
					panelWalletFile.add(lblWalletName);
				}
				{
					textFieldWalletFileName = new JTextField();
					textFieldWalletFileName.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
					textFieldWalletFileName.setHorizontalAlignment(SwingConstants.CENTER);
					panelWalletFile.add(textFieldWalletFileName);
					textFieldWalletFileName.setColumns(10);
				}
				{
					lbldogewallet = new JLabel(Version.walletFileExtensionWithDot);
					panelWalletFile.add(lbldogewallet);
				}
			}
			{
				JPanel panelWalletFolder = new JPanel();
				panel.add(panelWalletFolder);
				panelWalletFolder.setLayout(new BorderLayout(0, 0));
				{
					textFieldFolder = new JTextField();
					panelWalletFolder.add(textFieldFolder, BorderLayout.CENTER);
					textFieldFolder.setColumns(10);
				}
				{
					JButton btnSelect = new JButton("Select");
					btnSelect.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							JFileChooser fileChooser = new JFileChooser();
							fileChooser.setDialogTitle("Select Folder");
							fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							FileNameExtensionFilter filter = new FileNameExtensionFilter(
							        Version.walletFileDescription, Version.walletFileExtension);
							fileChooser.addChoosableFileFilter(filter);
							int returnVal = fileChooser.showOpenDialog(contentPanel);
							if (returnVal == JFileChooser.APPROVE_OPTION) {
								File f = fileChooser.getSelectedFile();
								textFieldFolder.setText(f.getAbsolutePath());
							}
						}
					});
					panelWalletFolder.add(btnSelect, BorderLayout.EAST);
				}
				{
					JLabel lblStoreWalletIn = new JLabel("Store Wallet File in");
					panelWalletFolder.add(lblStoreWalletIn, BorderLayout.NORTH);
					lblStoreWalletIn.setHorizontalAlignment(SwingConstants.CENTER);
				}
				{
					JLabel lblFolder = new JLabel("Folder:");
					panelWalletFolder.add(lblFolder, BorderLayout.WEST);
				}
			}
		}
		{
			JSlider slider = new JSlider();
			slider.setVisible(false);
			contentPanel.add(slider, BorderLayout.SOUTH);
		}
		{
			JPanel panel = new JPanel();
			panel.setVisible(false);
			contentPanel.add(panel, BorderLayout.CENTER);
			{
				JLabel lblPreset = new JLabel("Preset:");
				lblPreset.setHorizontalAlignment(SwingConstants.CENTER);
				lblPreset.setPreferredSize(new Dimension(250, 16));
				panel.add(lblPreset);
			}
			{
				JComboBox comboBox = new JComboBox();
				comboBox.setPreferredSize(new Dimension(250, 27));
				panel.add(comboBox);
			}
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1);
				{
					JLabel lblPregenerate = new JLabel("Pregenerate:");
					panel_1.add(lblPregenerate);
				}
				JSpinner spinner = new JSpinner();
				panel_1.add(spinner);
				spinner.setPreferredSize(new Dimension(75, 28));
				{
					JLabel lblAddresses = new JLabel("Addresses");
					panel_1.add(lblAddresses);
				}
			}
			{
				JTextArea txtrLowerAmountOf = new JTextArea();
				txtrLowerAmountOf.setWrapStyleWord(true);
				txtrLowerAmountOf.setBackground(SystemColor.window);
				txtrLowerAmountOf.setRows(2);
				txtrLowerAmountOf.setText("Greater amount helps to achieve better anonymity for coins usage.\nLower amount of addresses means more transparent coins usage.");
				panel.add(txtrLowerAmountOf);
			}
			{
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						File f = getFolder();
						if (f.exists()) {
							File fw = new File(f, getWalletFileName());
							if (fw.exists()) {
								JOptionPane.showMessageDialog(getRootPane(),
									    "The specified wallet file already exist in the specified folder!", "New Wallet Creation",0);
								dialogResultOK = false;
							} else {
								dialogResultOK = true;
								dispose();
							}
						} else {
							JOptionPane.showMessageDialog(getRootPane(),
								    "Folder does not exist!", "New Wallet Creation", 0);
							dialogResultOK = false;
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
					public void actionPerformed(ActionEvent arg0) {
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
		lbldogewallet.setText(extension);
		setModal(true);
		setVisible(true);
		return dialogResultOK;
	}
	
	public File getFolder() {
		return new File(textFieldFolder.getText());
	}
	
	public String getWalletFileName() {
		return textFieldWalletFileName.getText() + extension;
	}
}
