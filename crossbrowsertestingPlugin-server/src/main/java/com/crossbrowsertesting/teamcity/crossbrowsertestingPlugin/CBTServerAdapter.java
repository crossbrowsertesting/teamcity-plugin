package com.crossbrowsertesting.teamcity.crossbrowsertestingPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.crossbrowsertesting.teamcity.crossbrowsertestingPlugin.ui.CBTBuildFeature;

import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildFeatureDescriptor;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.buildLog.BuildLog;
import jetbrains.buildServer.serverSide.buildLog.MessageAttrs;

public class CBTServerAdapter extends BuildServerAdapter {
    
	private final SBuildServer myBuildServer;
	
	public CBTServerAdapter(SBuildServer sBuildServer) {
        myBuildServer = sBuildServer;
    }
    
	public void register() {
		myBuildServer.addListener(this);
	}
	@Override
	public void beforeBuildFinish(SRunningBuild runningBuild) {
		Collection<SBuildFeatureDescriptor> features = runningBuild.getBuildType().getBuildFeaturesOfType("com.crossbrowsertesting.teamcity");
		Map<String, String> params = new HashMap<String, String>();
		String teamCityVersion = "";
		for (SBuildFeatureDescriptor feature : features) {
			//buildFeature =  (CBTBuildFeature) feature.getBuildFeature();
			params = feature.getParameters();
			teamCityVersion = Byte.toString(myBuildServer.getServerMajorVersion()) + "." + Byte.toString(myBuildServer.getServerMinorVersion());
		}
		BuildLog logger = runningBuild.getBuildLog();
		PluginCheck pluginCheck = new PluginCheck(teamCityVersion);
		try {
			if (pluginCheck.needToUpgradeFake()) {
				logger.message("This version of the CrossBrowserTesting.com Plugin is no longer supported. Please update to the latest version.", Status.ERROR, MessageAttrs.attrs());
				return;
			}
		} catch (IOException e) {}
		logger.message("\n-----------------------", Status.NORMAL, MessageAttrs.attrs());
		logger.message("SCREENSHOT TEST RESULTS", Status.NORMAL, MessageAttrs.attrs());
		logger.message("-----------------------", Status.NORMAL, MessageAttrs.attrs());
    	String username = params.get("username");
    	String apikey = params.get("apikey");
    	String buildname = runningBuild.getFullName();
    	String buildnumber = runningBuild.getBuildNumber();
    	String operatingSystemApiName = params.get("operatingsystem");
    	String browserApiName = params.get("browser");
    	String resolution = params.get("resolution");
    	File workingDir = new File(params.get("test_dir")); 
        logger.message("Working Directory is: "+workingDir, Status.NORMAL, MessageAttrs.attrs());
        for (File executable : workingDir.listFiles()) {
    		ProcessBuilder pb = new ProcessBuilder();
    		pb.directory(workingDir);
    		
        	Map<String, String> pb_env = pb.environment();
        	Map<String, String> env = new HashMap<String, String>();
	    	env.put("CBT_USERNAME", username);
	    	env.put("CBT_APIKEY", apikey);
	    	env.put("CBT_BUILD_NAME", buildname);
	    	env.put("CBT_BUILD_NUMBER", buildnumber);
	    	env.put("CBT_OPERATING_SYSTEM", operatingSystemApiName);
	    	env.put("CBT_BROWSER", browserApiName);
	    	env.put("CBT_RESOLUTION", resolution);
	    	
        	String fileName = executable.getName();
        	//Extract extension
        	String extension = "";
        	int l = fileName.lastIndexOf('.');
        	if (l > 0) {
        		extension = fileName.substring(l+1);
        	}					
		
        	// supported extensions
        	//if (extension.equals("py") || extension.equals("rb") || extension.equals("jar") || extension.equals("js") || (extension.equals("exe")) || extension.equals("sh") || extension.equals("bat")) {
        	if (extension.equals("py") || extension.equals("rb") || extension.equals("jar") || (extension.equals("exe")) || extension.equals("sh") || extension.equals("bat")) {

            	logger.message("Test Found is: "+executable, Status.NORMAL, MessageAttrs.attrs());
        		boolean isJavascriptTest = false; // JS selenium tests have an extra cap
        		List<String> cmd = new LinkedList<String>();
        		// figure out how to launch it					
        		//if (extension.equals("py") || extension.equals("rb") || extension.equals("jar") || extension.equals("js") || extension.equals("sh")) { //executes with full filename
            	if (extension.equals("py") || extension.equals("rb") || extension.equals("jar") || extension.equals("sh")) { //executes with full filename

        			if (extension.equals("py")) { //python
        				cmd.add("python");
        			}else if (extension.equals("rb")) { //ruby
        				cmd.add("ruby");
        			}else if (extension.equals("jar")) { //java jar
						cmd.add("java");
						cmd.add("-jar");
					//}else if (extension.equals("js")) { //node javascript
					//	cmd.add("node");
					//	isJavascriptTest = true;
					}else if (extension.equals("sh")) { // custom shell script
						cmd.add("sh");
						isJavascriptTest = true;
					}
					cmd.add(executable.getName());
				} else if (extension.equals("exe") || extension.equals("bat")) { //exe csharp
					File csharpScriptPath = new File(workingDir, executable.getName()); 
					cmd.add(csharpScriptPath.toString());
					isJavascriptTest = true;
				}
				if (isJavascriptTest) {
					/*
					// Javascript Selenium Tests have an extra capability "browserName"
					String browserIconClass = seleniumBrowserList.getIconClass(operatingSystemApiName, browserApiName);
					String browserName = "";
					if (browserIconClass.equals("ie")) {
						browserName = "internet explorer";
					} else if (browserIconClass.equals("safari-mobile")) {
						browserName = "safari";
					} else {
						browserName = browserIconClass;
					}
					env.put("CBT_BROWSERNAME", browserName);
					*/
				}
    	    	pb_env.putAll(env);
        		try {
            		pb.command(cmd);
					
			    	// log the environment variables to the Jenkins build console
            		logger.message("\nEnvironment Variables", Status.NORMAL, MessageAttrs.attrs());
			    	logger.message("---------------------", Status.NORMAL, MessageAttrs.attrs());
			    	for (Map.Entry<String, String> envvar : env.entrySet()) {
				    	logger.message(envvar.getKey() + ": "+ envvar.getValue(), Status.NORMAL, MessageAttrs.attrs());
			    	}
			    	pb.redirectErrorStream(true);
			    	Process pr = pb.start();
			    	logger.message("\nErrors/Output", Status.NORMAL, MessageAttrs.attrs());
			    	logger.message("-------------", Status.NORMAL, MessageAttrs.attrs());
					//write the output from the script to the console
			    	BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			    	String line;
			    	while ((line = in.readLine()) != null) {
			    		logger.message(line, Status.NORMAL, MessageAttrs.attrs());
			    	}
			    	pr.waitFor();

			    	in.close();
			    	logger.close();
        		}catch (IOException ioe) {
        			logger.message(ioe.getMessage(), Status.ERROR, MessageAttrs.attrs());
        			logger.close();
        		} catch (InterruptedException ie) {
        			logger.message(ie.getMessage(), Status.ERROR, MessageAttrs.attrs());
        			logger.close();
				}
        	}
        }
	}
}
