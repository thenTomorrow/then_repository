var nursingHomeApp =  angular.module('nursingHomeApp');

nursingHomeApp.controller('somministrazioni-detail', ['$scope', '$routeParams', '$http', '$modal', '$location', '$window',
  function($scope, $routeParams, $http, $modal, $location, $window) {
	
	$scope.alertOK = '';
	$scope.isOpenDatiSomministrazione = true;
	$scope.alertKO = '';
	$scope.somministrazione = {quantita:1,data_inserimento:today(),quantita_pacchi:1};
	
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

    	$http.put('somministrazioni/', $scope.somministrazione)
    	.success(function(data){
    		if(data != null){
    			$scope.somministrazione = {quantita:1,data_inserimento:today(),quantita_pacchi:1};
    			$scope.alertOK = data+' somministrazioni inserite';
    		}else{
    			$scope.alertKO = 'Errore: somministrazioni non inserite';
    		}
    	}).error(function(data, status, headers, config){
    		$scope.alertKO = 'Errore: somministrazioni non inserite';
    	});

    	$window.scrollTo(0,0);
    };
}]);