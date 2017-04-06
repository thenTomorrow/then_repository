var nursingHomeApp =  angular.module('nursingHomeApp');
 
nursingHomeApp.controller('reports', ['$scope', '$http', '$filter', '$rootScope', 'ngTableParams',
  function ($scope, $http, $filter, $rootScope, ngTableParams) {
	
	$scope.tabs = [{
        title: 'Scadenze',
        icon: 'glyphicon glyphicon-th-list',
        url: 'scadenze.tpl.html'
    }, {
        title: "Top 10",
        icon: 'glyphicon glyphicon-stats',
        url: 'farmaciusati.tpl.html'
    }, {
        title: 'Statistiche',
        icon: 'glyphicon glyphicon-stats',
        url: 'statistiche.tpl.html'
    }];
	
	$scope.currentTab = 'scadenze.tpl.html';
	
	$scope.onClickTab = function (tab) {
	    $scope.currentTab = tab.url;
	}
	
	$scope.isActiveTab = function(tabUrl) {
	    return tabUrl == $scope.currentTab;
	}
	
	$rootScope.loadingRoot = true;
	
	$http.get('impostazioni/numero_giorni').success(function(data) {
		if(data!=''){
			$scope.numeroGiorni = parseInt(data);
			
			$http.get('reports/scadenze/')
			.success(function(data) {
			    $scope.tableParams = createNgTableParams(data, ngTableParams, $filter, {giorni_rimanenti: 'asc'});
			    $rootScope.loadingRoot = false;
			})
			.error(function(){ });
		}
	});
	
	$scope.labels = [];
	$scope.data = [];	
	$http.get('reports/statistiche/farmaciusati')
	.success(function(data) {
		data.forEach(function(element) {
			$scope.labels.push(element.farmaco);
			$scope.data.push(element.num_caricati);
		});
	    $scope.tableParams2 = createNgTableParams(data, ngTableParams, $filter, {num_caricati: 'desc'});
	})
	.error(function(){ });
	
	$scope.labels1 = [];
	$scope.data1 = [];	
	$http.get('reports/statistiche/pazientifarmaciusati')
	.success(function(data) {
		data.forEach(function(element) {
			$scope.labels1.push(element.cognome);
			$scope.data1.push(element.num_usati);			
		});
		$scope.tableParams3 = createNgTableParams(data, ngTableParams, $filter, {num_usati: 'desc'});
	})
	.error(function(){ });
	
	$http.get('reports/statistiche/')
	.success(function(data) {
		$scope.tableParams1 = createNgTableParams(data, ngTableParams, $filter, {eta: 'desc'});
	})
	.error(function(){ });
	
	$http.get('reports/statistiche/etamedia')
	.success(function(data) {
		$scope.eta = data;
	})
	.error(function(){ });
    
}]);