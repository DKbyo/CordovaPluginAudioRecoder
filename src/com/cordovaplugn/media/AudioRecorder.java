package com.cordovaplugn.media;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

/**
 * This class echoes a string called from JavaScript.
 */
public class AudioRecorder extends CordovaPlugin {

	private static final String LOG_TAG = "AudioRecordPlugin";

	Activity context;
	private CallbackContext callback;

	//Permission
	private String [] permissions = {Manifest.permission.RECORD_AUDIO};
	private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
	private boolean permissionToRecordAccepted = false;
	private static String PERMISSION_DENIED_ERROR = "PERMISSION DENIED";

	//File
	private static String mFileName = null;

	//Recorder
	private MediaRecorder mRecorder = null;

	@Override
	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
		// TODO Auto-generated method stub
		//super.onRequestPermissionResult(requestCode, permissions, grantResults);

		/*for(int r:grantResults)
		{
			if(r == PackageManager.PERMISSION_DENIED)
			{
				callback.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PERMISSION_DENIED_ERROR));
				Toast.makeText(context, PERMISSION_DENIED_ERROR, Toast.LENGTH_LONG).show();
				return;
			}
		}*/

		switch (requestCode){
		case REQUEST_RECORD_AUDIO_PERMISSION:
			permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
			break;
		}

		if (!permissionToRecordAccepted ){
			Toast.makeText(context, PERMISSION_DENIED_ERROR, Toast.LENGTH_LONG).show();
			callback.error(PERMISSION_DENIED_ERROR);
			return;
		}
	}


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        callback = callbackContext;
		context = cordova.getActivity();

		cordova.requestPermissions(this, REQUEST_RECORD_AUDIO_PERMISSION, permissions);

		mFileName = context.getFilesDir().getAbsolutePath(); // getExternalCacheDir().getAbsolutePath();
		mFileName += "/audiorecordtest.m4a";

		if("startRecord".equals(action)){
			Long timeToRecord = args.optLong(0);

			startRecording();
			Handler handler = new Handler();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					stopRecording();
				}
			};
			handler.postDelayed(runnable, timeToRecord * 1000);
			return true;
		}else if("stopRecord".equals(action)){
			stopRecording();
			return true;
		}else{
			callback.error("Incorrect action name");
			return false;
		}

    }

    private void startRecording(){
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed: " + e.getMessage());
			callback.error("prepare() failed");
		}

		mRecorder.start();
	}

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		encodeBase64();
	}

	private void encodeBase64(){
		byte[] audioBytes;
		FileInputStream fileInputStream = null;
		try{
			File audioFile = new File(mFileName);
			if(audioFile.exists()){
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				fileInputStream = new FileInputStream(audioFile);
				byte[] buffer = new byte[1024];
				int bytesReads;
				while (-1 != (bytesReads =fileInputStream.read(buffer))){
					outputStream.write(buffer, 0, bytesReads);
				}

				audioBytes = outputStream.toByteArray();
				boolean deleted = audioFile.delete();
				if(deleted){
					callback.success(Base64.encodeToString(audioBytes, Base64.DEFAULT));
				}else{
					callback.error("Error deleting File");
				}			
			}else{
				callback.error("Base64 error, file do not exist");
			}
		}catch (Exception e){
			Log.e(LOG_TAG, "Base64 error " + e.getMessage());
		}finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "Base64 IS close error: " + e.getMessage());
				}
			}
			callback.error("Base64 error");
		}
	}
}
