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

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class TextField extends JTextField {
	protected JMenuItem mntmPaste;
	
	public TextField() {
		super();
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(this, popupMenu);
		JMenuItem mntmCopy = new JMenuItem("Copy");
		final JTextField txtField = this;
		mntmCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection stringSelection = new StringSelection (txtField.getText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
				clpbrd.setContents (stringSelection, null);
			}
		});
		popupMenu.add(mntmCopy);
		mntmPaste = new JMenuItem("Paste");
		mntmPaste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* -- get system clipboard */

				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

				/* -- get clipboard context */

				Transferable data = clipboard.getContents(null);

				/* -- is context string type ? */

				boolean bIsText = ( ( data != null ) && ( data.isDataFlavorSupported( DataFlavor.stringFlavor ) ) );

				/* -- if yes, translate context to string type and write it */

				if ( bIsText ) {

				  try {

				    String s = (String)data.getTransferData( DataFlavor.stringFlavor );
				    txtField.setText(s);
				    //System.out.println( s );

				  } catch (UnsupportedFlavorException ex) {
				    ex.printStackTrace();
				  } catch (IOException ex) {
					  ex.printStackTrace();
				  }

				}
			}
		});
		popupMenu.add(mntmPaste);
	}
	
	public void setEditable(boolean editable) {
		super.setEditable(editable);
		if (mntmPaste != null)
			mntmPaste.setVisible(editable);
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
