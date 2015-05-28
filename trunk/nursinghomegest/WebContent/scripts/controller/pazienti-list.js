var nursingHomeApp =  angular.module('nursingHomeApp');
 
nursingHomeApp.controller('pazienti-list', ['$scope', '$http', '$location', '$filter', '$rootScope', '$modal', '$window', 'ngTableParams',
  function ($scope, $http, $location, $filter, $rootScope, $modal, $window, ngTableParams) {
	
	$rootScope.loadingRoot = true;
	
    $http.get('pazienti/')
	.success(function(data) {
	    $scope.tableParams = createNgTableParams(data, ngTableParams, $filter);
	    $rootScope.loadingRoot = false;
	})
	.error(function(){ });
    
    $scope.dettaglio = function (id) {
    	$location.path('/pazienti/'+id);
    };
    
    $scope.cancella = function (id) {
    	
    	var modalInstance = $modal.open({
    		templateUrl: 'myModalContent.html',
    		controller: 'ModalInstanceCtrl',
    		resolve: {
    			body: function () {
    				return 'Vuoi veramente cancellare il paziente?';
  	          	}
  		  	}
        });

    	modalInstance.result.then(function (selectedItem) {
    		$http['delete']('pazienti/'+id).success(function(data){
    			$window.location.reload();
	     	});
	    }, function () {
	    	
	    });
    };
    
}]);