/*
*    This file is part of TeamCity Stash.
*
*    TeamCity Stash is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    TeamCity Stash is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with TeamCity Stash.  If not, see <http://www.gnu.org/licenses/>.
*/


package com.crossbrowsertesting.teamcity.crossbrowsertestingPlugin.ui;

import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CBTBuildFeature extends BuildFeature {
    private final PluginDescriptor descriptor;
    private String teamCityVersion;
    public Map<String, String> params;

    public CBTBuildFeature(@NotNull final PluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    
    public String getPluginVersion() {
    	return descriptor.getPluginVersion();
    }
    public String getTeamCityVersion() {
    	return teamCityVersion;
    }
    public void setTeamCityVersion(String version) {
    	this.teamCityVersion = version;
    }

    @NotNull
    @Override
    public String getType()
    {
        return "com.crossbrowsertesting.teamcity";
    }

    @NotNull
    @Override
    public String getDisplayName()
    {
        return "CrossBrowserTesting.com Build Feature";
    }

    @Nullable
    @Override
    public String getEditParametersUrl()
    {
        return descriptor.getPluginResourcesPath("feature.html");
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> params)
    {
        return "";

    }

    @Nullable
    @Override
    public PropertiesProcessor getParametersProcessor() {
        final CBTServerKeyNames keyNames = new CBTServerKeyNames();
        return new PropertiesProcessor() {
        	
            private void validate(@NotNull final Map<String, String> properties, @NotNull final String key, @NotNull final String message, @NotNull final Collection<InvalidProperty> res) {
                if (jetbrains.buildServer.util.StringUtil.isEmptyOrSpaces(properties.get(key))) {
                    res.add(new InvalidProperty(key, message));
                }
            }

            @NotNull
            public Collection<InvalidProperty> process(@Nullable final Map<String, String> propertiesMap) {
            	params = propertiesMap;
                final Collection<InvalidProperty> result = new ArrayList<InvalidProperty>();
                if (propertiesMap == null) {
                    return result;
                }

                validate(propertiesMap, "username", "Username must be specified", result);
                validate(propertiesMap, "apikey", "Apikey must be specified", result);

                return result;
            }
        };
    }

    @Nullable
    @Override
    public Map<String, String> getDefaultParameters()
    {
        final Map<String, String> map = new HashMap<String, String>();
        return map;
    }

    @Override
    public boolean isMultipleFeaturesPerBuildTypeAllowed()
    {
        return true;
    }
}