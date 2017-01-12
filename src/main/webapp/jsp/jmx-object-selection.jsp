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
    		function toggleSubscribe(myObjectName, myAttribute, myAttributeType, attributeIndex) {
    			$.post("object-name-selection.html", {
    				objectName: myObjectName,
    				attributeName: myAttribute,
    				attributeType: myAttributeType
    			}, function(data) {
    				var text = $('#attribute' + attributeIndex).text();
    				if (text == 'Select') {
    					$('#attribute' + attributeIndex).html('Remove');
    				} else {
    					$('#attribute' + attributeIndex).html('Select');
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
    	</script>      
        <div class="container">
            <h2>MBeans</h2>
            <c:if test="${empty jmxConfigured}">
            	<table class="table">
            		<tr>
            			<td>The JMX connection has not yet been configured. Visit the <a href="/configuration.html">Configuration</a> page to connect to JMX.</td>
            		</tr>
            	</table>
            </c:if>
            <c:if test="${not empty jmxConfigured}">
	            <table class="table table-bordered">
	                <c:forEach var="nameEntry" items="${objectNameMap}" varStatus="mapIter">
	                	<tr class="dropdown">
	                		<td style="background-color: #f9f9f9">${nameEntry.key}</td>
	                		<td><button class="btn btn-primary" id="button-${mapIter.index}" type="button" onclick="toggleVisibility('${mapIter.index}')">Expand</button></td>
	                	</tr>	
	       				<c:forEach var="objectName" items="${nameEntry.value}" varStatus="objectNameIndex">
	       					<tr class="collapse ${mapIter.index}">
	       						<td style="background-color: white;">${objectName.objectNameShortened}</td>
	       						<td><button class="btn btn-info" type="button" data-toggle="modal" data-target="#myModal${objectNameIndex.index}">Expand</button></td>
	       					</tr>
	       					<div class="modal modal-lg fade" id="myModal${objectNameIndex.index}" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
							  <div class="modal-dialog" role="document">
							    <div class="modal-content" style="display: table;">
							      <div class="modal-header">
							        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
							          <span aria-hidden="true">&times;</span>
							        </button>
							        <h4 class="modal-title">Select Attributes</h4>
							      </div>
							      <div class="modal-body">
							        <div class="container">
							          <div>
					                    <div class="row">
					                        <div class="col-xs-2 col-sm-2 col-lg-3">Attribute Name</div>
					                        <div class="col-xs-2 col-sm-2 col-lg-3">Attribute Type</div>
					                        <div class="col-xs-2 col-sm-2 col-lg-3"></div>
					                    </div>
					                    <div class="divider">&nbsp;</div>
					                  </div>
							          <c:forEach var="attribute" items="${objectName.attributes}" varStatus="attributeIndex">
							            <c:set var="buttonText" value="Select" />
							            <div class="row">
							              <div class="col-xs-2 col-sm-2 col-lg-3" style="vertical-align: middle">${attribute.attributeName}</div>
							              <div class="col-xs-2 col-sm-2 col-lg-3" style="vertical-align: middle">${attribute.attributeType}</div>
							              <div class="col-xs-2 col-sm-2 col-lg-3">
							                <button id="attribute${attributeIndex.index}" type="button" class="btn btn-primary" 
			                        			onclick="toggleSubscribe('${objectName.objectNameEscaped}', '${attribute.attributeName}', '${attribute.attributeType}', '${attributeIndex.index}');">${buttonText}</button>
							              </div>
							            </div>
							          </c:forEach>
							        </div>
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
            </c:if>
        </div>
    </body>
</html>