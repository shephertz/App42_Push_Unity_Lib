package com.shephertz.app42.android.pushservice;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.unity3d.player.UnityPlayer;

public class App42Service {

	private static App42Service mInstance = null;
	private Context mContext;
	private ServiceContext serviceContext;

	private App42Service(Context context) {
		setContext(context);
		this.serviceContext = ServiceContext.instance(context);
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context context) {
		this.mContext = context;
	}

	/*
	 * instance of class
	 */
	public static App42Service instance(Context coontext) {

		if (mInstance == null) {
			mInstance = new App42Service(coontext);
		}
		return mInstance;
	}

	/*
	 * This function allows to register device for PushNotification service
	 */
	public void registerOnGCM(Context context) {
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);
		final String deviceId = GCMRegistrar.getRegistrationId(context);
		if (deviceId.equals("")) {
			try {
				GCMRegistrar.register(context, serviceContext.getProjectNo());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				messageReceived(deviceId, serviceContext.getCallBackRegister(), serviceContext.getGameObject());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void messageReceived(String message, String method,
			String gameObject) {
		UnityPlayer.UnitySendMessage(gameObject, method, message);
		Log.i(" Message Sent : ", message);
	}

	
	
	public void setProjectNo(String projectNo) {
		GCMIntentService.projectNo = projectNo;
		serviceContext.saveProjectNo(projectNo);
	}

	/*
	 * Call This Function from Unity after Message is shown on Unity Screen This
	 * function reset PushMessage Count to zero
	 */
	public void resetCount() {
		GCMIntentService.msgCount = 0;
	}

	public void registerForNotification(String callBackMessage,
			String gameObjectName,String callBackRegister) {
		serviceContext.saveUnityInfo(callBackMessage, gameObjectName,callBackRegister);
		registerOnGCM(getContext());
	}

}
