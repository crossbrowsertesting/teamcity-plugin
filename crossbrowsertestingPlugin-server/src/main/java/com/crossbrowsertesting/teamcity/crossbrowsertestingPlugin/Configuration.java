package com.crossbrowsertesting.teamcity.crossbrowsertestingPlugin;

import java.util.ArrayList;
import java.util.List;

public class Configuration extends InfoPrototype{
	public List<InfoPrototype> resolutions = new ArrayList<InfoPrototype>();
	public List<InfoPrototype> browsers = new ArrayList<InfoPrototype>();

	public Configuration(String api_name, String name) {
		super(api_name, name);			
	}
	public List<InfoPrototype> getBrowsers() {
		return browsers;
	}
	public List<InfoPrototype> getResolutions() {
		return resolutions;
	}
	public String getResolutionApiName(String name) {
		String api_name="";
		for (InfoPrototype resolution : resolutions) {
			if (name.equals(resolution.getName())) {
				api_name = resolution.getApiName();
			}
		}
		return api_name;
	}
	public String getBrowsersApiName(String name) {
		String api_name="";
		for (InfoPrototype browser : browsers) {
			if (name.equals(browser.getName())) {
				api_name = browser.getApiName();
			}
		}
		return api_name;
	}
	
}