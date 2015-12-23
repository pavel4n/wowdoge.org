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

import java.io.Serializable;
import java.util.Comparator;

public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String address;
    private float amount;
    private String description;

    public Contact(String name, String address, String description, float amount) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public float getAmount() {
    	return amount;
    }
    
    public void setAmount(float amount) {
    	this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String toString() {
        return name + ", " + address;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Contact) {
            Contact contact = (Contact) obj;
            return (name.equals(contact.getName()) && address.equals(contact.getAddress()) && (amount == contact.getAmount()) && description.equals(contact.getDescription()));
        }

        return false;
    }

    public int hashCode() {
        return (name.length() + address.length());
    }
}

class ContactNameComparator implements Comparator<Contact> {
    public int compare(Contact contact1, Contact contact2) {
        return contact1.getName().compareToIgnoreCase(contact2.getName());
    }
}
