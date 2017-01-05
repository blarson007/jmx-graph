<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <link rel="stylesheet" href="../bower_components/chartist/dist/chartist.min.css">  
        <link rel="stylesheet" href="../css/graph-style.css">
    </head>

    <body>
    	<jsp:include page="top-navigation.jsp">
    		<jsp:param value="Graphs" name="page" />
    	</jsp:include>
    	<script src="../bower_components/chartist/dist/chartist.min.js"></script>
    	<script src="../js/moment.min.js"></script>
    	<script>
    		var currentGraphs = [];
    		
    		$(document).ready(function() {
    			pollGraph();
    		});
    		
    		function showHideGraph(graphId) {
				var buttonTxt = $('#showHide' + graphId).text();
				
    			if (buttonTxt == 'View') {
    				$('#showHide' + graphId).html('Hide');
    				
    				executeGet(graphId);
    				
    				if ($.inArray(graphId, currentGraphs) == -1) {
    					currentGraphs.push(graphId);
    				}
    			} else {
    				$('#graph' + graphId).html('');
    				$('#showHide' + graphId).html('View');
    				
    				removePath(graphId);
    			}
    		}
    		
    		function pollGraph() {
    			try {
	   				for (i = 0; i < currentGraphs.length; i++) {
	   					var graphId = currentGraphs[i];
	   				
	   					executeGet(graphId);
	   				}
	   				setTimeout(pollGraph, ${pollIntervalMs});
    			} catch (err) {
    				// Do nothing
    			}	
    		}
    		
    		function executeGet(graphId) {
    			var filterId = $('#graphFilter' + graphId).val();
    			$.get('jmx-graph-ajax.html', {
    				graphId: graphId,
					filterId: filterId
				}, function(jsonResponse) {
					if (jsonResponse.errorMessage != null) {
						$('#graph' + graphId).html(jsonResponse.errorMessage);
						removePath(graphId);
					} else {
						new Chartist.Line('#graph' + graphId, jsonResponse.graphObject, {
							showPoint: false,
							axisX: {
							    type: Chartist.FixedScaleAxis,
								divisor: 10,
								labelInterpolationFnc: function(value) {
									return moment(value).format(jsonResponse.dateFormat);
								}
							},
							axisY: {
								offset: 60,
								onlyInteger: jsonResponse.onlyInteger,
								labelInterpolationFnc: function(value) {
									if (jsonResponse.graphType == 'memory') {
										return formatBytes(value, 2);
									} else if (jsonResponse.graphType == 'percentage') {
										return value + "%";
									}
									return value;
								}
							}
						});
					}
				});
    		}
    		
    		function removePath(graphId) {
    			var index = $.inArray(graphId, currentGraphs);
	       		if (index > -1) {
	       			currentGraphs.splice(index, 1);
	       		}
    		}
    		
    		function setFilter(graphId, filterId) {
    			$('#graphFilter' + graphId).val(filterId);
    			if ($('#showHide' + graphId).text() == 'Hide') {
    				executeGet(graphId);
    			}
    		}
    		
    		function formatBytes(bytes,decimals) {
				if(bytes == 0) return '0 Byte';
				var k = 1000; // or 1024 for binary
				var dm = decimals + 1 || 3;
				var sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
				var i = Math.floor(Math.log(bytes) / Math.log(k));
				return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
   			}
    	</script>	
        <div class="container">
            <h2>Graphs</h2>
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
		                        <td style="font-weight: bold">Graph Name</td>
		                        <td style="font-weight: bold">&nbsp;</td>
		                        <td></td>
		                        <td></td>
		                    </tr>
		                </thead>
		                <c:forEach var="jmxGraph" items="${jmxList}">
		                    <tr>
		                        <td>${jmxGraph.graphName}</td>
		                        <td>&nbsp;</td>
		                        <td class="dropdown">
		                        	<button id="graphFilter${jmxGraph.graphId}" class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown" value="1">Filter</button>
								    <ul class="dropdown-menu">
								    	<c:forEach var="filter" items="${filters}">
								    		<li><a href="#" onclick="setFilter('${jmxGraph.graphId}', '${filter.filterId}');">${filter.description}</a></li>
								    	</c:forEach>
								    </ul>
		                        </td>
		                        <td>
		                        	<button id="showHide${jmxGraph.graphId}" type="submit" class="btn btn-primary" onclick="showHideGraph('${jmxGraph.graphId}');">View</button>
		                        </td>
		                    </tr>
		                    <tr>
		                    	<td colspan="4" id="graph${jmxGraph.graphId}"></td>
		                    </tr>
		                </c:forEach>               
		            </table>
				</c:otherwise> 
			</c:choose>
        </div>
    </body>
</html>