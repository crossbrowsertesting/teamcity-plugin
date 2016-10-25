<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%--
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
  --%>

<jsp:useBean id="keys" class="com.crossbrowsertesting.teamcity.crossbrowsertestingPlugin.ui.CBTServerKeyNames"/>

<script type="text/javascript" src="${jquery}"></script>

<l:settingsGroup title="Authentication">
<tr>
  <th>User Name:<l:star/></th>
  <td>
    <props:textProperty name="username" className="longField"/>
    <span class="error" id="error_username"></span>
    <span class="smallNote">CrossBrowserTesting.com API Username</span>
  </td>
</tr>
<tr>
  <th>API Key:<l:star/></th>
  <td>
    <props:passwordProperty name="apikey" className="longField"/>
    <span class="error" id="error_apikey"></span>
    <span class="smallNote">CrossBrowserTesting.com API Key</span>
  </td>
</tr>
</l:settingsGroup>
<l:settingsGroup title="Configuration">
<tr>
	<th>Selenium Test Directory</th>
	<td>
		<props:textProperty name="test_dir" className="longField"/>
    	<span class="error" id="test_dir"></span>
    	<span class="smallNote">Full path to the directory where the selenium tests reside.</span>
	</td>
</tr>
<tr>
	<th>Operating System</th>
	<td><props:selectProperty id="os" name="operatingsystem"></props:selectProperty></td>
</tr>
<tr>
	<th>Browser</th>
	<td><props:selectProperty id="browser" name="browser"></props:selectProperty></td>
</tr>
<tr>
	<th>Resolution</th>
	<td><props:selectProperty id="resolution" name="resolution"></props:selectProperty></td>
</tr>
</l:settingsGroup>
<!--
<l:settingsGroup title="Other">
<tr>
  <th>Use Local Tunnel</th>
  <td>
    <props:checkboxProperty name="useLocalTunnel" treatFalseValuesCorrectly="${true}" uncheckedValue="false"/>
    <span class="smallNote">Runs the nodejs cbt_tunnels module</span>
  </td>
</tr>
<tr>
  <th>Enable Test Results Pages</th>
  <td>
    <props:checkboxProperty name="useTestResults" treatFalseValuesCorrectly="${true}" uncheckedValue="false"/>
    <span class="smallNote">As tests finish, links will populate on the sidebar of the build that allow you to view the results from the test</span>
  </td>
</tr>
</l:settingsGroup>
-->
<script type="text/javascript" charset="utf-8">
var $ = jQuery; // have to set the shorthand manually.... not sure why...

var selenium_configurations = null;

function getForOperatingSystem(operatingSystem, whatToReturn, callback) {
	// common whatToReturn are "browsers" or "resolutions"
	for( var i=0; i < selenium_configurations.length; i++) {
		var config = selenium_configurations[i];
		if ( config["name"] == operatingSystem  || config["api_name"] == operatingSystem ) {
			callback(config[whatToReturn]);
			break;
		}
	}
};
	
var api_url = "https://crossbrowsertesting.com/api/v3";
var selenium_configurations = null;

// populate the selenium browsers variable

$.getJSON( api_url + "/selenium/browsers", function( configs ) {
	selenium_configurations = configs;
  var options = '';
	for ( var i=0; i < selenium_configurations.length; i++) {
		var config = selenium_configurations[i];
		options += '<option value="'+config["api_name"]+'">' +config["name"]+'</option>';
	}
	$( "#os" ).html(options);
});
$( "#os" ).change(function() { // when an os is selected...
  var selected_os = $( "#os option:selected" ).val();
  // set browsers
  getForOperatingSystem(selected_os, "browsers", function(browsers) {
    var options = '';
  	for ( var i=0; i < browsers.length; i++) {
	  	var browser = browsers[i];
		  options += '<option value="'+browser["api_name"]+'">' +browser["name"]+'</option>';
	  }
  	$( "#browser" ).html(options);
  });
  // set resolutions
  getForOperatingSystem(selected_os, "resolutions", function(resolutions) {
    var options = '';
  	for ( var i=0; i < resolutions.length; i++) {
	  	var res = resolutions[i];
		  options += '<option value="'+res["name"]+'">' +res["name"]+'</option>';
	  }
  	$( "#resolution" ).html(options);
  });
});

</script>