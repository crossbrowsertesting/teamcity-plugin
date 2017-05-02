<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>

<jsp:useBean id="build" scope="request" type="jetbrains.buildServer.serverSide.SBuild"/>
<jsp:useBean id="jobs" scope="request" type="java.util.ArrayList"/>
<head>
  <meta charset="utf-8">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<style>
	.iframe-container {
	    position: relative;
    	padding-bottom: 56.25%;
    	padding-top: 35px;
    	height: 0;
    	overflow: hidden;
	}
	.iframe-container iframe {
    	position: absolute;
    	top:0;
    	left: 0;
    	width: 100%;
    	height: 100%;
	}
</style>
<div class="container">
  <ul class="nav nav-tabs">
  	<c:forEach var="job" items="${jobs}">
  		<li><a data-toggle="tab" href="#${job.getTestUrl()}">${job.getDisplayName()}</a></li>
    </c:forEach>
  </ul>
  <div class="tab-content">
  	<c:forEach var="job" items="${jobs}">
  		<div id="${job.getTestUrl()}" class="tab-pane fade">
  			<h3>${job.getDisplayName()}</h3>
  			<div class="iframe-container">
  				<iframe src="https://app.crossbrowsertesting.com/public/ie63a397dda49cbe/selenium/${job.getTestId()}" height="1000" width="1024"></iframe>
  			</div>
  		</div>
  	</c:forEach>
  </div>
</div>

<!--
<c:choose>
    <c:when test="${param.jobId != null}">
        <div id="sauce-job" class="groupBox">
            <h2>Details for ${param.jobId}</h2>
            <script type="text/javascript"
                    src="https://saucelabs.com/job-embed/${param.jobId}.js?auth=${param.hmac}"></script>

        </div>
        <div>
            <script type="text/javascript">
                var iframe = document.getElementById('sauce-job').children[1];
                iframe.style.width = "1024px";
                iframe.style.height = "1000px";
            </script>
        </div>
    </c:when>
    <c:otherwise>


    </c:otherwise>
</c:choose>
-->