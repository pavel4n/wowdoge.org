package org.wowdoge;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class DialogThankForDonation extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private float amount;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogThankForDonation dialog = new DialogThankForDonation();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogThankForDonation() {
		setTitle("Thank you");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JTextArea textArea = new JTextArea("Thank you for supporting " + Version.appName + ". Your donations are appreciated and will help us support the labor in improving our products.\n\nWe would appreciate your email address so we know who our supporters are.");
			textArea.setBackground(Version.getInactiveTextFieldColor());
			textArea.setWrapStyleWord(true);
			textArea.setLineWrap(true);
			textArea.setEditable(false);
			contentPanel.add(textArea, BorderLayout.NORTH);
		}
		{
			JButton okButton = new JButton("Send us Email");
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {						
						Desktop desktop;
						if (Desktop.isDesktopSupported() 
						    && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) {
						  URI mailto = null;
						
						try {
						
							mailto = new URI("mailto:"
								+ Version.supportEmailAddress
								+ "?subject=" + URLEncoder.encode("Donation/Support", "UTF-8") + "&body=Donated%20"
								+ URLEncoder.encode(Float.toString(amount), "UTF-8") + "%20" + Version.coinName);

						
							desktop.mail(mailto);
						} catch (URISyntaxException e1) {
							JOptionPane.showMessageDialog(null,
								    "Error in mailto syntax: " + e1.getMessage(), "Default Email Client Open", JOptionPane.WARNING_MESSAGE);

						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null,
								    "Not possible to open default email client. " + e1.getMessage() + "\nSend us email message directly to " + Version.supportEmailAddress + ", please.", "Default Email Client", JOptionPane.WARNING_MESSAGE);
						}
						} else {
							JOptionPane.showMessageDialog(null,
								    "Mailto is not supported.\nSend us email message directly to " + Version.supportEmailAddress + ", please.", "Default Email Client", JOptionPane.WARNING_MESSAGE);
						}
				}
			});
			contentPanel.add(okButton, BorderLayout.SOUTH);
			okButton.setActionCommand("OK");
			getRootPane().setDefaultButton(okButton);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("OK");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	
	public void setAmount(float amount) {
		this.amount = amount;
	}
}
