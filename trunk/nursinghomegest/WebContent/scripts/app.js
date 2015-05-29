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
	when('/farmaci', {
		templateUrl: 'partial/farmaci-list.html',
		controller: 'farmaci-list'
	}).
	when('/farmaci/new/', {
		templateUrl: 'partial/farmaci-detail.html',
		controller: 'farmaci-detail'
	}).
	when('/farmaci/:farmacoId', {
		templateUrl: 'partial/farmaci-detail.html',
		controller: 'farmaci-detail'
	}).
	when('/somministrazioni', {
		templateUrl: 'partial/somministrazioni-list.html',
		controller: 'somministrazioni-list'
	}).
	when('/somministrazioni/new/', {
		templateUrl: 'partial/somministrazioni-detail.html',
		controller: 'somministrazioni-detail'
	}).
	when('/somministrazioni/:somministrazioneId', {
		templateUrl: 'partial/somministrazioni-detail.html',
		controller: 'somministrazioni-detail'
	}).
	when('/reports', {
		templateUrl: 'partial/reports.html',
		controller: 'reports'
	}).
	when('/impostazioni', {
		templateUrl: 'partial/impostazioni.html',
		controller: 'impostazioni'
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