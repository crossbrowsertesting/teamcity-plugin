package com.crossbrowsertesting.teamcity.crossbrowsertestingPlugin;

public class InfoPrototype {
	/*
	 * Almost all of the JSON Objects have a "name" and "api_name"
	 * Doing it this way just because I'm lazy
	 */
	private String name;
	private String api_name;
	private String icon_class;
	public InfoPrototype(String name) {
		this.name = name;
		this.api_name = "";
	}
	public InfoPrototype(String api_name, String name) {
		this.api_name = api_name;
		this.name = name;
	}
	public InfoPrototype(String api_name, String name, String icon_class) {
		this.api_name = api_name;
		this.name = name;
		this.icon_class = icon_class;
	}
	public String getName() {
		return name;
	}
	public String getApiName() {
		if (api_name.isEmpty() || api_name == null) {
			return name;
		}else {
			return api_name;
		}
	}
	public String getIconClass() {
		return icon_class;
	}
	public String toString() {
		if (name.isEmpty() || name == null) {
			return api_name;
		}else {
			return name;
		}
	}
}