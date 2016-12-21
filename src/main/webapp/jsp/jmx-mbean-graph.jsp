<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <link rel="stylesheet" href="../css/bootstrap.min.css">
        <script src="../js/jquery-3.1.1.min.js"></script>
        <script src="../js/bootstrap.min.js"></script>     
        <link rel="stylesheet" href="../bower_components/chartist/dist/chartist.min.css">  
    </head>

    <body>
    	<jsp:include page="top-navigation.jsp">
    		<jsp:param value="Graphs" name="page" />
    	</jsp:include>
    	<script src="../bower_components/chartist/dist/chartist.min.js"></script>
    	<script>
    		var currentAttributes = [];
    		
    		var options = {
   				axisX: {
   				    type: 'Chartist.FixedScaleAxis',
   				    divisor: 5,
   					labelInterpolationFnc: function(value) {
   			      		return value;
   			    	}
   				}
    		};
    		
    		$(document).ready(function() {
    			pollGraph();
    		});
    	
    		function showHideGraph(attributeId) {
				var buttonTxt = $('#showHide' + attributeId).text();
				
    			if (buttonTxt == 'View') {
    				$('#showHide' + attributeId).html('Hide');
    				
    				executeGet(attributeId);
    				
    				if ($.inArray(attributeId, currentAttributes) == -1) {
    					currentAttributes.push(attributeId);
    				}
    			} else {
    				$('#graph' + attributeId).html('');
    				$('#showHide' + attributeId).html('View');
    				
    				removePath(attributeId);
    			}
    		}
    		
    		function pollGraph() {
    			try {
	   				for (i = 0; i < currentAttributes.length; i++) {
	   					var attributeId = currentAttributes[i];
	   				
	   					executeGet(attributeId);
	   				}
	   				setTimeout(pollGraph, ${pollIntervalMs});
    			} catch (err) {
    				// Do nothing
    			}	
    		}
    		
    		function executeGet(attributeId) {
    			var filterId = $('#attributeFilter' + attributeId).val();
    			$.get('jmx-attribute-graph-ajax.html', {
					attributeId: attributeId,
					filterId: filterId
				}, function(jsonResponse) {
					if (jsonResponse.errorMessage != null) {
						$('#graph' + attributeId).html(jsonResponse.errorMessage);
						removePath(attributeId);
					} else {
						new Chartist.Line('#graph' + attributeId, jsonResponse);
					}
				});
    		}
    		
    		function removePath(attributeId) {
    			var index = $.inArray(attributeId, currentAttributes);
	       		if (index > -1) {
	       			currentAttributes.splice(index, 1);
	       		}
    		}
    		
    		function setFilter(attributeId, filterId) {
    			$('#attributeFilter' + attributeId).val(filterId);
    			if ($('#showHide' + attributeId).text() == 'Hide') {
    				executeGet(attributeId);
    			}
    		}
    	</script>	
        <div class="container">
            <h2>Attributes</h2>
            <c:choose>
				<c:when test="${empty jmxConfigured}">
	            	<table class="table">
	            		<tr>
	            			<td>The JMX connection has not yet been configured. Visit the <a href="/configuration.html">Configuration</a> page to connect to JMX.</td>
	            		</tr>
	            	</table>
	            </c:when>
	            <c:when test="${empty jmxObjectsSubscribed}">
	            	<table class="table">
	            		<tr>
	            			<td>No JMX attributes have been selected for monitoring. Visit the <a href="/object-name-selection.html">MBean</a> page to select attributes to monitor.</td>
	            		</tr>
	            	</table>
	            </c:when>
            	<c:otherwise>
		            <table class="table table-striped">
		                <thead>
		                    <tr>
		                        <td style="font-weight: bold">Object Name</td>
		                        <td style="font-weight: bold">Attribute</td>
		                        <td></td>
		                        <td></td>
		                    </tr>
		                </thead>
		                <c:forEach var="jmxObjectName" items="${jmxList}">
		                	<c:forEach var="jmxAttribute" items="${jmxObjectName.attributes}">
			                    <tr>
			                        <td>${jmxObjectName.canonicalName}</td>
			                        <td>${jmxAttribute.attributeDescription}</td>
			                        <td class="dropdown">
			                        	<button id="attributeFilter${jmxAttribute.attributeId}" class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown" value="1">Filter&nbsp;<span class="caret"></span></button>
									    <ul class="dropdown-menu">
									    	<c:forEach var="filter" items="${filters}">
									    		<li role="presentation" class="${liClass}">
									    			<a role="menuitem" href="#" onclick="setFilter('${jmxAttribute.attributeId}', '${filter.filterId}');">${filter.description}</a>
									    		</li>
									    	</c:forEach>
									    </ul>
			                        </td>
			                        <td>
			                        	<button id="showHide${jmxAttribute.attributeId}" type="submit" class="btn btn-primary" onclick="showHideGraph('${jmxAttribute.attributeId}');">View</button>
			                        </td>
			                    </tr>
			                    <tr>
			                    	<td colspan="3" id="graph${jmxAttribute.attributeId}"></td>
			                    </tr>
		                    </c:forEach>
		                </c:forEach>               
		            </table>
				</c:otherwise> 
			</c:choose>
        </div>
    </body>
</html>