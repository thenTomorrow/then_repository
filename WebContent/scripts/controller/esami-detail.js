var nursingHomeApp =  angular.module('nursingHomeApp');

nursingHomeApp.controller('esami-detail', ['$scope', '$routeParams', '$http', '$modal', '$location', '$window', '$rootScope',
  function($scope, $routeParams, $http, $modal, $location, $window, $rootScope) {
	
	$rootScope.loadingRoot = true;
	$scope.isOpenInfo = true;
	$scope.isFileDaCaricare = true;
	$scope.isDocumentoDaCaricare = true;
	$scope.alertOK = '';
	$scope.alertKO = '';
	$scope.alertFileOK = '';
	$scope.alertFileKO = '';
	$scope.alertDocumentoOK = '';
	$scope.alertDocumentoKO = '';
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
    		}else
    			$scope.esameId = "Nuovo";
    		
    		$rootScope.loadingRoot = false;
    	});
    }
    else
    	$rootScope.loadingRoot = false;
   
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
    
    $scope.addDocumento = function(file){
    	var fd = new FormData();
    	fd.append('file', file[0]);
    	$http.post('esami/'+$scope.esame.id+'/addFile', fd,{
    		transformRequest: angular.identity,
    		headers: {'Content-Type': undefined}
    	})
    	.success(function(data){
    		$scope.esame = data;
    		$scope.alertDocumentoOK = 'Documento caricato';
    	})
    	.error(function(data, status, headers, config){
    		$scope.alertDocumentoKO = 'Errore nel caricamento del documento';
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