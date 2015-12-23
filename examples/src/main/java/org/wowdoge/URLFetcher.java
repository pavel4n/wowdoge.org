package org.wowdoge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class URLFetcher extends Thread {
	protected String url;
	protected String result;
	protected String errorMessage;
	protected boolean finished;
	
	public URLFetcher(String url) {
		this.url = url;
		this.result = null;
		this.finished = false;
		this.errorMessage = null;
	}
	
	public boolean isFinished() {
		return finished;
	}

	public String getResult() {
		return result;
	}
	
	public void run() {
		try {
			fetchURL();
		} catch (Exception e) {
			errorMessage = "Error during fetching of versions history!\n" + e.getLocalizedMessage();
			this.result = null;
			System.err.println(errorMessage);
			e.printStackTrace();
		}
		this.finished = true;
	}
	
	public void fetchURL() throws IOException {
		System.out.println("fetchURL()");
		InputStream input = null;
		try {
			URL url = new URL(this.url);
			input = url.openStream();
			StringBuilder inputStringBuilder = new StringBuilder();
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		    String line = bufferedReader.readLine();
		    while(line != null){
		        inputStringBuilder.append(line);inputStringBuilder.append('\n');
		        line = bufferedReader.readLine();
		    }
		    result = inputStringBuilder.toString();
		} finally {
			input.close();
		}
		System.out.println("fetchURL() END");
	}

}
