var nursingHomeApp =  angular.module('nursingHomeApp');

nursingHomeApp.controller('somministrazioni-detail', ['$scope', '$routeParams', '$http', '$modal', '$location', '$window',
  function($scope, $routeParams, $http, $modal, $location, $window) {
	
	$scope.alertOK = '';
	$scope.isOpenDatiSomministrazione = true;
	$scope.alertKO = '';
	$scope.somministrazione = {quantita:1,data_inserimento:today()};
	
	$http.get('pazienti/')
	.success(function(data) {
		$scope.pazienti = data;
	})
	.error(function(){ });
	
	$http.get('farmaci/')
	.success(function(data) {
		$scope.farmaci = data;
	})
	.error(function(){ });	
	
    if($routeParams.somministrazioneId!=null && $routeParams.somministrazioneId!=''){
    	$http.get('somministrazioni/'+$routeParams.somministrazioneId).success(function(data) {
    		if(data!=''){
    			$scope.somministrazioneId = $routeParams.somministrazioneId;
    			$scope.somministrazione = data;
    		}else{
    			$scope.somministrazioneId = "Nuovo";
    		}
    	});
    }
   
    $scope.open = function () {
    	var somministrazioneId = $scope.somministrazione.id;
    	var modalInstance = $modal.open({
    		templateUrl: 'myModalContent.html',
    		controller: 'ModalInstanceCtrl',
    		resolve: {
    			body: function () {
    				return 'Vuoi veramente cancellare la somministrazione?';
  	          	}
  		  	}
        });

    	modalInstance.result.then(function (selectedItem) {
    		$http['delete']('somministrazioni/'+somministrazioneId).success(function(data){
    			$location.path('/somministrazioni');
	     	});
	    }, function () {
	    	
	    });
    };
    
    $scope.save = function(){
		if($scope.somministrazione.id!=null){
        	$http.post('somministrazioni/'+$scope.somministrazione.id, $scope.somministrazione)
    		.success(function(data){
        		if(data != null){
        			$scope.alertOK = 'Somministrazione modificata';
        		}else
        			$scope.alertKO = 'Errore: somministrazione non modificata';
        	}).error(function(data, status, headers, config){
        		$scope.alertKO = 'Errore: somministrazione non modificata';
        	});	
        }
        else{
        	$http.put('somministrazioni/', $scope.somministrazione)
        	.success(function(data){
        		if(data != null){
        			$scope.somministrazione = data;
        			$scope.alertOK = 'Somministrazione inserita';
        		}else{
        			$scope.alertKO = 'Errore: somministrazione non inserita';
        		}
        	}).error(function(data, status, headers, config){
        		$scope.alertKO = 'Errore: somministrazione non inserita';
        	});
        }
    	$window.scrollTo(0,0);
    };
}]);