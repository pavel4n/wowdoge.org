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

import java.awt.image.BufferedImage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.ByteMatrix;

public class QRImage extends BufferedImage {

	public QRImage(String text, int width, int height) {
		super(width, height, BufferedImage.TYPE_INT_RGB);
		
		// get a byte matrix for the data
		BitMatrix matrix = null;
		com.google.zxing.Writer writer = new QRCodeWriter();
		try {
			matrix = writer.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, width, height);
		} catch (com.google.zxing.WriterException e) {
			e.printStackTrace();
		}
		
		//System.out.println("Current width, height: " + width + " " + height);

		// generate an image from the byte matrix
		//width = matrix.getWidth();
		//height = matrix.getHeight();
		//System.out.println("Matrix width, height: " + width + " " + height);

		//byte[][] array = matrix.getArray();

		// create buffered image to draw to
		//BufferedImage image = new BufferedImage(width, height,
		//		BufferedImage.TYPE_INT_RGB);

		// iterate through the matrix and draw the pixels to the image
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				boolean grayValue = matrix.get(x, y);//array[y][x] & 0xff;
				this.setRGB(x, y, (grayValue ? 0 : 0xFFFFFF));
			}
		}
	}
}
