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

/*
 * RecentFilesMenu.java - A menu with items to open recently-opened files.
 */
package org.wowdoge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;


/**
 * A menu that keeps track of recently opened files.  Whenever a file is
 * opened, it is moved to the "top" of the list, as it is assumed that popular
 * files will get re-opened over and over again.
 */
public abstract class RecentFilesMenu extends JMenu {

	private int maxFileHistorySize;
	
	/**
	 * A cache of the full paths of files in this menu, to allow us to know
	 * the index of a specific item to move it to the top later.
	 */
	private List fileHistory;

	private static final int DEFAULT_MAX_SIZE = 256;
	private String RECENT_ITEM_STRING = "recent.item."; //$NON-NLS-1$
	private Preferences prefNode;
	

	/**
	 * Constructor.
	 *
	 * @param name The name for this menu item, e.g. "Recent Files".
	 */
	public RecentFilesMenu(String name, Preferences prefNode) {
		this(name, (List)null, prefNode);
	}


	/**
	 * Constructor.
	 *
	 * @param name The name for this menu item, e.g. "Recent Files".
	 * @param initialContents The initial contents of this menu.
	 */
	public RecentFilesMenu(String name, List initialContents, Preferences prefNode) {
		super(name);
		this.prefNode = prefNode;
		this.maxFileHistorySize = DEFAULT_MAX_SIZE;
		fileHistory = new ArrayList(maxFileHistorySize);
		if (initialContents == null) {
			initialContents = this.loadFromPreferences();
		}
		if (initialContents!=null) {
			for (Iterator i=initialContents.iterator(); i.hasNext(); ) {
				addFileToFileHistory((String)i.next());
			}
		}
	}


	/**
	 * Constructor.
	 *
	 * @param name The name for this menu item, e.g. "Recent Files".
	 * @param initialContents The initial contents of this menu.
	 */
	public RecentFilesMenu(String name, String[] initialContents, Preferences prefNode) {
		this(name, initialContents!=null ?
				Arrays.asList(initialContents) : (List)null, prefNode);
	}


	/**
	 * Adds the file specified to the file history.
	 *
	 * @param fileFullPath Full path to a file to add to the file history in
	 *        the File menu.
	 * @see #getShouldIgnoreFile(String)
	 */
	public void addFileToFileHistory(String fileFullPath) {

		System.out.println(fileFullPath);
		if (getShouldIgnoreFile(fileFullPath)) {
			return;
		}

		JMenuItem menuItem;

		// If the file history already contains this file, remove it and add
		// it back to the top of the list; this keeps the list in a "most
		// recently opened" order.
		int index = fileHistory.indexOf(fileFullPath);
		if (index>-1) {
			// Remove it physically from the menu and add it back at the
			// top, then remove it from the path history and it its path
			// to the top of that.
			menuItem = (JMenuItem)getMenuComponent(index);
			remove(index);
			insert(menuItem, 0);
			Object temp = fileHistory.remove(index);
			fileHistory.add(0, temp);
			return;
		}

		// Add the new file to the top of the file history list.
		menuItem = new JMenuItem(createOpenAction(fileFullPath));
		insert(menuItem, 0);
		fileHistory.add(0, fileFullPath);

		// Too many files?  Oust the file in history added least recently.
		if (getItemCount()>maxFileHistorySize) {
			remove(getItemCount()-1);
			fileHistory.remove(fileHistory.size()-1);
		}

	}
	
	public void removeFileFromHistory(String fileFullPath) {
		// If the file history already contains this file, remove it.
		int index = fileHistory.indexOf(fileFullPath);
		if (index>-1) {
			// Remove it physically from the menu
			remove(index);
			fileHistory.remove(index);
		}
		storeToPreferences();
	}


	/**
	 * Creates the action that will open the specified file in the application
	 * when its menu item is selected.  Subclasses should override.
	 *
	 * @param fileFullPath The selected file.
	 * @return The action that will be invoked.
	 */
	protected abstract Action createOpenAction(String fileFullPath);


	/**
	 * Returns the full path to the specified file in this history.
	 *
	 * @param index The index of the file.
	 * @return The file.
	 * @see #getFileHistory()
	 */
	public String getFileFullPath(int index) {
		return (String)fileHistory.get(index);
	}


	/**
	 * Returns an ordered copy of the recent files.  This can be used
	 * to save them when closing an application.
	 *
	 * @return The file history.  This will be an empty list if no files
	 *         have been opened.
	 * @see #getFileFullPath(int)
	 */
	public List getFileHistory() {
		return new ArrayList(fileHistory);
	}


	/**
	 * Returns the maximum number of files the file history in the File menu
	 * will remember.
	 *
	 * @return The maximum size of the file history.
	 */
	public int getMaximumFileHistorySize() {
		return maxFileHistorySize;
	}


	/**
	 * Provides a hook for subclasses to not remember specific files.  If
	 * <code>addFileToFileHistory()</code> is called but the file specified
	 * does not pass this method's criteria, it will not get added to history.
	 * The default implementation does nothing.
	 *
	 * @param fileFullPath The file to (possibly) ignore.
	 * @return Whether to ignore the file.  The default implementation always
	 *         returns <code>false</code>.
	 * @see #addFileToFileHistory(String)
	 */
	protected boolean getShouldIgnoreFile(String fileFullPath) {
		return false;
	}


	/**
	 * Sets the maximum number of files to remember in history.
	 *
	 * @param newSize The new size of the file history.
	 */
	public void setMaximumFileHistorySize(int newSize) {

		if (newSize<0)
			return;

		// Remember the new size.
		maxFileHistorySize = newSize;

		// If we're bigger than the new max size, trim down
		while (getItemCount()>maxFileHistorySize) {
			remove(getItemCount()-1);
			fileHistory.remove(fileHistory.size()-1);
		}

	}

	private ArrayList loadFromPreferences() {
		ArrayList list = new ArrayList(maxFileHistorySize);
		// load recent files from properties
		for (int i = 0; i < maxFileHistorySize; i++) {
			String val = prefNode.get(RECENT_ITEM_STRING + i, ""); //$NON-NLS-1$

			if (!val.equals("")) //$NON-NLS-1$
			{
				list.add(i, val); // m_items.add(val);
			} else {
				break;
			}
		}
		return list;
	}
	    
	public void storeToPreferences() {
		for (int i = 0; i < maxFileHistorySize; i++) {
			if (i < fileHistory.size()) {
				prefNode.put(RECENT_ITEM_STRING + i,
						(String) fileHistory.get(i)); // (String)m_items.get(i));
			} else {
				prefNode.remove(RECENT_ITEM_STRING + i);
			}
		}
	}
}