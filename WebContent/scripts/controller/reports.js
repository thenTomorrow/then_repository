var nursingHomeApp =  angular.module('nursingHomeApp');
 
nursingHomeApp.controller('reports', ['$scope', '$http', '$filter', '$rootScope', 'ngTableParams',
  function ($scope, $http, $filter, $rootScope, ngTableParams) {
	
	$rootScope.loadingRoot = true;
	
	$http.get('impostazioni/numero_giorni').success(function(data) {
		if(data!=''){
			$scope.numeroGiorni = parseInt(data);
			
			$http.get('reports/scadenze/')
			.success(function(data) {
			    $scope.tableParams = createNgTableParams(data, ngTableParams, $filter);
			    $rootScope.loadingRoot = false;
			})
			.error(function(){ });
		}
	});
    
}]);