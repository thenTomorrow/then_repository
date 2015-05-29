var nursingHomeApp =  angular.module('nursingHomeApp');

nursingHomeApp.controller('pazienti-detail', ['$scope', '$routeParams', '$http', '$modal', '$location', '$window',
  function($scope, $routeParams, $http, $modal, $location, $window) {
	
	$scope.alertOK = '';
	$scope.isOpenDatiAnagrafici = true;
	$scope.alertKO = '';
	$scope.paziente = {};
	
    if($routeParams.pazienteId!=null && $routeParams.pazienteId!=''){
    	$http.get('pazienti/'+$routeParams.pazienteId).success(function(data) {
    		if(data!=''){
    			$scope.pazienteId = $routeParams.pazienteId;
    			$scope.paziente = data;
    		}else{
    			$scope.pazienteId = "Nuovo";
    		}
    	});
    }
   
    $scope.open = function () {
    	var pazienteId = $scope.paziente.id;
    	var modalInstance = $modal.open({
    		templateUrl: 'myModalContent.html',
    		controller: 'ModalInstanceCtrl',
    		resolve: {
    			body: function () {
    				return 'Vuoi veramente cancellare la scheda paziente?';
  	          	}
  		  	}
        });

    	modalInstance.result.then(function (selectedItem) {
    		$http['delete']('pazienti/'+pazienteId).success(function(data){
    			$location.path('/pazienti');
	     	});
	    }, function () {
	    	
	    });
    };
    
    $scope.save = function(){
		if($scope.paziente.id!=null){
        	$http.post('pazienti/'+$scope.paziente.id, $scope.paziente)
    		.success(function(data){
        		if(data != null){
        			$scope.alertOK = 'Scheda paziente modificata';
        		}else
        			$scope.alertKO = 'Errore: scheda paziente non modificata';
        	}).error(function(data, status, headers, config){
        		$scope.alertKO = 'Errore: scheda paziente non modificata';
        	});	
        }
        else{
        	$http.put('pazienti/', $scope.paziente)
        	.success(function(data){
        		if(data != null){
        			$scope.paziente = data;
        			$scope.alertOK = 'Scheda paziente inserita';
        		}else{
        			$scope.alertKO = 'Errore: scheda paziente non inserita';
        		}
        	}).error(function(data, status, headers, config){
        		$scope.alertKO = 'Errore: scheda paziente non inserita';
        	});
        }
    	$window.scrollTo(0,0);
    };
}]);