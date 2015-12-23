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

import java.awt.Color;
import java.awt.SystemColor;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import javax.swing.UIManager;

public class Version {
	public static final String appName = "WowDoge"; //"WowDoge";
	public static final String currentVersion = "0.3.15.1";
	public static final String preferencesName = "org.wowdoge"; //org.wowdoge
	public static final String spvChainFileName = "dogecoins.dogespvchain"; //dogecoins.dogespvchain
	public static final String checkpointsFileName = "/org/wowdoge/wowdoge.checkpoints"; //"/org/wowdoge/wowdoge.checkpoints"
	public static final String defaultWalletFileName = "dogecoins.dogewallet"; //"dogecoins.dogewallet"
	public static final String walletFileExtension = "dogewallet"; //"dogewallet";
	public static final String walletFileExtensionWithDot = ".dogewallet"; //".dogewallet";
	public static final String walletFileDescription = "Wow DOGE Wallet Files"; //"Wow DOGE Wallet Files";
	public static final String coinImagePath = "/org/wowdoge/doge.png"; //doge
	public static final String coinName = "DOGE"; //"DOGE";
	public static final String coinNameWithSpace = "DOGE "; //"DOGE ";
	public static final String coinNameLong = "Dogecoin";
	public static final String coinNameLongSmall = "dogecoin";
	public static final String appIconPath = "/org/wowdoge/appicon.png"; //doge
	public static final String appAboutImagePath = "/org/wowdoge/appicon.png"; //doge
	public static final String logFileName = "wowdoge.log"; //wowdoge.log
	public static final String logFileNamePattern = "/wowdoge.%d.log.gz"; //wowdoge
	public static final String appWebsite = "http://www.wowdoge.org";
	public static final String appWebsiteWithoutHTTP = "www.wowdoge.org";
	public static final String coinLibraryName = "DogecoinJ"; //"DogecoinJ";
	public static final String donationAddress = "DLoawzLvw5WvvpGUkSTxrwMgKhkamsqFo7";
	public static final String supportEmailAddress = "info@wowdoge.org";
	public static final int intCurrentVersion = toIntVersion(currentVersion);
	public static List<Version> versions = null;
	protected static final String updateURL = "http://www.wowdoge.org/version.txt";
	private static String OS = System.getProperty("os.name").toLowerCase();
	protected static URLFetcher urlF;
	protected String version;
	protected int intVersion;
	protected Date date;
	protected String message;
	
	public Version(String version, Date date) {
		setVersion(version);
		this.date = date;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
		this.intVersion = toIntVersion(version);
	}
	
	public int getIntVersion() {
		return intVersion;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean isNewer() {
		System.out.println(intCurrentVersion);
		System.out.println(intVersion);
		return intCurrentVersion < intVersion; 
	}
	
	public int compareTo(Object anotherVersion) throws ClassCastException {
		if (!(anotherVersion instanceof Version))
			throw new ClassCastException("A Version object expected.");
		int intAnotherVersion = ((Version) anotherVersion).getIntVersion();
		return this.intVersion - intAnotherVersion;
	}
	
	public String toString() {
		return "Version " + version + " released on " + new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).format(date) + "\n" + message;
	}
	
	public static int toIntVersion(String version) {
		System.out.println(version);
		int multiplyBy = 1000;
		int intVersion = 0;
		String element[] = version.split("\\.");
		//System.out.println(element.length);
		//System.out.println(element);
		for(int counter=element.length - 1; counter >= 0;counter--){
			//System.out.println(element[counter]);
			intVersion = intVersion + new Integer(element[counter]).intValue() * multiplyBy;
			multiplyBy = multiplyBy * 100;
		}
		System.out.println(intVersion);
		return intVersion; 
	}
	
	public static void fetchVersionsInfo() {
		urlF = new URLFetcher(updateURL);
		urlF.start();
	}
	
	public static boolean isFetched() {
		if (urlF != null)
			return urlF.isFinished();
		return false;
	}
	
	public static void resetFetchedResult() {
		urlF = null;
	}
	
	public static boolean isNewVersion() throws ParseException, Exception {
		System.out.println(urlF.getResult());
		versions = parseVersionsInfo();
		for (Version v: versions) {
			System.out.println(v);
			if (v.isNewer()) {
				return true;
			}
		}
		return false;
	}
	
	public static List<Version> getVersions() {
		return versions;
	}
	
	public static List<Version> parseVersionsInfo() throws ParseException, Exception {
		versions = new ArrayList<Version>();
		Scanner scanner = new Scanner(new StringReader(urlF.getResult()));
		Version v = null;
		String message = "";
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
            if (line != null) {
            	if (line.startsWith("Version")) {
            		String[] element = line.split(" ");
            		message = "";
            		if (element.length >= 1) {
            			String version = element[1];
            			if (element.length >= 3) {
            				String day = element[3];
            				if (element.length >= 4) {
                				String month = element[4];
                				if (element.length >= 5) {
                    				String year = element[5];
                    				year = year.replaceAll(" .$", "");
                    				v = new Version(version,  new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH).parse(day + " " + month + " " + year));
                    			}
                			}
            			}
            		}
            	} else {
            		if (line.isEmpty()) {
            			if (v != null) {
            				v.setMessage(message);
            				//System.out.println(message);
            				versions.add(v);
            				v = null;
            			}
            		} else {
            			message = message + line + "\n";
            		}
            	}
            }
		}
		return versions;
	}
	
	public static Color getInactiveTextFieldColor() {
		if (isMac()) 
			return SystemColor.window;
		else
			return UIManager.getColor("TextField.inactiveBackground");
	}
	
	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}
	
	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}
 
	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}
 
	public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}
}
