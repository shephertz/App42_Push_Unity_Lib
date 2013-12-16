package com.shephertz.app42.android.pushservice;

import com.google.android.gcm.GCMBroadcastReceiver;

import android.content.Context;

public class App42PushReciver extends GCMBroadcastReceiver{
	@Override
	protected String getGCMIntentServiceClassName(Context context) { 
		return "com.shephertz.app42.android.pushservice.App42PushService"; 
	} 
}
