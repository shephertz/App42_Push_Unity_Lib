package com.shephertz.app42.android.pushservice;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Exception;
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

	public void regirsterPushOnApp42(final Context context,
			final String userID, final String deviceId) {
		new Thread() {
			@Override
			public void run() {
				try {
					App42API.buildPushNotificationService().storeDeviceToken(
							userID, deviceId);
					messageReceived("Registration done with user " + userID,
							serviceContext.getCallBackMethod(),
							serviceContext.getGameObject());
					Log.d("App42", "Registration done with user " + userID);

				} catch (App42Exception e) {
					Log.e("App42", e.getMessage());

				}
			}
		}.start();
	}

	/*
	 * This function allows to register device for PushNotification service
	 */
	public void registerForPushNotification(Context context, String userID) {
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
				regirsterPushOnApp42(context, userID, deviceId);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void messageReceived(String message, String method,
			String gameObject) {
		UnityPlayer.UnitySendMessage(gameObject, method, message);
		messageOpenLog(message);
		Log.i(" Message Sent : ", message);
	}

	private static void messageOpenLog(String message) {
		App42API.buildLogService().setEvent("Message", "Open  " + message,
				new App42CallBack() {

					@Override
					public void onSuccess(Object arg0) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onException(Exception arg0) {
						System.out.println(" onMessage  Exception : " + arg0);

					}
				});
	}

	public void setCurrentUser(String userId) {
		serviceContext.saveApp42UserId(userId);
	}

	public void setSessionId(String sessionId) {
		App42API.setUserSessionId(sessionId);
	}

	public void intialize(String apiKey, String secretKey) {
		serviceContext.saveApiSecretKey(apiKey, secretKey);
		App42API.initialize(getContext(), apiKey, secretKey);
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

	public void registerForNotification(String callBackMethod,
			String gameObjectName) {
		serviceContext.saveUnityInfo(callBackMethod, gameObjectName);
		registerForPushNotification(getContext(),
				serviceContext.getApp42UserId());
	}

}
