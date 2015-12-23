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

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class AddressBook implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private List<Contact> contacts;
    private Preferences preferences;

    public AddressBook(String name, Preferences preferences) {
        this(name, new ArrayList<Contact>(), preferences);
    }

    public AddressBook(String name, List<Contact> contacts, Preferences preferences) {
        this.name = name;
        this.contacts = contacts;
        this.preferences = preferences;
    }

    public void addContact(Contact contact) {
        if (contacts != null) {
            contacts.add(contact);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public boolean equals(Object obj) {
        if (obj instanceof AddressBook) {
            AddressBook addressBook = (AddressBook) obj;
            return name.equals(addressBook.getName());
        }

        return false;
    }
    
    public void sort() {
    	Collections.sort(contacts, new ContactNameComparator());
    }

    public int hashCode() {
        return (name.length());
    }

    public static Set<Contact> getUniqueContacts(List<AddressBook> addressBooks) {

        Set<Contact> commonContacts = new HashSet<Contact>();
        Set<Contact> uniqueContacts = new HashSet<Contact>();

        for (AddressBook addressBook : addressBooks) {
            List<Contact> contacts = addressBook.getContacts();
            List<Contact> allContacts = new ArrayList<Contact>();
            allContacts.addAll(uniqueContacts);
            allContacts.addAll(contacts); 
            contacts.retainAll(uniqueContacts); 
            commonContacts.addAll(contacts); 
            allContacts.removeAll(commonContacts); 

            // set new uinque contacts
            uniqueContacts.clear();
            uniqueContacts.addAll(allContacts);

        }

        return uniqueContacts;

    }
    
	public void save(String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(contacts);
		oos.close();
	}

	public void load(String fileName) throws IOException,
			ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				fileName));
		contacts = (List<Contact>) ois.readObject();
		ois.close();
	}

	public void save() throws IOException, BackingStoreException,
			ClassNotFoundException {
		Preferences prefs = preferences.node(name);
		PrefObj.putObject(prefs, "contacts", contacts);
	}

	public void load() throws IOException, BackingStoreException,
			ClassNotFoundException {
		Preferences prefs = preferences.node(name);
		try {
			contacts = (List<Contact>) PrefObj.getObject(prefs, "contacts");
		} catch (EOFException e) {
			contacts = new ArrayList<Contact>();
		}
	}
}