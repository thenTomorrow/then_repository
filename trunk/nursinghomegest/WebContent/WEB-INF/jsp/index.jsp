<!doctype html>
<html ng-app="nursingHomeApp" ng-controller="HeaderController">
	<head>
		<title>{{intestazione}}</title>
		<meta charset="utf-8">
    	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
		<link rel="icon" type="image/png" href="img/{{nome}}.png" />
		<meta http-equiv="cache-control" content="no-cache" />
		<meta http-equiv="expires" content="0" />
		<meta http-equiv="pragma" content="no-cache" />
		
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
		<script src="scripts/controller/somministrazioni-list.js" ></script>
		<script src="scripts/controller/somministrazioni-detail.js" ></script>
		<script src="scripts/controller/esami-list.js" ></script>
		<script src="scripts/controller/esami-detail.js" ></script>
		<script src="scripts/controller/reports.js" ></script>
		<script src="scripts/controller/impostazioni.js" ></script>
		
		<link href="css/bootstrap.min.css" rel="stylesheet">
		<link href="css/bootstrap-theme.min.css" rel="stylesheet">
		<link href="css/ng-table.min.css" rel="stylesheet">
		<link href="http://netdna.bootstrapcdn.com/font-awesome/4.0.0/css/font-awesome.min.css" rel="stylesheet prefetch" >
		<link href="css/nursinghome.css" rel="stylesheet">
	</head>
	<body ng-init="$parent.loadingRoot=false" ng-cloak>
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
	        		<ul class="nav navbar-nav" ng-click="navCollapsed=true">
		            	<li ng-class="{active:($location.path().indexOf('/pazienti')!=-1)}"><a href="#pazienti"><i class="glyphicon glyphicon-user"></i> <span class="menu-item">Pazienti</span></a></li>
		            	<li ng-class="{active:($location.path().indexOf('/farmaci')!=-1)}"><a href="#farmaci"><i class="glyphicon glyphicon-inbox"></i> <span class="menu-item">Farmaci</span></a></li>
		            	<li ng-class="{active:($location.path().indexOf('/somministrazioni')!=-1)}"><a href="#somministrazioni"><i class="glyphicon glyphicon-hand-right"></i> <span class="menu-item">Somministrazioni</span></a></li>
		            	<li ng-class="{active:($location.path().indexOf('/esami')!=-1)}"><a href="#esami"><i class="glyphicon glyphicon-check"></i> <span class="menu-item">Esami</span></a></li>
		            	<li ng-class="{active:($location.path().indexOf('/reports')!=-1)}"><a href="#reports"><i class="glyphicon glyphicon-th-list"></i> <span class="menu-item">Reports</span></a></li>
		            	<li ng-class="{active:($location.path().indexOf('/impostazioni')!=-1)}"><a href="#impostazioni"><i class="glyphicon glyphicon-cog"></i> <span class="menu-item">Impostazioni</span></a></li>
	          		</ul>
			        <form class="navbar-form navbar-right" role="search" style="float: left;" ng-submit="navCollapsed=true;search(query);query=''">
				    	<div class="input-group" style="width:78%;float:left;margin-right:5px;">
				  			<input type="text" class="form-control" placeholder="Cerca" ng-model="query">
				  			<div class="input-group-btn">
				  				<button type="submit" class="btn btn-primary"><span class="glyphicon glyphicon-search"></span></button>
				  			</div>
				    	</div>
				    	<button type="button" ng-click="openLogout()" class="btn btn-danger" style="float:left;"><i class="glyphicon glyphicon-off"></i></button>
				    </form>
		        </div>
      		</div>
    	</div>
    	<script>
    	function HeaderController($scope, $location, $http, $modal) {
			$scope.$location = $location;
			$scope.intestazione = '';
			$scope.indirizzo = '';
			$scope.nome = '';
			$http.get('cliente')
			.success(function(data) {
			    $scope.intestazione = data.denominazione;
			    $scope.indirizzo = data.indirizzo;
			    $scope.nome = data.nome;
			})
			.error(function(){ });
			
			$scope.openLogout = function () {
		    	var modalInstance = $modal.open({
		    		templateUrl: 'myModalLogout.html',
		    		controller: 'ModalInstanceCtrl',
		    		resolve: {
		    			body: function () {
		    				return 'Sicuro di voler uscire dall\'applicativo?';
		  	          	}
		  		  	}
		        });
		    };
		}
		</script>
		
	    <center>
			<div class="progress" ng-show="$parent.loadingRoot" style="width: 50%">
				<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
			</div>
		</center>
		<div style="padding: 2px;" ng-view ng-show="!$parent.loadingRoot"></div>
		
		<script type="text/ng-template" id="myModalLogout.html">
        	<div class="modal-header">
	        	<button type="button" class="close" aria-label="Close" ng-click="cancel()"><span aria-hidden="true">&times;</span></button>
	        	<h4 class="modal-title"><i class="glyphicon glyphicon-off"></i> Logout</h4>
	      	</div>
	      	<div class="modal-body">
	      		<h4>{{body}}</h4>
			</div>
	      	<div class="modal-footer">
	      		<a href="logout" class="btn btn-danger"><i class="glyphicon glyphicon-off"></i> Si</a>
		        <button type="button" class="btn btn-default" ng-click="cancel()">No</button>
			</div>
    	</script>
		
		<div class="footer">
	    	<div class="container" style="text-align: center;">
	        	<p class="text-muted">{{intestazione}}</p>
	        	<p class="text-muted">{{indirizzo}}</p>
	        	<p class="text-muted">© Tutti i diritti riservati</p>
	      	</div>
	    </div>
	</body>
</html>