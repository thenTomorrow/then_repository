var nursingHomeApp =  angular.module('nursingHomeApp');

nursingHomeApp.controller('impostazioni', ['$scope', '$http', '$window',
  function($scope, $http, $window) {
	
	$scope.alertOK = '';
	$scope.alertKO = '';
	$scope.emailScadenze;
	$scope.numeroGiorni;
	
	$http.get('impostazioni/email_scadenze').success(function(data) {
		if(data!=''){
			$scope.emailScadenze = data;
		}
	});
	$http.get('impostazioni/numero_giorni').success(function(data) {
		if(data!=''){
			$scope.numeroGiorni = parseInt(data);
		}
	});
	
    $scope.save = function(){
		
    	$scope.alertOK = '';
    	$scope.alertKO = '';
    	
    	$http.post('impostazioni/', {nome:'email_scadenze', valore:$scope.emailScadenze})
    	.success(function(data){
    		if(data != null){
    			$scope.emailScadenze = data;
    			$scope.alertOK += 'Email scadenza modificata; ';
    		}else{
    			$scope.alertKO += 'Errore: email scadenza non modificata; ';
    		}
    	}).error(function(data, status, headers, config){
    		$scope.alertKO += 'Errore: email scadenza non modificata; ';
    	});
    	
    	$http.post('impostazioni/', {nome:'numero_giorni', valore:$scope.numeroGiorni})
    	.success(function(data){
    		if(data != null){
    			$scope.numeroGiorni = parseInt(data);
    			$scope.alertOK += 'Numero giorni modificati; ';
    		}else{
    			$scope.alertKO += 'Errore: numero giorni non modificati; ';
    		}
    	}).error(function(data, status, headers, config){
    		$scope.alertKO += 'Errore: numero giorni non modificati; ';
    	});
    
    	$window.scrollTo(0,0);
    };
}]);