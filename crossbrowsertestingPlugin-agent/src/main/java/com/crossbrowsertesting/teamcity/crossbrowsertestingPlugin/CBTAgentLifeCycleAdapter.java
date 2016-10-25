package com.crossbrowsertesting.teamcity.crossbrowsertestingPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import jetbrains.buildServer.agent.AgentBuildFeature;
import jetbrains.buildServer.agent.AgentLifeCycleAdapter;
import jetbrains.buildServer.agent.AgentLifeCycleListener;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.util.EventDispatcher;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CBTAgentLifeCycleAdapter extends AgentLifeCycleAdapter {
		
	public CBTAgentLifeCycleAdapter(@NotNull EventDispatcher<AgentLifeCycleListener> agentDispatcher) {
		agentDispatcher.addListener(this);
	}
}
