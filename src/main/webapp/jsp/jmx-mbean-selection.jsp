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
    		var currentPaths = [];
    		
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
    	
    		function showHideGraph(pathId) {
				var buttonTxt = $('#showHide' + pathId).text();
				
    			if (buttonTxt == 'View') {
    				$('#showHide' + pathId).html('Hide');
    				
    				executeGet(pathId);
    				
    				if ($.inArray(pathId, currentPaths) == -1) {
    					currentPaths.push(pathId);
    				}
    			} else {
    				$('#graph' + pathId).html('');
    				$('#showHide' + pathId).html('View');
    				
    				removePath(pathId);
    			}
    		}
    		
    		function pollGraph() {
    			try {
	   				for (i = 0; i < currentPaths.length; i++) {
	   					var pathId = currentPaths[i];
	   				
	   					executeGet(pathId);
	   				}
	   				setTimeout(pollGraph, ${pollIntervalMs});
    			} catch (err) {
    				// Do nothing
    			}	
    		}
    		
    		function executeGet(pathId) {
    			var filterId = $('#pathFilter' + pathId).val();
    			$.get('jmx-attribute-selection.html', {
					pathId: pathId,
					filterId: filterId
				}, function(jsonResponse) {
					if (jsonResponse.errorMessage != null) {
						$('#graph' + pathId).html(jsonResponse.errorMessage);
						removePath(pathId);
					} else {
						new Chartist.Line('#graph' + pathId, jsonResponse);
					}
				});
    		}
    		
    		function removePath(pathId) {
    			var index = $.inArray(pathId, currentPaths);
	       		if (index > -1) {
	       			currentPaths.splice(index, 1);
	       		}
    		}
    		
    		function setFilter(pathId, filterId) {
    			$('#pathFilter' + pathId).val(filterId);
    			if ($('#showHide' + pathId).text() == 'Hide') {
    				executeGet(pathId);
    			}
    		}
    	</script>	
        <div class="container">
            <h2>Attributes</h2>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <td>Object Name</td>
                        <td>Attribute</td>
                        <td>Filter</td>
                        <td></td>
                    </tr>
                </thead>
                <c:forEach var="jmxAttributePath" items="${jmxList}">
                    <c:set var="classSuccess" value=""/>
                    <tr class="${classSuccess}">
                        <td>${jmxAttributePath.objectName}</td>
                        <td>${jmxAttributePath.attribute}</td>
                        <td class="dropdown">
                        	<button id="pathFilter${jmxAttributePath.pathId}" class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown" value="1">Filter&nbsp;<span class="caret"></span></button>
						    <ul class="dropdown-menu">
						    	<c:forEach var="filter" items="${filters}">
						    		<li role="presentation" class="${liClass}">
						    			<a role="menuitem" href="#" onclick="setFilter('${jmxAttributePath.pathId}', '${filter.filterId}');">${filter.description}</a>
						    		</li>
						    	</c:forEach>
						    </ul>
                        </td>
                        <td>
                        	<button id="showHide${jmxAttributePath.pathId}" type="submit" class="btn btn-primary" onclick="showHideGraph('${jmxAttributePath.pathId}');">View</button>
                        </td>
                    </tr>
                    <tr>
                    	<td colspan="3" id="graph${jmxAttributePath.pathId}"></td>
                    </tr>
                </c:forEach>               
            </table>
        </div>
    </body>
</html>