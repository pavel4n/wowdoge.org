package org.wowdoge;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.swing.BoxLayout;

public class DialogWebcamQRScan extends JDialog implements Runnable, ThreadFactory {

	private final JPanel contentPanel = new JPanel();
	private Webcam webcam;
	private Executor executor = Executors.newSingleThreadExecutor(this);
	private String scannedQRCodeText;
	private BufferedImage scannedImage;
	private boolean dialogResultOK;
	private JPanel panelForCam;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogWebcamQRScan dialog = new DialogWebcamQRScan();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogWebcamQRScan() {
		setTitle("Scan QR Code via Webcam");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		{
			panelForCam = new JPanel();
			contentPanel.add(panelForCam);
		}
		{	
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Close");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dialogResultOK = false;
						webcam.close();
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setupWebcam();
	}
	
	private void setupWebcam() {
		Dimension size = WebcamResolution.QVGA.getSize();

		webcam = Webcam.getWebcams().get(0);
		webcam.setViewSize(size);

		WebcamPanel panel = new WebcamPanel(webcam);
		panel.setPreferredSize(size);
		
		panelForCam.add(panel);
		
		executor.execute(this);
	}

	@Override
	public void run() {

		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Result result = null;
			BufferedImage image = null;

			if (webcam.isOpen()) {

				if ((image = webcam.getImage()) == null) {
					continue;
				}

				LuminanceSource source = new BufferedImageLuminanceSource(image);
				BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

				try {
					result = new MultiFormatReader().decode(bitmap);
				} catch (NotFoundException e) {
					// fall thru, it means there is no QR code in image
				}
			}

			if (result != null) {
				//textarea.setText(result.getText());
				//System.out.println(result.getText());
				scannedQRCodeText = result.getText();
				scannedImage = image;
				dialogResultOK = true;
				webcam.close();
				dispose();
			}

		} while (true);
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, "scanner");
		t.setDaemon(true);
		return t;
	}
	
	
	public boolean showDialog() {
		setModal(true);
		setVisible(true);
		return dialogResultOK;
	}
	
	public String getScannedQRCodeText() {
		return scannedQRCodeText;
	}
	
	public BufferedImage getScannedImage() {
		return scannedImage;
	}
}
