var exec = require('cordova/exec');

exports.deviceReady = function(success, error) {
    exec(success, error, "PassportReaderPlugin", "deviceReady", []);
};

exports.enableReader = function(documentNumber, dateOfBirth, dateOfExpiry, success, error) {
    exec(success, error, "PassportReaderPlugin", "enableReader", [documentNumber, dateOfBirth, dateOfExpiry]);
};

exports.disableReader = function(success, error) {
    exec(success, error, "PassportReaderPlugin", "disableReader", []);
};

exports.checkNfcSupport = function(success, error) {
    exec(success, error, "PassportReaderPlugin", "checkNfcSupport", []);
};

exports.isNfcEnabled = function(success, error) {
    exec(success, error, "PassportReaderPlugin", "isNfcEnabled", []);
};
