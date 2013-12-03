package com.shephertz.app42.android.pushservice;

import java.io.IOException;
import java.io.InputStream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.App42Log;

public class GCMIntentService extends GCMBaseIntentService {

	public static int msgCount = 0;
	public static String notificationMessage = "";
	static String projectNo = "<Your Project No>";

	public GCMIntentService() {
		super(projectNo);
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		Log.i(TAG, "Device registered: regId = " + arg1);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {

		Bundle b = intent.getExtras();
		String message = (String) b.get("message");
		Log.i(TAG, "Received message " + message);
		App42API.buildLogService().setEvent("Message", "Delivered",
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

		notificationMessage = message;
		displayMessage(context, message);
		generateNotification(context, message);
		try {
			App42Service.messageReceived(message,
					ServiceContext.instance(context).getCallBackMethod(),
					ServiceContext.instance(context).getGameObject());
		} catch (Exception e) {
			gameClosedLog(e.toString());
			e.printStackTrace();
		} catch (Error error) {
			gameClosedLog(error.toString());
		}
		// TODO: handle exception
	}

	private static void gameClosedLog(String exception) {
		App42API.buildLogService().setEvent("Message",
				"application is closed : " + exception, new App42CallBack() {

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

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = "" + total + "Message deleted ";
		displayMessage(context, message);
		generateNotification(context, message);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {

		Log.i(TAG, "Device registered: regId = " + registrationId);
		App42Service.instance(context).regirsterPushOnApp42(context,
				ServiceContext.instance(context).getApp42UserId(),
				registrationId);

	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		Log.i(TAG, "onUnregistered " + arg1);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private void generateNotification(Context context, String message) {

		long when = System.currentTimeMillis();
		String activityName = null;
		try {
			activityName = getActivtyName();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent;
		try {
			notificationIntent = new Intent(context,
					Class.forName(activityName));

		} catch (Exception e) {
			notificationIntent = new Intent();

		}
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(context)
				.setContentTitle(
						ServiceContext.instance(context).getGameObject())
				.setContentText(message).setContentIntent(intent)
				.setSmallIcon(android.R.drawable.ic_dialog_info).setWhen(when)
				.setNumber(++msgCount).setLargeIcon(getBitmapFromAssets())
				.setLights(Color.YELLOW, 1, 2).setAutoCancel(true).build();

		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);
	}

	public Bitmap getBitmapFromAssets() {
		AssetManager assetManager = getAssets();
		InputStream istr;
		try {
			istr = assetManager.open("app_icon.png");
			Bitmap bitmap = BitmapFactory.decodeStream(istr);
			return bitmap;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private String getActivtyName() {
		String activityName = null;
		ComponentName myService = new ComponentName(this, this.getClass());
		try {
			Bundle data = getPackageManager().getServiceInfo(myService,
					PackageManager.GET_META_DATA).metaData;
			App42Log.debug(" Message Activity Name : "
					+ data.getString("onMessageOpen"));
			activityName = data.getString("onMessageOpen");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return activityName;
	}

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(ServiceConstants.DisplayMessageAction);
		intent.putExtra(ServiceConstants.NotificationMessage, message);
		context.sendBroadcast(intent);
	}

}
