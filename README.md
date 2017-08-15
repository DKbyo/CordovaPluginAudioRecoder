#Cordova Native Audio Plugin

Cordova extension for Native Audio record for iOS and Android.

## Integration

###startRecord
```
function(time, success, error) {
    exec(success, error, "AudioRecorder", "startRecord", [time]);
};
```
time -> Time in seconds that the recording is made

success -> CallBack function success
'error   -> CallBack function error

###stopRecord
```
function(success, error) {
    exec(success, error, "AudioRecorder", "stopRecord", []);
};
```

success -> CallBack function success
'error   -> CallBack function error

###Based on 

https://github.com/emj365/cordova-plugin-audio-recorder-api

