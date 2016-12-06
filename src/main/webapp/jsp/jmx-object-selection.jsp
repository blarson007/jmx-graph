<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
   <head>
        <link rel="stylesheet" href="../css/bootstrap.min.css">
        <script src="../js/jquery-3.1.1.min.js"></script>
        <script src="../js/bootstrap.min.js"></script>     
    </head>

    <body>
    	<jsp:include page="top-navigation.jsp">
    		<jsp:param value="MBeans" name="page" />
    	</jsp:include>
    	<script>
    		function toggleSubscribe(myObjectName, myAttribute, myAttributeType, pathIndex) {
    			$.post("object-name-selection.html", {
    				objectName: myObjectName,
    				attribute: myAttribute,
    				attributeType: myAttributeType
    			}, function(data) {
    				var text = $('#path' + pathIndex).text();
    				if (text == 'Select') {
    					$('#path' + pathIndex).html('Remove');
    				} else {
    					$('#path' + pathIndex).html('Select');
    				} 
    			});
    		}
    		
    		function toggleVisibility(id) {
    			var text = $('#button-' + id).text();
    			if (text == 'Expand') {
    				$('#button-' + id).html('Hide');
    				$('.' + id).show();
    			} else {
    				$('#button-' + id).html('Expand');
    				$('.' + id).hide();
    			}
    		}
    		
    		function fillModal(myObjectName) {
    			$.get("object-name-selection.html", {
    				objectName: myObjectName,
    			}, function(data) {
    				
    			});
    		}
    	</script>      
        <div class="container">
            <h2>MBeans</h2>
            <table class="table table-bordered">
                <c:forEach var="nameEntry" items="${objectNameMap}" varStatus="mapIter">
                	<tr class="dropdown">
                		<td style="background-color:gray;">${nameEntry.key}</td>
                		<td><button class="btn btn-info" id="button-${mapIter.index}" type="button" onclick="toggleVisibility('${mapIter.index}')">Expand</button></td>
                	</tr>	
       				<c:forEach var="path" items="${nameEntry.value}" varStatus="pathIndex">
       					<tr class="collapse ${mapIter.index}">
       						<td style="background-color: white;">${path.objectNameShortened}</td>
       						<td><button class="btn btn-info" type="button" data-toggle="modal" data-target="#myModal${pathIndex.index}" onclick="fillModal('${path.objectNameEscaped}')">Expand</button></td>
       					</tr>
       					<div class="modal fade" id="myModal${pathIndex.index}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
						  <div class="modal-dialog" role="document">
						    <div class="modal-content">
						      <div class="modal-header">
						        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
						          <span aria-hidden="true">&times;</span>
						        </button>
						        <h4 class="modal-title">Modal title</h4>
						      </div>
						      <div class="modal-body">
						        <p>This is the body text&hellip;</p>
						      </div>
						      <div class="modal-footer">
						        <button type="button" class="btn btn-secondary" data-dismiss="modal">Done</button>
						      </div>
						    </div>
						  </div>
						</div>
       				</c:forEach>
                </c:forEach>	
            </table>
        </div>
    </body>
</html>