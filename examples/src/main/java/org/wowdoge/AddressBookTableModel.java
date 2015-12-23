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

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.Component;
import java.awt.Color;
import java.util.Locale;
import java.util.prefs.Preferences;

class AddressBookTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	AddressBookTableModel model = (AddressBookTableModel) table.getModel();
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        //c.setBackground(model.getRowColour(row));
        //c.setForeground(model.getRowForegroundColour(row));
        return c;
    }
}

class AddressBookTableModel extends AbstractTableModel {
		private boolean DEBUG = false;
		private AddressBook addressBook;
		private Color green = Color.GRAY; //new Color(0x008f00, false);
		private Color red = Color.WHITE;//new Color(0xce443e, false);
		
        private String[] columnNames = {"Name",
        								"Address",
        								"Description",
                                        "Amount [" + Version.coinName + "]"
                                        };
        private Object[][] data = {
        {"Kathy", "Smith",
         "Snowboarding", new Integer(5), new Boolean(false)},
        {"John", "Doe",
         "Rowing", new Integer(3), new Boolean(true)},
        {"Sue", "Black",
         "Knitting", new Integer(2), new Boolean(false)},
        {"Jane", "White",
         "Speed reading", new Integer(20), new Boolean(true)},
        {"Joe", "Brown",
         "Pool", new Integer(10), new Boolean(false)}
        };
        
        public AddressBookTableModel(Preferences preferences) {
        	addressBook = new AddressBook("AddressBook", preferences);
        }
        
        public void setAddressBook(AddressBook addressBook) {
        	this.addressBook = addressBook;
        	fireTableDataChanged();
        }
        
        public AddressBook getAddressBook() {
        	return addressBook;
        }
 
        public int getColumnCount() {
            return columnNames.length;
        }
 
        public int getRowCount() {
        	if (addressBook != null)
        		return addressBook.getContacts().size(); //data.length;
        	else
        		return 0;
        }
 
        public String getColumnName(int col) {
            return columnNames[col];
        }
 
        public Object getValueAt(int row, int col) {
        	Contact c = addressBook.getContacts().get(row);
        	switch (col) {
        		case 0:
        			return c.getName();
        		case 1:
        			return c.getAddress();
        		case 2:
        			return c.getDescription();
        		case 3:
        			return c.getAmount();
        	}
            return data[row][col];
        }
        
        public Color getRowColour(int row) {
        	return ((row % 2) == 0) ? Color.GRAY : Color.LIGHT_GRAY;
        }
        
        public Color getRowForegroundColour(int row) {
        	return ((row % 2) == 0) ? Color.WHITE : Color.BLACK;
        }
 
        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
 
        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
 //           if (col < 2) {
                return false;
 //           } else {
 //               return true;
 //           }
        }
 
        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
            if (DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                                   + " to " + value
                                   + " (an instance of "
                                   + value.getClass() + ")");
            }
 
            data[row][col] = value;
            fireTableCellUpdated(row, col);
 
            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
        }
 
        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();
 
            for (int i=0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                    System.out.print("  " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
    }