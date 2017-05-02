package com.crossbrowsertesting.teamcity.crossbrowsertestingPlugin.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import com.crossbrowsertesting.api.Account;
import com.crossbrowsertesting.teamcity.crossbrowsertestingPlugin.SeleniumTest;

import jetbrains.buildServer.serverSide.BuildsManager;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildFeatureDescriptor;
import jetbrains.buildServer.web.openapi.BuildTab;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;

public class ResultsTab extends BuildTab {
	
	public ResultsTab(WebControllerManager manager, BuildsManager buildManager, PluginDescriptor pluginDescriptor) {
		super("cbtBuildResults", "CBT Results", manager, buildManager, pluginDescriptor.getPluginResourcesPath("buildResults.jsp"));
	}
	private Map<String, String> stringToMap(String mapAsString) {
		mapAsString = mapAsString.substring(1, mapAsString.length()-1); //remove curly brackets
		String[] keyValuePairs = mapAsString.split(","); //split the string to create key-value pairs
		Map<String,String> map = new HashMap<String, String>();               
		for(String pair : keyValuePairs) { //iterate over the pairs
		    String[] entry = pair.split("="); //split the pairs to get key and value 
		    map.put(entry[0].trim(), entry[1].trim()); //add them to the hashmap and trim whitespaces
		}
		return map;
	}
	
    @Override
    protected void fillModel(@NotNull Map<String, Object> model, @NotNull SBuild build) {
        List<SeleniumTest> jobs = new ArrayList<SeleniumTest>();
        Collection<SBuildFeatureDescriptor> features = build.getBuildType().getBuildFeaturesOfType("com.crossbrowsertesting.teamcity");
        SBuildFeatureDescriptor feature = features.iterator().next();
        Map<String, String> params = feature.getParameters();
        String pluginVersion = ((CBTBuildFeature) feature.getBuildFeature()).getPluginVersion();
        String teamCityVersion = ((CBTBuildFeature) feature.getBuildFeature()).getTeamCityVersion();
		String username = params.get("username");
		String apikey = params.get("apikey");
		
    	Account userAccount = new Account(username, apikey);
    	userAccount.sendMixpanelEvent("TeamCity Plugin Downloaded");
		
    	JSONObject json = new JSONObject(params.get("selenium_configs"));
		for (Entry<String, Object> entry : json.toMap().entrySet()) {
			Map<String, String> config = stringToMap(entry.getValue().toString());
			if (!config.isEmpty()) {
				String buildNumber = build.getBuildNumber();
 				String buildName = build.getFullName();
				String os = config.get("operating_system");
				String browser = config.get("browser");
				String resolution = config.get("resolution");
				
				SeleniumTest st = new SeleniumTest(buildName, buildNumber, os, browser, resolution, username, apikey, pluginVersion, teamCityVersion);
				jobs.add(st);
			}
		}
        model.put("jobs", jobs);
    }
    
    @Override
    protected boolean isAvailableFor(@NotNull SBuild build) {
        Collection<SBuildFeatureDescriptor> features = build.getBuildType().getBuildFeaturesOfType("com.crossbrowsertesting.teamcity");
        SBuildFeatureDescriptor feature = features.iterator().next();
        if (feature == null) {
        	return false;
        } else if (!feature.getParameters().containsKey("useTestResults")) {
        	return false;
        } else if (feature.getParameters().containsKey("useTestResults") && feature.getParameters().get("useTestResults").equals("false")) {
            return false;
        } else {
        	return super.isAvailableFor(build);
        }
    }
    
}
