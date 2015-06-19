var nursingHomeApp =  angular.module('nursingHomeApp');
 
nursingHomeApp.controller('pazienti-list', ['$scope', '$http', '$location', '$filter', '$rootScope', '$modal', '$window', 'ngTableParams',
  function ($scope, $http, $location, $filter, $rootScope, $modal, $window, ngTableParams) {
	
	$rootScope.loadingRoot = true;
	
    $http.get('pazientiAll/')
	.success(function(data) {
	    $scope.tableParams = createNgTableParams(data, ngTableParams, $filter, {nome: 'asc'});
	    $rootScope.loadingRoot = false;
	})
	.error(function(){ });
    
    $scope.dettaglio = function (id) {
    	$location.path('/pazienti/'+id);
    };
    
    $scope.disabilita = function (id) {
    	
    	var modalInstance = $modal.open({
    		templateUrl: 'myModalContent.html',
    		controller: 'ModalInstanceCtrl',
    		resolve: {
    			body: function () {
    				return 'Vuoi veramente disabilitare la scheda paziente?';
  	          	}
  		  	}
        });

    	modalInstance.result.then(function (selectedItem) {
    		$http['delete']('pazienti/'+id+'/disabilita').success(function(data){
    			$window.location.reload();
	     	});
	    }, function () {
	    	
	    });
    };
    
    $scope.riabilita = function (id) {
    	
    	var modalInstance = $modal.open({
    		templateUrl: 'myModalContent.html',
    		controller: 'ModalInstanceCtrl',
    		resolve: {
    			body: function () {
    				return 'Vuoi veramente riabilitare la scheda paziente?';
    			}
    		}
    	});
    	
    	modalInstance.result.then(function (selectedItem) {
    		$http['delete']('pazienti/'+id+'/riabilita').success(function(data){
    			$window.location.reload();
    		});
    	}, function () {
    		
    	});
    };
    
    $scope.selectedStato = 'PRESENTE';
    $scope.cercaStato=function(newStato){
    	if(newStato!='')
    		$scope.selectedStato=newStato;
    	else
    		$scope.selectedStato=null;
    };
}]);