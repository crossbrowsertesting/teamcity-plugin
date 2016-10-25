package com.crossbrowsertesting.teamcity.crossbrowsertestingPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

public class PluginCheck {
	private Request req = new Request("plugins");
	
	private String contributer = "TeamCity";
	private String contributerVersion = "";
	private String pluginVersion = "0.1";
	
	public PluginCheck(String contributerVersion) {
		this.contributerVersion = contributerVersion;
	}
	public boolean needToUpgradeFake() throws IOException{
		return false;
	}
	public boolean needToUpgrade() throws IOException {
		Map params = new HashMap();
		params.put("contributer", contributer);
		params.put("contributer_version", contributerVersion);
		params.put("plugin_version", pluginVersion);
		
		String json = req.get("", params);
		JSONObject jo = new JSONObject(json);
		return !jo.getBoolean("safe");
	}
	

}
