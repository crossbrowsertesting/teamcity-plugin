package com.crossbrowsertesting.teamcity.crossbrowsertestingPlugin;

import java.io.IOException;
import java.io.Serializable;

import com.crossbrowsertesting.api.Selenium;
import com.crossbrowsertesting.plugin.Constants;

public class SeleniumTest implements Serializable {
	private int testId;
	
	private String buildName;
	private String buildNumber;
	
	private String operatingSystem;
	private String browser;
	private String resolution;
	
	private Selenium se;
	
	public SeleniumTest(String name, String buildNumber, String os, String browser, String resolution, String username, String authkey, String pluginVersion, String contributerVersion) {
		this.buildName = name;
		this.buildNumber = buildNumber;
		this.operatingSystem = os;
		this.browser = browser;
		this.resolution = resolution;
		
		se = new Selenium(username, authkey);
		String seleniumTestId = "";
		try {
			seleniumTestId = se.getSeleniumTestId(name, buildNumber, browser, os, resolution);
			se.updateContributer(seleniumTestId, Constants.TEAMCITY_CONTRIBUTER, contributerVersion, pluginVersion);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.testId = Integer.parseInt(seleniumTestId);
	}
	
	public String getDisplayName() {
		String displayName = "CBT Selenium Test (" + this.operatingSystem + " " + this.browser + " " + this.resolution + ")";
		/*
	   	int maxCharactersViewable = 46;
	   	if (displayName.length() > maxCharactersViewable - 3) {
	   		// going to cut the string down and add "..."
	   		displayName = displayName.substring(0, maxCharactersViewable - 3);
	   		displayName += "...";
	   	}
	   	*/
	   	return displayName;
	}

	public String getTestUrl() {
		return getDisplayName().replaceAll("[:.()|/ ]", "").toLowerCase();
	}

	public String getTestId() {
		return Integer.toString(testId);
	}

	public String getBuildName() {
		return buildName;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public String getBrowser() {
		return browser;
	}

	public String getResolution() {
		return resolution;
	}
}
