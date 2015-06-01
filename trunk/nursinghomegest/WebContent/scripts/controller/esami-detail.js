var nursingHomeApp =  angular.module('nursingHomeApp');

nursingHomeApp.controller('esami-detail', ['$scope', '$routeParams', '$http', '$modal', '$location', '$window',
  function($scope, $routeParams, $http, $modal, $location, $window) {
	
	$scope.isOpenInfo = true;
	$scope.isFileDaCaricare = true;
	$scope.alertOK = '';
	$scope.alertKO = '';
	$scope.alertFileOK = '';
	$scope.alertFileKO = '';
	$scope.imgPreview;
    $scope.random;
    $scope.esame = {};
    
    $http.get('pazienti/')
	.success(function(data) {
		$scope.pazienti = data;
	})
	.error(function(){ });
	
    if($routeParams.esameId!=null && $routeParams.esameId!=''){
    	
    	$http.get('esami/'+$routeParams.esameId).success(function(data) {
    		if(data!=''){
    			$scope.esameId = $routeParams.esameId;
    			$scope.esame = data;
    			$scope.imgPreview = data;
    			$scope.random = (new Date()).toString();
    		}else{
    			$scope.esameId = "Nuovo";
    		}
    	});
    }
   
    $scope.open = function () {
    	var esameId = $scope.esame.id;
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
    		$http['delete']('esami/'+esameId).success(function(data){
    			$location.path('/esami');
	     	});
	    }, function () {
	    	
	    });
    };
    
    $scope.save = function(){
		if($scope.esame.id!=null){
        	$http.post('esami/'+$scope.esame.id, $scope.esame)
    		.success(function(data){
        		if(data != null){
        			$scope.alertOK = 'Info esame modificate';
        		}else
        			$scope.alertKO = 'Errore: info esame non modificate';
        	}).error(function(data, status, headers, config){
        		$scope.alertKO = 'Errore: info esame non modificate';
        	});	
        }
        else{
        	$http.put('esami/', $scope.esame)
        	.success(function(data){
        		if(data != null){
        			$scope.esame = data;
        			$scope.alertOK = 'Info esame inserite';
        		}else{
        			$scope.alertKO = 'Errore: info esame non inserite';
        		}
        	}).error(function(data, status, headers, config){
        		$scope.alertKO = 'Errore: info esame non inserite';
        	});
        }
    	$window.scrollTo(0,0);
    };
    
    $scope.addImg = function(file){
    	$scope.imgPreview = null;
        var fd = new FormData();
        fd.append('file', file[0]);
    	$http.post('esami/'+$scope.esame.id+'/add', fd,{
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        })
    	.success(function(data){
    		$scope.imgPreview = data;
    		$scope.random = (new Date()).toString();
    		$scope.file = data;
    		$scope.alertFileOK = 'Esame caricato';
    	})
    	.error(function(data, status, headers, config){
    		$scope.alertFileKO = 'Errore nel caricamento dell\'esame';
    	});
    };
    
    $scope.openCarousel = function (file) {
    	$modal.open({
    		templateUrl: 'modalCarousel.html',
    		controller: 'ModalCarouselCtrl',
    		windowClass: 'modal-large',
    		resolve: {
    			file: function () {
    				return file;
    			}
    		}
    	});
    };
    
}]);