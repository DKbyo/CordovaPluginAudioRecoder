var exec = require('cordova/exec');

exports.startRecord = function(time, success, error) {
    exec(success, error, "AudioRecorder", "startRecord", [time]);
};

exports.stopRecord = function(success, error) {
    exec(success, error, "AudioRecorder", "stopRecord", []);
};
