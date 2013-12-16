package com.shephertz.app42.android.pushservice;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.unity3d.player.UnityPlayer;

public class App42PushService {

	private static App42PushService mInstance = null;
	private Context mContext;
	private ServiceContext serviceContext;

	private App42PushService(Context context) {
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
	public static App42PushService instance(Context coontext) {

		if (mInstance == null) {
			mInstance = new App42PushService(coontext);
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
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			try {
				messageReceived(deviceId, serviceContext.getCallBackRegister(), serviceContext.getGameObject());
			} catch (Throwable ex) {
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
		App42GCMService.projectNo = projectNo;
		serviceContext.saveProjectNo(projectNo);
	}

	/*
	 * Call This Function from Unity after Message is shown on Unity Screen This
	 * function reset PushMessage Count to zero
	 */
	public void resetCount() {
		App42GCMService.msgCount = 0;
	}
	
	/*
	 * This function calls from Unity to show last PushMessage to user.
	 */
	public void getLastMessage(){
		try {
			App42GCMService.msgCount = 0;
			messageReceived(serviceContext.getLastMessage(), serviceContext.getCallBackMessage(), serviceContext.getGameObject());
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
	
	public void registerForNotification(String callBackMessage,
			String gameObjectName,String callBackRegister) {
		serviceContext.saveUnityInfo(callBackMessage, gameObjectName,callBackRegister);
		registerOnGCM(getContext());
	}

}
