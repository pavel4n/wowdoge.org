package org.wowdoge;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DialogOfferBackup extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private Wow wow;
	private int num;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogOfferBackup dialog = new DialogOfferBackup();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DialogOfferBackup() {
		this(null, 0);
	}
	/**
	 * Create the dialog.
	 */
	public DialogOfferBackup(final Wow wow, final int num) {
		this.wow = wow;
		this.num = num;
		setTitle("Wallet Backup Offer - Huge Help in Unpredictable Situations");
		setBounds(100, 100, 667, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JTextArea txtrBeforeYouStart = new JTextArea();
			txtrBeforeYouStart.setWrapStyleWord(true);
			txtrBeforeYouStart.setLineWrap(true);
			txtrBeforeYouStart.setEditable(false);
			txtrBeforeYouStart.setRows(10);
			txtrBeforeYouStart.setText("Before you start to use your newly created wallet or newly created address(es), it is always wise and very recommended to make additional backup for unpredictable situation upfront. Like computer not possible to boot, operating system failure, hard disk failure, splitted water into the computer, computer in fire, computer damaged, computer stolen, electricity failure causing the wallet file to get corrupted, etc.. That is why additional backups of your wallet are very helpful and are very recommended and is important way to prevent this situations to be able to restore your wallet balance. \n\nIt is recommended to save your wallet file into external medium like usb thumb drive or memory card. And or export private keys there. And or to print all private keys with paper backups. So you can anytime restore your wallet balance in unfortunate unpredictable situations. See the options below.");
			txtrBeforeYouStart.setBackground(Version.getInactiveTextFieldColor());
			contentPanel.add(txtrBeforeYouStart);
		}
		{
			JLabel lblNewLabel = new JLabel("Would you like to make a backup?");
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblNewLabel, BorderLayout.NORTH);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Backup Wallet File");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (wow != null)
							wow.saveWalletBackupAs();
					}
				});
				okButton.setActionCommand("");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton btnNewButton = new JButton("Export Private Keys");
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (wow != null)
							wow.exportPrivateKeys();
					}
				});
				buttonPane.add(btnNewButton);
			}
			{
				JButton btnPrintPaperBackup = new JButton("Print Paper Backup");
				btnPrintPaperBackup.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Object[] options = {"All Addresses",
	                    "Only New", "Cancel"};
						int n = JOptionPane.showOptionDialog(contentPanel,
								"Would you like to make paper backup of all wallet addresses or just only of newly created?",
								"Whole Wallet Paper Backup or Backup of Just Newly Created Address(es)?", JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, // do not
																	// use a
																	// custom
																	// Icon
								options, // the titles of buttons
								options[1]); // default button title
						
						if (n == JOptionPane.YES_OPTION)
							wow.makePaperBackups(0);
						if (n == JOptionPane.NO_OPTION)
							wow.makePaperBackups(num);
					}
				});
				buttonPane.add(btnPrintPaperBackup);
			}
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
	}

}
