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

import javax.swing.AbstractListModel;

import com.google.dogecoin.core.Transaction;
import com.google.dogecoin.core.Wallet;

/**
 * @author pavel
 *
 */
public class AddressesListModel extends AbstractListModel {
	private List<String> addresses;

    public void setAddresses(List<String> addresses) {
    	int start = 0;
    	int end = addresses.size();
//    	if (this.addresses != null) {
//			int size1 = addresses.size();
//			int size2 = this.addresses.size();
//			int size = (size1 < size2) ? size1 : size2;
//			end = (size1 > size2) ? size1 : size2;
//			for (int i = 0; i < size; i++) {
//				if (!addresses.get(i).equals(this.addresses.get(i))) {
//					start = i;
//					break;
//				}
//			}
//    	}
    	this.addresses = addresses;
    	fireContentsChanged(this, start, end);
    }
	
	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public Object getElementAt(int arg0) {
		if (addresses != null)
			return addresses.get(arg0);
		else
			return "";
	}

	/* (non-Javadoc)
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		if (addresses != null)
			return addresses.size();
		else
			return 0;
	}

}
