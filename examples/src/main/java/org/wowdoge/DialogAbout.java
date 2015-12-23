package org.wowdoge;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.Font;
import java.awt.Component;

import javax.swing.Box;

import com.google.dogecoin.core.VersionMessage;

public class DialogAbout extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblLibraryVersion;
	private JLabel lblVersion;
	private JButton btnWwwwowdogeorg;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogAbout dialog = new DialogAbout();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogAbout() {
		setTitle("About " + Version.appName);
		setBounds(100, 100, 331, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			btnWwwwowdogeorg = new JButton("");
			btnWwwwowdogeorg.setToolTipText(Version.appWebsiteWithoutHTTP);
			btnWwwwowdogeorg.setBorder(null);
			btnWwwwowdogeorg.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(Desktop.isDesktopSupported())
					{
					  try {
						Desktop.getDesktop().browse(new URI(Version.appWebsite));
					  } catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					  } catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					  }
					}
				}
			});
			btnWwwwowdogeorg.setHorizontalTextPosition(SwingConstants.CENTER);
			btnWwwwowdogeorg.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent arg0) {
					float r = ((float) btnWwwwowdogeorg.getHeight()) / ((float) btnWwwwowdogeorg.getWidth());
					int min = btnWwwwowdogeorg.getWidth() > btnWwwwowdogeorg.getHeight() ? btnWwwwowdogeorg.getHeight() : btnWwwwowdogeorg.getWidth();
					//System.out.println("RESIZED");
					//btnWwwwowdogeorg.setIcon(new ImageIcon (new ImageIcon(Wow.class.getResource("/org/wowdoge/doge.png")).getImage().getScaledInstance(min, min, Image.SCALE_SMOOTH)));
					
					int min0 = (int) (min * 0.85);
					btnWwwwowdogeorg.setIcon(new ImageIcon (new ImageIcon(Wow.class.getResource(Version.appAboutImagePath)).getImage().getScaledInstance(-1, min0, Image.SCALE_SMOOTH)));
					//btnWwwwowdogeorg.setIconTextGap((btnWwwwowdogeorg.getHeight() - min0)/4);
				}
			});
			{
				JPanel panel = new JPanel();
				contentPanel.add(panel, BorderLayout.NORTH);
				panel.setLayout(new BorderLayout(0, 0));
				{
					JLabel lblWowDoge = new JLabel(Version.appName);
					lblWowDoge.setFont(new Font("Lucida Grande", Font.BOLD, 20));
					panel.add(lblWowDoge, BorderLayout.NORTH);
					lblWowDoge.setHorizontalAlignment(SwingConstants.CENTER);
				}
				{
					JPanel panelVersion = new JPanel();
					panel.add(panelVersion, BorderLayout.SOUTH);
					panelVersion.setLayout(new BorderLayout(0, 0));
					{
						lblVersion = new JLabel("Version 0.0.1");
						panelVersion.add(lblVersion, BorderLayout.NORTH);
						lblVersion.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
						lblVersion.setHorizontalAlignment(SwingConstants.CENTER);
					}
				}
			}
			contentPanel.add(btnWwwwowdogeorg);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.SOUTH);
			panel.setLayout(new BorderLayout(0, 0));
			{
				lblLibraryVersion = new JLabel(Version.coinLibraryName);
				panel.add(lblLibraryVersion, BorderLayout.NORTH);
				lblLibraryVersion.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
				lblLibraryVersion.setHorizontalAlignment(SwingConstants.CENTER);
			}
			{
				JLabel lblCopyrightWow = new JLabel("Copyright 2015 " + Version.appName + " Developers");
				panel.add(lblCopyrightWow);
				lblCopyrightWow.setHorizontalAlignment(SwingConstants.CENTER);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BorderLayout(0, 0));
			{
				JPanel panel = new JPanel();
				buttonPane.add(panel, BorderLayout.CENTER);
				{
					JButton okButton = new JButton("OK");
					okButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							dispose();
						}
					});
					panel.add(okButton);
					okButton.setActionCommand("OK");
					getRootPane().setDefaultButton(okButton);
				}
			}
		}
	}
	
	public boolean showDialog() {
		lblVersion.setText("Version " + Version.currentVersion);
		lblLibraryVersion.setText(Version.coinLibraryName + ": " + VersionMessage.LIBRARY_SUBVER);
		setModal(true);
		setVisible(true);
		return true;
	}

}
