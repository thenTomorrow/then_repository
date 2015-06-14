var nursingHomeApp =  angular.module('nursingHomeApp');

nursingHomeApp.controller('pazienti-detail', ['$scope', '$routeParams', '$http', '$modal', '$location', '$window', '$rootScope', 'ngTableParams', '$filter',  
  function($scope, $routeParams, $http, $modal, $location, $window, $rootScope, ngTableParams, $filter) {
	
	$rootScope.loadingRoot = true;
	$scope.alertOK = '';
	$scope.isOpenDatiAnagrafici = true;
	$scope.isOpenScadenze = true;
	$scope.isOpenSomministrazioni = true;
	$scope.alertKO = '';
	$scope.paziente = {};
	
    if($routeParams.pazienteId!=null && $routeParams.pazienteId!=''){
    	$http.get('pazienti/'+$routeParams.pazienteId).success(function(data) {
    		if(data!=''){
    			$scope.pazienteId = $routeParams.pazienteId;
    			$scope.paziente = data;
    			
    			$http.get('impostazioni/numero_giorni').success(function(data1) {
    				if(data1!=''){
    					$scope.numeroGiorni = parseInt(data1);
    					
    					$http.get('reports/scadenze/'+$routeParams.pazienteId)
    					.success(function(data2) {
    						$scope.tableParams = createNgTableParams(data2, ngTableParams, $filter);
    					})
    					.error(function(){ });
    				}
    			});
    			
    		    $http.get('somministrazioni/bypaziente/'+$routeParams.pazienteId)
    			.success(function(data) {
    			    $scope.tableParams1 = createNgTableParams(data, ngTableParams, $filter);
    			})
    			.error(function(){ });
    			
    		}else
    			$scope.pazienteId = "Nuovo";
    		
    		$rootScope.loadingRoot = false;
    	});
    }
    else
    	$rootScope.loadingRoot = false;
   
    $scope.open = function () {
    	var pazienteId = $scope.paziente.id;
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
    		$http['delete']('pazienti/'+pazienteId+'/disabilita').success(function(data){
    			$location.path('/pazienti');
	     	});
	    }, function () {
	    	
	    });
    };
    
    $scope.riabilita = function () {
    	var pazienteId = $scope.paziente.id;
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
    		$http['delete']('pazienti/'+pazienteId+'/riabilita').success(function(data){
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