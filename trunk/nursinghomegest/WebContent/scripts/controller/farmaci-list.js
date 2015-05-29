var nursingHomeApp =  angular.module('nursingHomeApp');
 
nursingHomeApp.controller('farmaci-list', ['$scope', '$http', '$location', '$filter', '$rootScope', '$modal', '$window', 'ngTableParams',
  function ($scope, $http, $location, $filter, $rootScope, $modal, $window, ngTableParams) {
	
	$rootScope.loadingRoot = true;
	
    $http.get('farmaci/')
	.success(function(data) {
	    $scope.tableParams = createNgTableParams(data, ngTableParams, $filter);
	    $rootScope.loadingRoot = false;
	})
	.error(function(){ });
    
    $scope.dettaglio = function (id) {
    	$location.path('/farmaci/'+id);
    };
    
    $scope.cancella = function (id) {
    	
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
    		$http['delete']('farmaci/'+id).success(function(data){
    			$window.location.reload();
	     	});
	    }, function () {
	    	
	    });
    };
    
}]);