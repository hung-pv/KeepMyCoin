
var ethFuncs = window.ethFuncs;
var ethUtil = window.ethUtil;
var etherUnits = window.etherUnits;
var globalFuncs = window.globalFuncs;
var BigNumber = window.BigNumber;
var ajaxReq = {};

var $scope = {}
$scope.tokens = window.Token.popTokens;
$scope.wallet = {}
$scope.wallet.address = '';
$scope.wallet.privKey = '';
$scope.wallet.data = {}
$scope.wallet.getAddressString = function() {
	return $scope.wallet.address;
}
$scope.wallet.getPrivateKeyString = function() {
	return $scope.wallet.privKey;
}
$scope.wallet.getPath = function() {
	return $scope.wallet.data.path;
}
$scope.wallet.getHWType = function() {
	return $scope.wallet.data.hwType;
}
$scope.wallet.getHWTransport = function() {
	return $scope.wallet.data.hwTransport;
}

$scope.renew = function() {
    $scope.gasPriceDec = 0;
    $scope.nonceDec = 0;
	$scope.tx = {
		gasLimit: globalFuncs.defaultTxGasLimit,
		from: "",
		data: "",
		to: "",
		unit: "ether",
		value: '',
		nonce: null,
		gasPrice: null,
		donate: false
	};
	$scope.tokenTx = {
		to: '',
		value: 0,
		id: 'ether',
		gasLimit: 150000
	};
	$scope.wallet.address = '';
	$scope.wallet.privKey = '';
	$scope.wallet.data = {}
};

$scope.renew();

$scope.tokenObjs = [];
for (var i = 0; i < $scope.tokens.length; i++) {
	$scope.tokenObjs.push(new Token($scope.tokens[i].address, '', $scope.tokens[i].symbol, $scope.tokens[i].decimal, $scope.tokens[i].type));
}

var countTokens = $scope.tokenObjs.length;
var gasLimit = $scope.tx.gasLimit;

var mew = {}
mew.signEtherTx = function(jsonEtherTxInfo) {
	etherTxInfo = JSON.parse(jsonEtherTxInfo);
	if (etherTxInfo === undefined || etherTxInfo == null) return;
	let guid = etherTxInfo.guid;
	if (guid === undefined || guid == null || guid.length < 1) return;
	//guid, from, to, value, nonce, gasPrice, gasLimit, privKey
	$scope.renew();
	$scope.tx.gasLimit = etherTxInfo.gasLimit;
	$scope.tx.from = etherTxInfo.from;
	$scope.tx.to = etherTxInfo.to;
	$scope.tx.value = etherTxInfo.value;
	$scope.tx.nonce = etherTxInfo.nonce;
	$scope.tx.gasPrice = etherTxInfo.gwei;
	
	//
	$scope.wallet.address = $scope.tx.from;
	if (etherTxInfo.privKey)
		$scope.wallet.privKey = etherTxInfo.privKey;

	var txData = window.uiFuncs.getTxData($scope);
	txData.isOffline = true;
    txData.nonce = etherTxInfo.nonceHex;
    txData.gasPrice = etherTxInfo.gasPriceHex;
    /*- For contract only
    if ($scope.tokenTx.id != 'ether') {
        txData.data = $scope.tokenObjs[$scope.tokenTx.id].getData($scope.tx.to, $scope.tx.value).data;
        txData.to = $scope.tokenObjs[$scope.tokenTx.id].getContractAddress();
        txData.value = '0x00';
    }
    */
    //resultStorage[guid] = JSON.stringify(txData);
	window.uiFuncs.generateTx(txData, function (rawTx) {
        resultStorage[guid] = JSON.stringify(rawTx);
    });
}

var resultStorage = {}
resultStorage.has = function(guid) {
	return resultStorage[guid] !== undefined;
}