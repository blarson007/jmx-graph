<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <link rel="stylesheet" href="../css/bootstrap.min.css">   		
    </head>

    <body>          
        <div class="container">
            <h2>Attributes</h2>
            <table  class="table table-striped">
                <thead>
                    <tr>
                        <td>ID</td>
                        <td>Object Name</td>
                        <td>Attribute</td>
                        <td>Path</td>
                        <td>Enabled</td>
                        <td>Value</td>
                        <td>Timestamp</td>
                    </tr>
                </thead>
                <c:forEach var="jmxAttributePath" items="${jmxList}">
                	<c:forEach var="jmxAttributeValue" items="${jmxAttributePath.attributeValues}">
	                    <tr>
	                        <td>${jmxAttributePath.pathId}</td>                             
	                        <td>${jmxAttributePath.objectName}</td>
	                        <td>${jmxAttributePath.attribute}</td>
	                        <td>${jmxAttributePath.path}</td>
	                        <td>${jmxAttributPath.enabled}</td>
	                        <td>${jmxAttributeValue.attributeValue}</td>
	                        <td>${jmxAttributeValue.timestamp}</td>
	                    </tr>
	            	</c:forEach>
                </c:forEach>               
            </table>
        </div>
    </body>
</html>