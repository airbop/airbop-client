/*
 * Copyright 2012 Indigo Rose Software Design Corporation
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.airbop.client;

import static com.airbop.client.CommonUtilities.USE_LOCATION;
import static com.airbop.client.CommonUtilities.displayMessage;

import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */ 
    //static final String SERVER_URL = "http://www.airbop.com/";
    static final String SERVER_URL = "http://www.airbop.com/api/v1/";

    /**
     * Google API project id registered to use GCM.
     */
    static final String PROJECT_ID = <<REPLACE_ME>>;
    
    /**
     * AirBop App key to identify this app
     */
    static final String AIRBOP_APP_KEY = <<REPLACE_ME>>;
    
    /**
     * AIRBOP_APP_SECRET App key to identify this app shhhh
     */
    static final String AIRBOP_APP_SECRET = <<REPLACE_ME>>;
    
    /**
     * Should we send the location to the AirBopServer
     */
    static final boolean USE_LOCATION = true;
    
    /**
     * Should we use the IntentService or the AsyncTask
     */
    static final boolean USE_SERVICE = true;
    
    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    /**
     * Intent used to display a message in the screen.
     */
    static final String DISPLAY_MESSAGE_ACTION =
            "com.airbop.client.app.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
    
    static void displayMessageFromIntent(Context context, Intent intent) {
       if (intent != null) {
    	   Bundle bundle = intent.getExtras();
    	   if (bundle != null) {
    		   Set<String> keys = bundle.keySet();
    		   if (keys != null) {
    			   for (String key : keys) { 
    				   Object o = bundle.get(key);
    				   if (o != null) {
    					   displayMessage(context, "Key: " + key + " value: " + o);
    				   }
    			   }
    		   }
    	   } else {
    		   displayMessage(context, "Extras are null");
    	   }
       } else {
    	   displayMessage(context, "Intent is null");
       }
    }
    
    /************************
     * Language helpers
     */
    
    /**
     * Simple helper that gets the location criteria that we want. 
     * @return
     */
    public static Criteria getCriteria() {
    	if (USE_LOCATION) {
	    	Criteria criteria = new Criteria();
		    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		    criteria.setPowerRequirement(Criteria.POWER_LOW);
		    criteria.setAltitudeRequired(false);
		    criteria.setBearingRequired(false);
		    criteria.setSpeedRequired(false);
		    criteria.setCostAllowed(true);
		    
		    return criteria;
    	} 
    	return null;
    }
    
    /**
     * Get the last location from the LocationManager, if it's available, if not
     * return null.
     * @param appContext
     * @return
     */
    public static Location getLastLocation(final Context appContext) {
    	Location location = null;
    	if (USE_LOCATION) {
	    	Criteria criteria = getCriteria();
	    	LocationManager locationManager = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
		    if (locationManager != null) {
		    	String provider = locationManager.getBestProvider(criteria, true);
			    location = locationManager.getLastKnownLocation(provider); 
			    
			    if (location != null) {
			    	displayMessage(appContext, "Got last location latitude: " 
	 		    			+ location.getLatitude()
	 		    			+ " longitude: " + location.getLongitude());
			    }
	    	}
    	}
	    return location;
    }
    
    /** 
     * Get the current location from the location manager, and when we get it
     * post that information to the Airbop servers
     * @param appContext
     * @param regId
     * @return
     */
    public static boolean getCurrentLocation(LocationListener locationListener
    		, final Context appContext
    		) {
    	if (USE_LOCATION) {
	    	Criteria criteria = getCriteria();
	    	LocationManager locationManager = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
		    if (locationManager != null) {
		    	String provider = locationManager.getBestProvider(criteria, true);
		    	
	    		locationManager.requestLocationUpdates(provider, 2000, 10,
	                    locationListener);    
	    		// We've posted so let the caller know
	    		return true;
		    }
    	}
	    // We couldn't get the location manager so let the caller know
	    return false;
    }
 
}
