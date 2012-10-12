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
	public static final String BUNDLE_REGISTRATION_TASK = "REG_TASK";
	
	public AirBopIntentService() {
		super("AirBopIntentService");
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		
		Context appContext = getApplicationContext();
		boolean registered = false;
		if (appContext != null) {
			registered = GCMRegistrar.isRegisteredOnServer(appContext);
	       
			Bundle extras = intent.getExtras();
			String regID = null;
			boolean bRegister = true;
			if (extras != null) {
				regID = extras.getString(BUNDLE_REG_ID);
				bRegister = extras. getBoolean(BUNDLE_REGISTRATION_TASK, true);
			}
			
			if (regID != null) {
				
				if (bRegister) {
					if (!registered) {
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
				} else {
					if (registered) {
						registered = ServerUtilities.unregister(appContext
                        		, regID
                        		, AIRBOP_APP_KEY);
		                // If this worked unregister from the GCM servers
		                if (registered) {
		                    GCMRegistrar.unregister(appContext);
		                }
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
