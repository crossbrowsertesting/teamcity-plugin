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

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crossbrowsertesting.api.Selenium;


public class SettingsPageController extends BaseController {
	private Selenium seleniumApi = new Selenium();

    @NotNull
    private final PluginDescriptor descriptor;
    //private Selenium se = new Selenium();

    public SettingsPageController(@NotNull final PluginDescriptor descriptor, @NotNull final WebControllerManager web) {
        this.descriptor = descriptor;
        web.registerController(descriptor.getPluginResourcesPath("feature.html"), this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull final HttpServletRequest request, @NotNull final HttpServletResponse response) throws Exception {
    	HashMap<String, Object> params = new HashMap<String, Object>();
    	//params.put("operating_systems", se.configurations);
    	params.put("jsp_home", this.descriptor.getPluginResourcesPath());
    	params.put("jquery", this.descriptor.getPluginResourcesPath("lib/jquery-3.1.0.min.js"));
    	params.put("selenium_browsers", seleniumApi.configurationsAsJson);
        return new ModelAndView(descriptor.getPluginResourcesPath("feature.jsp"), params);

    }
}