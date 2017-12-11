package com.crossbrowsertesting.teamcity.crossbrowsertestingPlugin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;

import com.crossbrowsertesting.api.LocalTunnel;
import com.crossbrowsertesting.api.Selenium;
import com.crossbrowsertesting.plugin.Constants;

import jetbrains.buildServer.agent.AgentBuildFeature;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.util.EventDispatcher;

import org.json.JSONArray;
import org.json.JSONObject;

public class LifeCycleAdapter extends AgentLifeCycleAdapter {
	
	LocalTunnel tunnel = null;
	Selenium se = new Selenium();
	
	public LifeCycleAdapter(@NotNull EventDispatcher<AgentLifeCycleListener> agentDispatcher) {
		agentDispatcher.addListener(this);
	}
	private Map<String, String> stringToMap(String mapAsString) {
		mapAsString = mapAsString.substring(1, mapAsString.length()-1); //remove curly brackets
		String[] keyValuePairs = mapAsString.split(","); //split the string to create key-value pairs
		Map<String,String> map = new HashMap<String, String>();
		for(String pair : keyValuePairs) //iterate over the pairs
		{
		    String[] entry = pair.split("="); //split the pairs to get key and value 
		    map.put(entry[0].trim(), entry[1].trim()); //add them to the hashmap and trim whitespaces
		}
		return map;
	}

	@Override
	public void buildStarted(@NotNull AgentRunningBuild runningBuild) {
		super.buildStarted(runningBuild);
		JSONArray seleniumConfigurations = new JSONArray();
		Collection<AgentBuildFeature> features = runningBuild.getBuildFeaturesOfType("com.crossbrowsertesting.teamcity");
		AgentBuildFeature feature = features.iterator().next();
		Map<String, String> params = feature.getParameters();
		String username = params.get("username");
		String apikey = params.get("apikey");
		
		// local tunnel

		if (params.get("useLocalTunnel") != null && params.get("useLocalTunnel").equals("true")) {

			tunnel = new LocalTunnel(username, apikey);
			if (!tunnel.isTunnelRunning) {
				runningBuild.getBuildLogger().message(Constants.TUNNEL_NEED_TO_START);
				try {
					tunnel.start(true);
					runningBuild.getBuildLogger().message(Constants.TUNNEL_WAITING);
					for (int i=1 ; i<15 && !tunnel.isTunnelRunning ; i++) {
						//will check every 2 seconds for upto 30 to see if the tunnel connected
						Thread.sleep(4000);
						tunnel.queryTunnel();
					}
					if (tunnel.isTunnelRunning) {
						runningBuild.getBuildLogger().message(Constants.TUNNEL_CONNECTED);
					}else {
						throw new Error(Constants.TUNNEL_START_FAIL);
					}
				}catch (URISyntaxException ue) {} catch(IOException ioe) {}catch (InterruptedException ie) {
					//log.finer("err: "+e);
					throw new Error(Constants.TUNNEL_START_FAIL);
				}
			} else {
				runningBuild.getBuildLogger().message(Constants.TUNNEL_NO_NEED_TO_START);
			}
		}

		// configure json selenium browsers
		JSONObject json = new JSONObject(params.get("selenium_configs"));
		
		for (Entry<String, Object> entry : json.toMap().entrySet()) {
			Map<String, String> config = stringToMap(entry.getValue().toString());
			// use the icon_class to figure out what the browsername is
			String browserName = "";
			String iconClass = se.operatingSystems2.get(config.get("operating_system") + "").browsers2.get(config.get("browser") + "").getIconClass();
			if (iconClass.equals("ie")) {
				browserName = "internet explorer";
			} else if (iconClass.equals("safari-mobile")) {
				browserName = "safari";
			} else {
				browserName = iconClass;
			}
			config.put("browserName", browserName);
			seleniumConfigurations.put(new JSONObject(config));
		}
		if (seleniumConfigurations.length() > 1) {
			runningBuild.addSharedEnvironmentVariable(Constants.BROWSERS, seleniumConfigurations.toString());
		}else if (seleniumConfigurations.length() == 1) {
			JSONObject browser = seleniumConfigurations.getJSONObject(0);
			runningBuild.addSharedEnvironmentVariable(Constants.BROWSER, browser.getString("browser"));
			runningBuild.addSharedEnvironmentVariable(Constants.RESOLUTION, browser.getString("resolution"));
			runningBuild.addSharedEnvironmentVariable(Constants.OPERATINGSYSTEM, browser.getString("operating_system"));
			runningBuild.addSharedEnvironmentVariable(Constants.BROWSERNAME, browser.getString("browserName"));
		}
		// set environment variables
		runningBuild.addSharedEnvironmentVariable(Constants.USERNAME, username);
		runningBuild.addSharedEnvironmentVariable(Constants.AUTHKEY, apikey);
		runningBuild.addSharedEnvironmentVariable(Constants.APIKEY, apikey);
		runningBuild.addSharedEnvironmentVariable(Constants.BUILDNAME, runningBuild.getProjectName() + "-" + Long.toString(runningBuild.getBuildId())); // looks like '{project name}-{unique build id}'
		runningBuild.addSharedEnvironmentVariable(Constants.BUILDNUMBER, runningBuild.getBuildNumber());
	}
	@Override
	public void beforeBuildFinish(AgentRunningBuild build, BuildFinishedStatus buildStatus) {
		if (tunnel != null && tunnel.pluginStartedTheTunnel) {
			try {
				tunnel.stop();
				build.getBuildLogger().message(Constants.TUNNEL_STOP);
			} catch (IOException ioe) {
				build.getBuildLogger().message(Constants.TUNNEL_STOP_FAIL);
			} catch (InterruptedException ie) {
				build.getBuildLogger().message(Constants.TUNNEL_STOP_FAIL);
			}
		}
	}
}
