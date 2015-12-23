package org.wowdoge;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JToolBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.border.MatteBorder;

import java.awt.Component;

import javax.swing.Box;

import com.google.dogecoin.core.ECKey;
import com.google.dogecoin.core.NetworkParameters;
import com.google.dogecoin.params.MainNetParams;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import javax.swing.JSpinner;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class DialogPaperWallet extends JDialog implements Printable {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtLabel;
	private JTextField txtAddress;
	private NetworkParameters networkParameters;
	private JTextField txtPrivateKey;
	private JButton btnNewPaperWallet;
	private JLabel lblPaperBackup;
	private JPanel panel;
	private Printable printable;
	private JTextField textFieldCreationDate;
	private JTextField textFieldCreationDateNormal;
	private JButton btnPrivateKey;
	private JButton btnAddress;
	private Component horizontalStrut;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogPaperWallet dialog = new DialogPaperWallet(new MainNetParams(), null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogPaperWallet(NetworkParameters networkParameters, ECKey key) {
		this.networkParameters = networkParameters;
		printable = this;
		setTitle("Print Preview");
		setBounds(100, 100, 1024, 715);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JToolBar toolBar = new JToolBar();
			contentPanel.add(toolBar, BorderLayout.NORTH);
			{
				btnNewPaperWallet = new JButton("New Paper Wallet");
				btnNewPaperWallet.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						generateNewPaperWallet();
					}
				});
				toolBar.add(btnNewPaperWallet);
			}
			{
				JButton btnPrint = new JButton("Print Now");
				btnPrint.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						PrinterJob job = PrinterJob.getPrinterJob();
						PageFormat pf = job.defaultPage();
						//job.setPrintable(printable);
						pf.setOrientation(PageFormat.LANDSCAPE);
						Book bk = new Book();
					    bk.append(printable, pf);
					    job.setPageable(bk);
						if (job.printDialog()) {
						    try {
						        job.print();
						    } catch (PrinterException e) {
						        // The job did not successfully
						        // complete
						    	e.printStackTrace();
						    }
						}
					}
				});
				{
					Component horizontalGlue = Box.createHorizontalGlue();
					toolBar.add(horizontalGlue);
				}
				toolBar.add(btnPrint);
			}
			{
				Component horizontalGlue = Box.createHorizontalGlue();
				toolBar.add(horizontalGlue);
			}
			{
				horizontalStrut = Box.createHorizontalStrut(20);
				horizontalStrut.setPreferredSize(new Dimension(161, 0));
				toolBar.add(horizontalStrut);
			}
		}
		{
			panel = new JPanel();
			panel.setMinimumSize(new Dimension(1000, 10));
			panel.setBackground(Color.WHITE);
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			{
				JPanel panelAddressAndPrivate = new JPanel();
				panelAddressAndPrivate.setMinimumSize(new Dimension(1000, 10));
				panelAddressAndPrivate.setBackground(Color.WHITE);
				panel.add(panelAddressAndPrivate, BorderLayout.CENTER);
				panelAddressAndPrivate.setLayout(new GridLayout(0, 2, 10, 20));
				{
					JPanel panelAddress = new JPanel();
					panelAddress.setMinimumSize(new Dimension(500, 10));
					panelAddress.setBackground(Color.WHITE);
					panelAddressAndPrivate.add(panelAddress);
					panelAddress.setLayout(new BorderLayout(0, 0));
					{
						btnAddress = new JButton("");
						btnAddress.setSize(new Dimension(500, 500));
						btnAddress.setMinimumSize(new Dimension(500, 500));
						btnAddress.setPreferredSize(new Dimension(500, 500));
						btnAddress.setBorder(null);
						btnAddress.setBackground(Color.WHITE);
						btnAddress.addComponentListener(new ComponentAdapter() {
							@Override
							public void componentResized(ComponentEvent arg0) {
								String address = txtAddress.getText();
								//System.out.println("Addres:" + address);
								if (address != null) {
									btnAddress.setIcon(new ImageIcon(new QRImage(address, btnAddress.getWidth(), btnAddress.getHeight())));
								}
							}
						});
						panelAddress.add(btnAddress);
					}
					{
						JLabel lblAddressLabel = new JLabel("Public Address");
						lblAddressLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
						lblAddressLabel.setHorizontalAlignment(SwingConstants.CENTER);
						panelAddress.add(lblAddressLabel, BorderLayout.NORTH);
					}
					{
						txtAddress = new TextField();
						txtAddress.setPreferredSize(new Dimension(500, 28));
						txtAddress.setMinimumSize(new Dimension(500, 28));
						txtAddress.setBackground(Color.WHITE);
						txtAddress.setEditable(false);
						txtAddress.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
						txtAddress.setHorizontalAlignment(SwingConstants.CENTER);
						txtAddress.setText("Address");
						txtAddress.setBorder(null);
						panelAddress.add(txtAddress, BorderLayout.SOUTH);
						txtAddress.setColumns(10);
					}
				}
				{
					JPanel panelPrivate = new JPanel();
					panelPrivate.setMinimumSize(new Dimension(500, 10));
					panelPrivate.setBackground(Color.WHITE);
					panelAddressAndPrivate.add(panelPrivate);
					panelPrivate.setLayout(new BorderLayout(0, 0));
					{
						btnPrivateKey = new JButton("");
						btnPrivateKey.setSize(new Dimension(500, 500));
						btnPrivateKey.setMinimumSize(new Dimension(500, 500));
						btnPrivateKey.setPreferredSize(new Dimension(500, 500));
						btnPrivateKey.setBorder(null);
						btnPrivateKey.setBackground(Color.WHITE);
						btnPrivateKey.addComponentListener(new ComponentAdapter() {
							@Override
							public void componentResized(ComponentEvent e) {
								String privateKey = txtPrivateKey.getText();
								if (privateKey != null) {
									btnPrivateKey.setIcon(new ImageIcon(new QRImage(privateKey, btnPrivateKey.getWidth(), btnPrivateKey.getHeight())));
								}
							}
						});
						panelPrivate.add(btnPrivateKey);
					}
					{
						JLabel lblPrivateKeyLabel = new JLabel("Private Key");
						lblPrivateKeyLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
						lblPrivateKeyLabel.setHorizontalAlignment(SwingConstants.CENTER);
						panelPrivate.add(lblPrivateKeyLabel, BorderLayout.NORTH);
					}
					{
						txtPrivateKey = new TextField();
						txtPrivateKey.setPreferredSize(new Dimension(500, 28));
						txtPrivateKey.setMinimumSize(new Dimension(500, 28));
						txtPrivateKey.setEditable(false);
						txtPrivateKey.setHorizontalAlignment(SwingConstants.CENTER);
						txtPrivateKey.setBackground(Color.WHITE);
						txtPrivateKey.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
						txtPrivateKey.setBorder(null);
						panelPrivate.add(txtPrivateKey, BorderLayout.SOUTH);
						txtPrivateKey.setColumns(10);
					}
				}
			}
			{
				JPanel panelHeader = new JPanel();
				panelHeader.setMinimumSize(new Dimension(1000, 10));
				panelHeader.setBackground(Color.WHITE);
				panel.add(panelHeader, BorderLayout.NORTH);
				panelHeader.setLayout(new BorderLayout(0, 0));
				{
					lblPaperBackup = new JLabel(Version.coinNameLong + " Paper Wallet");
					lblPaperBackup.setMinimumSize(new Dimension(1000, 16));
					panelHeader.add(lblPaperBackup, BorderLayout.NORTH);
					lblPaperBackup.setFont(new Font("Lucida Grande", Font.PLAIN, 40));
					lblPaperBackup.setHorizontalAlignment(SwingConstants.CENTER);
				}
				{
					txtLabel = new TextField();
					txtLabel.addFocusListener(new FocusAdapter() {
						@Override
						public void focusGained(FocusEvent arg0) {
							if (txtLabel.getText().equals("Type something here...")) {
								txtLabel.setText("");
							}
						}
					});
					txtLabel.setMinimumSize(new Dimension(1000, 28));
					txtLabel.setBorder(null);
					txtLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 25));
					txtLabel.setHorizontalAlignment(SwingConstants.CENTER);
					txtLabel.setText("Type something here...");
					panelHeader.add(txtLabel);
					txtLabel.setColumns(10);
				}
				{
					JPanel panelKeyCreationDate = new JPanel();
					panelKeyCreationDate.setBackground(Color.WHITE);
					panelHeader.add(panelKeyCreationDate, BorderLayout.SOUTH);
					{
						JLabel lblKeyCreationDate = new JLabel("Key Creation Date:");
						panelKeyCreationDate.add(lblKeyCreationDate);
					}
					{
						textFieldCreationDate = new TextField();
						textFieldCreationDate.setBackground(Color.WHITE);
						panelKeyCreationDate.add(textFieldCreationDate);
						textFieldCreationDate.setHorizontalAlignment(SwingConstants.CENTER);
						textFieldCreationDate.setText("Unknown");
						textFieldCreationDate.setBorder(null);
						textFieldCreationDate.setEditable(false);
						textFieldCreationDate.setColumns(10);
					}
					{
						textFieldCreationDateNormal = new TextField();
						textFieldCreationDateNormal.setBackground(Color.WHITE);
						textFieldCreationDateNormal.setHorizontalAlignment(SwingConstants.CENTER);
						panelKeyCreationDate.add(textFieldCreationDateNormal);
						textFieldCreationDateNormal.setBorder(null);
						textFieldCreationDateNormal.setEditable(false);
						textFieldCreationDateNormal.setColumns(15);
					}
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setMinimumSize(new Dimension(1000, 10));
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Close");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		if (key == null)
			generateNewPaperWallet();
		else {
			setValues(key);
			btnNewPaperWallet.setVisible(false);
			horizontalStrut.setVisible(false);
			lblPaperBackup.setText(Version.coinNameLong + " Paper Backup");
		}
	}
	
	private void generateNewPaperWallet() {
		ECKey key = new ECKey();
		setValues(key);
	}
	private void setValues(ECKey key) {
		String address = key.toAddress(networkParameters).toString();
		txtAddress.setText(address);
		String date = (key.getCreationTimeSeconds() == 0) ? "Unknown" : Long.toString(key.getCreationTimeSeconds());
		//spinnerCreationDate.setVisible(key.getCreationTimeSeconds() != 0);
		textFieldCreationDateNormal.setVisible(key.getCreationTimeSeconds() != 0);
		if (key.getCreationTimeSeconds() != 0) {
			//spinnerCreationDate.setValue(new Date(key.getCreationTimeSeconds() * 1000));
			textFieldCreationDateNormal.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
					DateFormat.SHORT, Locale.getDefault()).format(new Date(key.getCreationTimeSeconds() * 1000)));
		}
		textFieldCreationDate.setText(date);
		//txtLabel.setText(address);
		txtPrivateKey.setText(key.getPrivateKeyEncoded(networkParameters).toString());
		String privateKey = txtPrivateKey.getText();
		if (privateKey != null) {
			btnPrivateKey.setIcon(new ImageIcon(new QRImage(privateKey, btnPrivateKey.getWidth(), btnPrivateKey.getHeight())));
		}
		//String address = txtAddress.getText();
		//System.out.println("Addres:" + address);
		if (address != null) {
			btnAddress.setIcon(new ImageIcon(new QRImage(address, btnAddress.getWidth(), btnAddress.getHeight())));
		}
	}

	@Override
	public int print(Graphics g, PageFormat pf, int page)
			throws PrinterException {
		//pf.setOrientation(PageFormat.LANDSCAPE);
		if (page > 0) {
	        return NO_SUCH_PAGE;
	    }

		// get the bounds of the component
        Dimension dim = panel.getSize();
        double cHeight = dim.getHeight();
        double cWidth = dim.getWidth();

        // get the bounds of the printable area
        double pHeight = pf.getImageableHeight();
        double pWidth = pf.getImageableWidth();

        double pXStart = pf.getImageableX();
        double pYStart = pf.getImageableY();

        double xRatio = pWidth / cWidth;
        double yRatio = pHeight / cHeight;


        Graphics2D g2 = (Graphics2D) g;
        g2.translate(pXStart, pYStart + (pf.getImageableHeight() - cHeight *xRatio) /2);
        g2.scale(xRatio, xRatio);//yRatio
        
        panel.paint(g2);
        
		/*
		// Get the preferred size ofthe component...
        Dimension compSize = panel.getPreferredSize();
        // Make sure we size to the preferred size
        //panel.setSize(compSize);
        // Get the the print size
        Dimension printSize = new Dimension();
        printSize.setSize(pf.getImageableWidth(), pf.getImageableHeight());

        // Calculate the scale factor
        double scaleFactor = getScaleFactorToFit(compSize, printSize);
        // Don't want to scale up, only want to scale down
        if (scaleFactor > 1d) {
            scaleFactor = 1d;
        }

        // Calcaulte the scaled size...
        double scaleWidth = compSize.width * scaleFactor;
        double scaleHeight = compSize.height * scaleFactor;

        // Create a clone of the graphics context.  This allows us to manipulate
        // the graphics context without begin worried about what effects
        // it might have once we're finished
        Graphics2D g2 = (Graphics2D) g.create();
        // Calculate the x/y position of the component, this will center
        // the result on the page if it can
        double x = ((pf.getImageableWidth() - scaleWidth) / 2d) + pf.getImageableX();
        double y = ((pf.getImageableHeight() - scaleHeight) / 2d) + pf.getImageableY();
        // Create a new AffineTransformation
        AffineTransform at = new AffineTransform();
        // Translate the offset to out "center" of page
        at.translate(x, y);
        // Set the scaling
        at.scale(scaleFactor, scaleFactor);
        // Apply the transformation
        g2.transform(at);
        // Print the component
        panel.printAll(g2);
        // Dispose of the graphics context, freeing up memory and discarding
        // our changes
        g2.dispose();

        panel.revalidate();
        */
        
		
		/*
	    Graphics2D g2d = (Graphics2D)g;
	    g2d.translate(pf.getImageableX(), pf.getImageableY());
	    
	    // Now we perform our rendering
	    g.drawString("Hello world!", 100, 100);

	    // Print the entire visible contents of a
	    // java.awt.Frame.
	    //panel.printAll(g);
	     * 
	     */

	    return PAGE_EXISTS;
	}
	
	public static double getScaleFactorToFit(Dimension original, Dimension toFit) {

        double dScale = 1d;

        if (original != null && toFit != null) {

            double dScaleWidth = getScaleFactor(original.width, toFit.width);
            double dScaleHeight = getScaleFactor(original.height, toFit.height);

            dScale = Math.min(dScaleHeight, dScaleWidth);

        }

        return dScale;

    }

    public static double getScaleFactor(int iMasterSize, int iTargetSize) {

        double dScale = 1;
        if (iMasterSize > iTargetSize) {

            dScale = (double) iTargetSize / (double) iMasterSize;

        } else {

            dScale = (double) iTargetSize / (double) iMasterSize;

        }

        return dScale;

    }
}
