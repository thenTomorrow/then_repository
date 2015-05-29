<!doctype html>
<html ng-app>
	<head>
		<title>Azienda Agricola Vincenzo Gravagna</title>
  		<meta charset="utf-8">
    	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
		 
		<script src="js/jquery-2.1.4.min.js" ></script>
		<script src="js/angular.min.js" ></script>
		<script src="js/angular-route.min.js" ></script>
		<script src="js/ui-bootstrap-tpls-0.12.0.min.js" ></script>
		
		<link href="css/bootstrap.min.css" rel="stylesheet">
		<link href="css/bootstrap-theme.min.css" rel="stylesheet">
		<link href="css/nursinghome.css" rel="stylesheet">
	</head>
	<body>
		<div class="container" ng-controller="Login">
	      	<form class="form-signin" ng-submit="login()" autocomplete="on">
				<div class="logo">	
					
				</div>
				<br/>
	        	<div class="alert alert-danger alert-dismissable" ng-show="alert!=''" >
		  	 		<button type="button" class="close" aria-hidden="true" ng-click="alert=''">&times;</button>
		  			<strong>Attenzione!</strong> {{alert.msg}}.
		  		</div>
	        	<input type="text" id="username" ng-model="user.username" class="form-control" placeholder="Username" required autofocus>
	        	<input type="password" id="password" ng-model="user.password" class="form-control" placeholder="Password">
	        	<button class="btn btn-lg btn-success btn-block" type="submit">Accedi</button>
	      	</form>
   		</div>
		
		<script>
		function Login($scope, $http, $location, $rootScope, $window){
			
			$scope.user = {username: '', password: ''};	
			$scope.alert = '';
			$scope.login = function(){
				$http.post('login', $scope.user)
				.success(function(data){
					if(data!=null && data == 'true'){
						$window.location.href = "${pageContext.request.contextPath}/#/pazienti";
					}else{
						$scope.alert = { type: 'danger', msg: 'username e/o password errate' };
					}
		   		}).error(function(){});
			};
		}
		</script>
		
	</body>
</html>

