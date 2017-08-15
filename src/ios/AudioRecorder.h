#import <Cordova/CDV.h>
#import <AVFoundation/AVFoundation.h>

@interface AudioRecorder : CDVPlugin <AVAudioRecorderDelegate> {
  NSString *recorderFilePath;
  NSNumber *duration;
  AVAudioRecorder *recorder;
  AVAudioPlayer *player;
  CDVPluginResult *pluginResult;
  CDVInvokedUrlCommand *_command;
  NSString *scanCallbackId;

}

- (void)startRecord:(CDVInvokedUrlCommand*)command;
- (void)stopRecord:(CDVInvokedUrlCommand*)command;
- (void)playback:(CDVInvokedUrlCommand*)command;

@end