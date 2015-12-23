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

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import java.awt.BorderLayout;

import javax.swing.SwingConstants;
import javax.swing.JButton;

import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class DialogStart extends JDialog {
	private JButton btnNewWallet;
	private JButton btnOpenExistingWallet;
	private boolean dialogResultOK = false;
	private String walletPath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DialogStart dialog = new DialogStart();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public DialogStart() {
		setTitle(Version.appName + " Wallet - Quick Start");
		setBounds(100, 100, 450, 300);
		
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		btnNewWallet = new JButton("Create New Wallet");
		btnNewWallet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DialogNew d = new DialogNew();
				d.setLocationRelativeTo(getContentPane());
				if (d.showDialog()) {
					walletPath = new File(d.getFolder(), d.getWalletFileName()).getAbsolutePath();
					dialogResultOK = true;
				}
				dispose();
			}
		});
		btnNewWallet.setIconTextGap(15);
		btnNewWallet.setHorizontalTextPosition(SwingConstants.CENTER);
		btnNewWallet.setVerticalTextPosition(SwingConstants.TOP);
		btnNewWallet.setVerticalAlignment(SwingConstants.TOP);
		btnNewWallet.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				int min = btnNewWallet.getWidth() > btnNewWallet.getHeight() ? btnNewWallet.getHeight() : btnNewWallet.getWidth();
				int min0 = (int) (min * 0.85);
				//System.out.println("RESIZED");
				btnNewWallet.setIcon(new ImageIcon (new ImageIcon(Wow.class.getResource(Version.coinImagePath)).getImage().getScaledInstance(min0, min0, Image.SCALE_SMOOTH)));
				btnNewWallet.setIconTextGap((btnNewWallet.getHeight() - min0)/4);
			}
		});
		panel.add(btnNewWallet);
		
		btnOpenExistingWallet = new JButton("Open Existing Wallet");
		btnOpenExistingWallet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
				        Version.walletFileDescription, Version.walletFileExtension);
				fileChooser.addChoosableFileFilter(filter);
				int returnVal = fileChooser.showOpenDialog(getContentPane());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					walletPath = fileChooser.getSelectedFile().getAbsolutePath();
					dialogResultOK = true;
				}
				dispose();
			}
		});
		btnOpenExistingWallet.setIconTextGap(15);
		btnOpenExistingWallet.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int min = btnOpenExistingWallet.getWidth() > btnOpenExistingWallet.getHeight() ? btnOpenExistingWallet.getHeight() : btnOpenExistingWallet.getWidth();
				int min0 = (int) (min * 0.85);
				//System.out.println("RESIZED");
				btnOpenExistingWallet.setIcon(new ImageIcon (new ImageIcon(Wow.class.getResource(Version.coinImagePath)).getImage().getScaledInstance(min0, min0, Image.SCALE_SMOOTH)));
				btnOpenExistingWallet.setIconTextGap((btnOpenExistingWallet.getHeight() - min0)/4);
			}
		});
		btnOpenExistingWallet.setVerticalTextPosition(SwingConstants.TOP);
		btnOpenExistingWallet.setVerticalAlignment(SwingConstants.TOP);
		btnOpenExistingWallet.setHorizontalTextPosition(SwingConstants.CENTER);
		panel.add(btnOpenExistingWallet);
		
		JPanel panelQuit = new JPanel();
		getContentPane().add(panelQuit, BorderLayout.SOUTH);
		panelQuit.setLayout(new BorderLayout(0, 0));
		
		JButton btnQuit = new JButton("Quit");
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panelQuit.add(btnQuit, BorderLayout.EAST);
		
		JPanel panelWhat = new JPanel();
		getContentPane().add(panelWhat, BorderLayout.NORTH);
		
		JLabel label = new JLabel("What would you like to do?");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		panelWhat.add(label);

	}

	public boolean showDialog() {
		setModal(true);
		setVisible(true);
		return dialogResultOK;
	}
	
	public String getWalletFilePath() {
		return walletPath;
	}
}
