package com.airbop.client;

import static com.airbop.client.CommonUtilities.AIRBOP_APP_KEY;

import com.google.android.gcm.GCMRegistrar;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AirBopIntentService extends IntentService {

	public static final String ACTION_REGISTRATION_PROCESSED = "com.airbop.client.intent.action.REGISTRATION_PROCESSED";
	public static final String BUNDLE_REG_ID = "REG_ID";
	public static final String BUNDLE_AIRBOP_SUCCESS = "AIRBOP_SUCCESS";
	
	public AirBopIntentService() {
		super("AirBopIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		
		Context appContext = getApplicationContext();
		boolean registered = false;
		if (appContext != null) {
			registered = GCMRegistrar.isRegisteredOnServer(appContext);
	        if (!registered) {
				Bundle extras = intent.getExtras();
				String regID = null;
				if (extras != null) {
					regID = extras.getString(BUNDLE_REG_ID);
				}
				
				if (regID != null) {
					
					AirBopServerData server_data = AirBopServerData.fillDefaults();
			        server_data.loadCurrentLocation(appContext);
			        
			        registered = ServerUtilities.register(appContext
			        		, regID
			        		, AIRBOP_APP_KEY
			        		, server_data);
				    // At this point all attempts to register with the AirBop
				    // server failed, so we need to unregister the device
				    // from GCM - the app will try to register again when
				    // it is restarted. Note that GCM will send an
				    // unregistered callback upon completion, but
				    // GCMIntentService.onUnregistered() will ignore it.
				    if (!registered) {
				        GCMRegistrar.unregister(appContext);
				    }
				}
	        }
		}
	    Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(ACTION_REGISTRATION_PROCESSED);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(BUNDLE_AIRBOP_SUCCESS, registered);
		sendBroadcast(broadcastIntent);
		
		

	}

}
