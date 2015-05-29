<!doctype html>
<html ng-app="nursingHomeApp">
	<head>
		<title>Residenza per anziani Maria Madre della Fiducia</title>
		<meta charset="utf-8">
    	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
		
		<script src="js/jquery-2.1.4.min.js" ></script>
		<script src="js/angular.min.js" ></script>
		<script src="js/angular-route.min.js" ></script>
		<script src="js/angular-locale_it-it.js" ></script>
		<script src="js/ui-bootstrap-tpls-0.12.0.min.js" ></script>
		<script src="js/underscore-min.js" ></script>
		<script src="js/ng-table.min.js" ></script>
		<script src="js/angular-sanitize.min.js" ></script>
		<script src="js/nursinghome.js" ></script>
		
		<script src="scripts/app.js" ></script>
		<script src="scripts/controller/pazienti-list.js" ></script>
		<script src="scripts/controller/pazienti-detail.js" ></script>
		<script src="scripts/controller/farmaci-list.js" ></script>
		<script src="scripts/controller/farmaci-detail.js" ></script>
		
		<link href="css/bootstrap.min.css" rel="stylesheet">
		<link href="css/bootstrap-theme.min.css" rel="stylesheet">
		<link href="css/ng-table.min.css" rel="stylesheet">
		<link href="http://netdna.bootstrapcdn.com/font-awesome/4.0.0/css/font-awesome.min.css" rel="stylesheet prefetch" >
		<link href="css/nursinghome.css" rel="stylesheet">
	</head>
	<body ng-init="loadingRoot=false">
		<a id="top-scroll" name="top"></a>
		<div class="navbar navbar-default" role="navigation">
      		<div class="container" style="width:100%">
	        	<div class="navbar-header">
	          		<button type="button" class="navbar-toggle" ng-init="navCollapsed=true" ng-click="navCollapsed=!navCollapsed">
	            		<span class="sr-only">Toggle navigation</span>
	            		<span class="icon-bar"></span>
	            		<span class="icon-bar"></span>
	            		<span class="icon-bar"></span>
	          		</button>
	          		<a class="navbar-brand" href="#"><span class="navbar-item">Admin</span></a>
	          	</div>
	        	<div class="collapse navbar-collapse" ng-class="!navCollapsed && 'in'">
	        		<ul class="nav navbar-nav" ng-click="navCollapsed=true" ng-controller="HeaderController">
		            	<li ng-class="{active:($location.path().indexOf('/pazienti')!=-1)}"><a href="#pazienti"><i class="glyphicon glyphicon-user"></i> <span class="menu-item">Pazienti</span></a></li>
		            	<li ng-class="{active:($location.path().indexOf('/farmaci')!=-1)}"><a href="#farmaci"><i class="glyphicon glyphicon-inbox"></i> <span class="menu-item">Farmaci</span></a></li>
		            	<li ng-class="{active:($location.path().indexOf('/somministrazioni')!=-1)}"><a href="#somministrazioni"><i class="glyphicon glyphicon-check"></i> <span class="menu-item">Somministrazioni</span></a></li>
		            	<li ng-class="{active:($location.path().indexOf('/impostazioni')!=-1)}"><a href="#impostazioni"><i class="glyphicon glyphicon-cog"></i> <span class="menu-item">Impostazioni</span></a></li>
		            	<li><a href="logout"><i class="glyphicon glyphicon-off"></i> <span class="menu-item">Esci</span></a></li>
	          		</ul>
			        <form class="navbar-form navbar-left" role="search" ng-submit="navCollapsed=true;search(query);query=''">
				    	<div class="input-group">
				  			<input type="text" class="form-control" placeholder="Cerca" ng-model="query">
				  			<div class="input-group-btn">
				  				<button type="submit" class="btn btn-primary"><span class="glyphicon glyphicon-search"></span></button>
				  			</div>
				    	</div>
				    </form>
		        </div>
      		</div>
    	</div>
    	<script>		
		function HeaderController($scope, $location) {
			$scope.$location = $location;
		}
		</script>
	    <center>
			<div class="progress" ng-show="loadingRoot" style="width: 50%">
				<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
			</div>
		</center>
		<div style="padding: 2px;" ng-view ng-show="!loadingRoot"></div>
		<div class="footer">
	    	<div class="container">
	        	<p class="text-muted">Residenza per anziani Maria Madre della Fiducia</p>
	        	<p class="text-muted">Contrada Scaro 1 - 97016 Pozzallo (RG)</p>
	        	<p class="text-muted">Via Unita' D'Italia 932 - 97016 Pozzallo (RG)</p>
	        	<p class="text-muted">© Tutti i diritti riservati</p>
	      	</div>
	    </div>
	</body>
</html>