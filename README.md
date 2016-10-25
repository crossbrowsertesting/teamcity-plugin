#CrossBrowserTesting.com TeamCity Plugin
#### This plugin integrates TeamCity users with Selenium Testing and Screenshot Testing on CrossBrowserTesting.com. CrossBrowserTesting.com provides cross browser testing of websites, webpages, and web applications on Windows, Macs, and real iPhones, iPads, and Android Phones and Tablets.
Wiki: https://github.com/crossbrowsertesting/teamcity-plugin/wiki/CrossBrowserTesting.com-TeamCity-Plugin-Wiki

### Installation

##### [via the interface][teamcity_install]
1. Click the **Administration**.
2. Click **Plugins List**.
3. Click the **Upload plugin zip**.
4. Add the zip file of the CrossBrowserTesting Plugin. Click **Save**.
5. Restart the TeamCity server.

##### [by hand][teamcity_install] (*not recommended*)
1. Download [crossbrowsertestingPlugin.zip][latest_version].
2. Save the downloaded zip file into your `<TeamCity Data Directory>/plugins` directory.
3. Restart the TeamCity server.

### Building the plugin for testing/development

##### Requirements:
- [JDK][java] &#8805; 8
- [Maven][maven] &#8805; 3

<pre>mvn package</pre>
The zip will be saved in the `target` directory.
##### To release
Make sure the pom.xml file's version has the new point release and has **-SNAPSHOT**
<pre> mvn release:prepare release:perform </pre>

[latest_version]: http://updates.jenkins-ci.org/latest/crossbrowsertesting.hpi
[maven]: https://maven.apache.org/index.html
[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[teamcity_install]: https://confluence.jetbrains.com/display/TCD10/Installing+Additional+Plugins