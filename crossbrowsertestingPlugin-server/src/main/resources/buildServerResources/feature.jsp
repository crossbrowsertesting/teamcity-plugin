<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

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
	<th>Selenium</th>
	<td>
		<props:hiddenProperty id="selenium_configs" name="selenium_configs" value=""/>
    	<div id="se_configs">
    	</div>
    	<input type="button" id="add_selenium_test" value="Add a Selenium Test"/>
	</td>
</tr>
<!--
<tr>
	<th>Screenshots</th>
	<td>
		<props:hiddenProperty id="screenshots_browserlists" name="screenshots_browserlists" value=""/>
    	<div id="ss_browserlists">
    	</div>
    	<input type="button" id="add_screenshots_test" value="Add a Screenshots Test"/>
	</td>
</tr>
-->
</l:settingsGroup>
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
<script type="text/javascript">
var $ = jQuery; // have to set the shorthand manually.... not sure why...
const selenium_configurations = ${selenium_browsers};

var se_test_number = 0;

var se_configs = {};

var os_options = '';
for ( var i=0; i < selenium_configurations.length; i++) {
	  var config = selenium_configurations[i];
	  os_options += '<option value="'+config["api_name"]+'">'+config["name"]+'</option>';
}

$("#add_selenium_test").click(function() { // called when the add button is clicked
	se_test_number++;
	  // create the structure of the row
	var row = '<div class="container" id="'+se_test_number+'" name="container"><tr>';
	row += '<td><select class="operating_system" id="'+se_test_number+'" name="operating_system"></select></td>';
	row += '<td><select class="browser" id="'+se_test_number+'" name="browser"><option>SELECT AN OPERATING SYSTEM</option></select></td>';
	row += '<td><select class="resolution" id="'+se_test_number+'" name="resolution"><option>SELECT AN OPERATING SYSTEM</option></select></td>';
	row += '<td><input type="button" class="remove" value="Remove" id="'+se_test_number+'"/></td>'
	row += '</tr></div>';
	$('#se_configs').append(row);
	$( "select#"+se_test_number+".operating_system" ).html(os_options); // add the os's
	
	$( "select#"+se_test_number+".operating_system" ).change(setBrowsersAndResolutions);
	$( "select#"+se_test_number+".browser" ).change(setSeConfigsForBrowsersAndResolutions);
	$( "select#"+se_test_number+".resolution" ).change(setSeConfigsForBrowsersAndResolutions);

	$( "input#"+se_test_number+".remove" ).click(removeSeleniumTest);
});

function removeSeleniumTest() {
    const id = $(this).attr('id');
    $( "select#"+id+".operating_system" ).remove();
    $( "select#"+id+".browser" ).remove();
    $( "select#"+id+".resolution" ).remove();
    $( "input#"+id+".remove" ).remove();
}

var options = '';
for ( var i=0; i < selenium_configurations.length; i++) {
	var config = selenium_configurations[i];
	options += '<option value="'+config["api_name"]+'">' +config["name"]+'</option>';
}
$( "#operating_system" ).html(options);

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
function removeSeleniumTest() {
    const id = $(this).attr('id');
	delete se_configs[id];
	$( "div#"+id).remove();
}

function setBrowsersAndResolutions() { // when an os is selected...
    const selected_os = $(this).val();
    const id = $(this).attr('id');
    // set browsers
    getForOperatingSystem(selected_os, "browsers", function(browsers) {
    	var browser_options = '';
    	for ( var i=0; i < browsers.length; i++) {
	    	var browser = browsers[i];
		    browser_options += '<option value="'+browser["api_name"]+'">' +browser["name"]+'</option>';
  		}
    	$( "select#"+id+".browser" ).html(browser_options);
    });
    // set resolutions
    getForOperatingSystem(selected_os, "resolutions", function(resolutions) {
      var resolution_options = '';
  	  for ( var i=0; i < resolutions.length; i++) {
	  	  var res = resolutions[i];
  		  resolution_options += '<option value="'+res["name"]+'">' +res["name"]+'</option>';
	    }
  	  $( "select#"+id+".resolution" ).html(resolution_options);
    });
    setSeConfigs(id);
}
function setSeConfigsForBrowsersAndResolutions() {
    const id = $(this).attr('id');
    console.log("id = "+ id);
    setSeConfigs(id);
}

function setSeConfigs(id) {
	var se_test = {};
	se_test["operating_system"] = $( "select#"+id+".operating_system" ).val();
	se_test["browser"] = $( "select#"+id+".browser" ).val();
	se_test["resolution"] = $( "select#"+id+".resolution" ).val();
	console.log("se_test=");
	console.log(se_test);
	se_configs[id] = se_test;
	
    $( '#selenium_configs').val(JSON.stringify(se_configs));
    console.log( 'selenium_configs = ' );
    console.log(se_configs);
}
</script>
