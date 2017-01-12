<!DOCTYPE html>
<html lang="en">
	<head>
		<title>JMX-Graph</title>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<link rel="stylesheet" href="../css/bootstrap.min.css">
        <script src="../js/jquery-3.1.1.min.js"></script>
        <script src="../js/bootstrap.min.js"></script>
	</head>
	<body>
		<nav class="navbar navbar-inverse">
			<div class="container-fluid">
				<div class="navbar-header">
					<a class="navbar-brand" href="#">JMX-Graph</a>
				</div>
				<ul class="nav navbar-nav">
					<li>${page}</li>
					<li class="${param.page eq 'Graphs' ? 'active' : ''}"><a href="/jmx-graph.html">Graphs</a></li>
					<li class="${param.page eq 'MBeans' ? 'active' : ''}"><a href="/object-name-selection.html">MBeans</a></li>
					<li class="${param.page eq 'Configuration' ? 'active' : ''}"><a href="/configuration.html">Configuration</a></li>
				</ul>
			</div>
		</nav>
	</body>
</html>