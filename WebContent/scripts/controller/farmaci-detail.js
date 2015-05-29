var nursingHomeApp =  angular.module('nursingHomeApp');

nursingHomeApp.controller('farmaci-detail', ['$scope', '$routeParams', '$http', '$modal', '$location', '$window',
  function($scope, $routeParams, $http, $modal, $location, $window) {
	
	$scope.alertOK = '';
	$scope.isOpenDatiFarmaco = true;
	$scope.alertKO = '';
	$scope.farmaco = {quantita_per_pezzo:1};
	
    if($routeParams.farmacoId!=null && $routeParams.farmacoId!=''){
    	$http.get('farmaci/'+$routeParams.farmacoId).success(function(data) {
    		if(data!=''){
    			$scope.farmacoId = $routeParams.farmacoId;
    			$scope.farmaco = data;
    		}else{
    			$scope.farmacoId = "Nuovo";
    		}
    	});
    }
   
    $scope.open = function () {
    	var farmacoId = $scope.farmaco.id;
    	var modalInstance = $modal.open({
    		templateUrl: 'myModalContent.html',
    		controller: 'ModalInstanceCtrl',
    		resolve: {
    			body: function () {
    				return 'Vuoi veramente cancellare il farmaco?';
  	          	}
  		  	}
        });

    	modalInstance.result.then(function (selectedItem) {
    		$http['delete']('farmaci/'+farmacoId).success(function(data){
    			$location.path('/farmaci');
	     	});
	    }, function () {
	    	
	    });
    };
    
    $scope.save = function(){
		if($scope.farmaco.id!=null){
        	$http.post('farmaci/'+$scope.farmaco.id, $scope.farmaco)
    		.success(function(data){
        		if(data != null){
        			$scope.alertOK = 'Farmaco modificato';
        		}else
        			$scope.alertKO = 'Errore: farmaco non modificato';
        	}).error(function(data, status, headers, config){
        		$scope.alertKO = 'Errore: farmaco non modificato';
        	});	
        }
        else{
        	$http.put('farmaci/', $scope.farmaco)
        	.success(function(data){
        		if(data != null){
        			$scope.farmaco = data;
        			$scope.alertOK = 'Farmaco inserito';
        		}else{
        			$scope.alertKO = 'Errore: farmaco non inserito';
        		}
        	}).error(function(data, status, headers, config){
        		$scope.alertKO = 'Errore: farmaco non inserito';
        	});
        }
    	$window.scrollTo(0,0);
    };
}]);