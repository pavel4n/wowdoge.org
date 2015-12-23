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

import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.Component;

import com.google.dogecoin.core.ScriptException;
import com.google.dogecoin.core.Transaction;
import com.google.dogecoin.core.TransactionConfidence;
import com.google.dogecoin.core.TransactionInput;
import com.google.dogecoin.core.TransactionOutput;
import com.google.dogecoin.core.Utils;
import com.google.dogecoin.core.Wallet;

import static com.google.dogecoin.core.Utils.bitcoinValueToFriendlyString;

import java.awt.Color;
import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Locale;

class TransactionsTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	TransactionsTableModel model = (TransactionsTableModel) table.getModel();
    	Component c;
        c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); //label = new Label(value.toString());
		c.setBackground(model.getRowColour(table.convertRowIndexToModel(row)));
		c.setForeground(Color.WHITE);
		//System.out.println(value.getClass().toString());
   
        return c;
    }
    
    @Override 
    public void setValue(Object aValue) {
        Object result = aValue;
        if (aValue != null) {
        	//System.out.println(aValue.getClass().toString());
        	if (aValue instanceof TransactionConfidence) {
        		//System.out.println(aValue);
        		TransactionConfidence value = (TransactionConfidence) aValue;
        		int confirmations = value.getDepthInBlocks();
        		String state = null;
        		if (confirmations == 0) {
        			switch (value.getConfidenceType()) {
        				case DEAD:
        					state = "Dead.";
        					break;
        				case PENDING:
        					state = "Pending/Unconfirmed.";
        					break;
        				case UNKNOWN:
        					state = "Unknown.";
        					break;
        			}
        		}
        		setText((confirmations == 0) ? state : confirmations + " confirmations.");
        	} else if (aValue instanceof Date) {
				// System.out.println(aValue);
				Date value = (Date) aValue;
				String text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
						DateFormat.SHORT, Locale.getDefault()).format(value);
				setText(text);
        	} else if (aValue instanceof BigInteger) {
        		BigInteger value = (BigInteger) aValue;
        		setText(bitcoinValueToFriendlyString(value));
        	} else
        		super.setValue(result);
        } else
        	super.setValue(result);
      }   
}

class TransactionsTableModel extends AbstractTableModel {
		private boolean DEBUG = false;
		private List<Transaction> transactions;
		private Wallet wallet;
		private Color green = new Color(0x008f00, false);
		private Color red = new Color(0xce443e, false);
		//private boolean advanced = false;
		
        private String[] columnNames = {"Type",
        								"Date",
                                        "Amount [" + Version.coinName + "]",
                                        "Hash",
                                        "Confidence",
                                        "Ins",
                                        "Outs"
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
        
        public void setTransactions(List<Transaction> transactions, Wallet wallet) {
        	this.transactions = transactions;
        	this.wallet = wallet;
        	fireTableDataChanged();
        }
        
//        public void setAdvancedView(boolean value) {
//        	this.advanced = value;
//        	fireTableStructureChanged();
//        }
 
        public int getColumnCount() {
        	//if (advanced)
        		return columnNames.length;
        	//else
        	//	return columnNames.length - 2;
        }
 
        public int getRowCount() {
        	if (transactions != null)
        		return transactions.size(); //data.length;
        	else
        		return 0;
        }
 
        public String getColumnName(int col) {
            return columnNames[col];
        }
 
        @SuppressWarnings("deprecation")
		public Object getValueAt(int row, int col) {
        	if (transactions == null)
        		return "";
        	if (row < transactions.size()) {
        		Transaction t = transactions.get(row);
        		switch (col) {
        		case 0:
        			if (t.getValue(wallet).compareTo(BigInteger.ZERO) < 0)
        				return "SENT";
        			else
        				return "RECEIVED";
        		case 1:
        			return t.getUpdateTime();
        			//        					DateFormat.getDateTimeInstance(
        			//                            DateFormat.MEDIUM, 
        			//                            DateFormat.SHORT, 
        			//                            Locale.getDefault()).format(t.getUpdateTime());
        		case 2:
        			if (wallet != null)
        				return t.getValue(wallet); //new Float(bitcoinValueToFriendlyString(
        			else
        				return "";
        		case 3:
        			return t.getHashAsString();
        		case 4:
        			return t.getConfidence();
        		case 5:
        			String tempText = new String("");
        			List<TransactionInput> ins = t.getInputs();
        			for (TransactionInput i : ins) {
        				try {
        					TransactionOutput output = i.getOutpoint().getConnectedOutput();
        					if (output != null)
        						tempText = tempText + Utils.bitcoinValueToFriendlyString(output.getValue()) + " " + i.getFromAddress().toString() + " ";
        					else
        						tempText = tempText + " " + i.getFromAddress().toString() + " ";
        				} catch (ScriptException e) {
        					tempText = tempText + "COINBASE" + " ";
        				}
        				//System.out.println(i.getFromAddress());
        			}
        			return tempText; //t.getInputs().toString();
        		case 6:
        			String tempText1 = new String("");
        			List<TransactionOutput> outs = t.getOutputs();
        			for (TransactionOutput o : outs) {
        				tempText1 = tempText1 + Utils.bitcoinValueToFriendlyString(o.getValue()) + " " + o.getScriptPubKey().getToAddress(o.getParams()).toString() + " "; //toString()
        				//System.out.println(i.getFromAddress());
        			}
        			return tempText1; //t.getOutputs().toString();
        		}
        		return data[row][col];
        	} else {
        		return "";
        	}
        }
        
        public Color getRowColour(int row) {
        	Transaction t = transactions.get(row);
        	if (t.getValue(wallet).compareTo(BigInteger.ZERO) < 0)
        		return red;
        	else
        		return green;
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