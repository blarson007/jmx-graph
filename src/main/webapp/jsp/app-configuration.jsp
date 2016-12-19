<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
       <link rel="stylesheet" href="../css/bootstrap.min.css"/> 
       <script src="../js/jquery-3.1.1.min.js"></script>        
       <script src="../js/bootstrap.min.js"></script>   
    </head>
    <body>
    	<script>
    		function testJmxConnection() {
    			$.get('configuration-ajax.html', {
					jmxHost: $('#jmxHost').val(),
					jmxPort: $('#jmxPort').val(),
					jmxUsername: $('#jmxUsername').val(), 
					jmxPassword:$('#jmxPassword').val()
				}, function(jsonResponse) {
					$('#messagePlaceholder span').text(jsonResponse.description);
				});
    		}
    	</script>
    	<jsp:include page="top-navigation.jsp">
    		<jsp:param value="Configuration" name="page" />
    	</jsp:include>
        <div class="container">
            <form action="/configuration.html" method="post" role="form" data-toggle="validator">
            	<input type="hidden" id="repositoryType" name="repositoryType" value="${config.repositoryType}"/>
                <c:if test ="${empty action}">                          
                    <c:set var="action" value="add"/>
                </c:if>
                <h2>Configuration</h2>
                <div class="form-group col-xs-4">
                    <label for="jmxHost" class="control-label col-md-4">JMX Host:</label>
                    <input type="text" name="jmxHost" id="jmxHost" class="form-control" placeholder="Required" value="${config.jmxConnectionConfig.jmxHost}" />
                    <c:if test="${jmxHostError != null}"><div class="error">${jmxHostError}</div></c:if>

                    <label for="jmxPort" class="control-label col-md-4">JMX Port:</label>                   
                    <input type="text" name="jmxPort" id="jmxPort" class="form-control" placeholder="Required" value="${config.jmxConnectionConfig.jmxPort}" />
                    <c:if test="${jmxPortError != null}"><div class="error">${jmxPortError}</div></c:if>

                    <label for="jmxUsername" class="control-label col-md-4 col-lg-6">JMX Username:</label>
                    <input type="text" name="jmxUsername" id="jmxUsername" class="form-control" value="${config.jmxConnectionConfig.jmxUsername}" />

                    <label for="jmxPassword" class="control-label col-md-4 col-lg-6">JMX Password:</label>
                    <input type="password" name="jmxPassword" id="jmxPassword" class="form-control" value="${config.jmxConnectionConfig.jmxPassword}" />

                    <label for="pollIntervalInSeconds" class="control-label col-md-4 col-lg-8">Poll Interval (seconds):</label>
                    <input type="text" name="pollIntervalInSeconds" id="pollIntervalInSeconds" class="form-control" placeholder="Required" value="${config.pollIntervalInSeconds}" />
                    <c:if test="${jmxPollIntervalError != null}"><div class="error">${jmxPollIntervalError}</div></c:if>

                    <br><div id="messagePlaceholder"><span>&nbsp;</span></div><br>
                    <button type="submit" class="btn btn-primary btn-md">Accept</button>
                    <button type="button" class="btn btn-secondary btn-md" onclick="testJmxConnection();">Test JMX Connection</button>
                </div>                                                      
            </form>
        </div>
    </body>
</html>