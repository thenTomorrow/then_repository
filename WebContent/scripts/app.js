var nursingHomeApp = angular.module('nursingHomeApp', [                                   
	'ui.bootstrap',                                                 	
	'ngRoute',
	'ngTable',
	"ngSanitize"
]);
 
nursingHomeApp.config(['$routeProvider', function($routeProvider) {
	
	$routeProvider.
	when('/pazienti', {
		templateUrl: 'partial/pazienti-list.html',
		controller: 'pazienti-list'
	}).
	when('/pazienti/new/', {
		templateUrl: 'partial/pazienti-detail.html',
		controller: 'pazienti-detail'
	}).
	when('/pazienti/:pazienteId', {
		templateUrl: 'partial/pazienti-detail.html',
		controller: 'pazienti-detail'
	}).
	otherwise({
		redirectTo: '/pazienti'
	});
	
}]);

var ModalInstanceCtrl = function ($scope, $modalInstance, body) {
	  
	$scope.body = body;
	$scope.ok = function () {
		$modalInstance.close('ok');
	};

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
};

var ModalCarouselCtrl = function ($scope, $modalInstance, file) {
	
	$scope.file = file;
	$scope.ok = function () {
		$modalInstance.close('ok');
	};
};
	
var AccordionCtrl = function($scope) {};

var createNgTableParams = function(data, ngTableParams, $filter) {
	return new ngTableParams(
			{page: 1, count: 100}, 
			{total: data.length,
			 getData: function($defer, params){
				   		 	var orderedData = params.sorting() ?
			                                  $filter('orderBy')(data, params.orderBy()) :
			                                  data;
			
			                $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
			          }
            });
};