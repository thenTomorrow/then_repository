var nursingHomeApp =  angular.module('nursingHomeApp');
 
nursingHomeApp.controller('esami-list', ['$scope', '$http', '$location', '$filter', '$rootScope', '$modal', '$window', 'ngTableParams',
  function ($scope, $http, $location, $filter, $rootScope, $modal, $window, ngTableParams) {
	
	$rootScope.loadingRoot = true;
	
    $http.get('esami/')
	.success(function(data) {
	    $scope.tableParams = createNgTableParams(data, ngTableParams, $filter);
	    $rootScope.loadingRoot = false;
	})
	.error(function(){ });
    
    $scope.dettaglio = function (id) {
    	$location.path('/esami/'+id);
    };
    
    $scope.cancella = function (id) {
    	
    	var modalInstance = $modal.open({
    		templateUrl: 'myModalContent.html',
    		controller: 'ModalInstanceCtrl',
    		resolve: {
    			body: function () {
    				return 'Vuoi veramente cancellare l\'esame?';
  	          	}
  		  	}
        });

    	modalInstance.result.then(function (selectedItem) {
    		$http['delete']('esami/'+id).success(function(data){
    			$window.location.reload();
	     	});
	    }, function () {
	    	
	    });
    };
    
}]);