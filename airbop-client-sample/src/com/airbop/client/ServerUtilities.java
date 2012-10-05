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

import static com.airbop.client.AirBopServerData.REGISTRATION_ID;
import static com.airbop.client.AirBopServerData.AIRBOP_KEY;
import static com.airbop.client.CommonUtilities.SERVER_URL;
import static com.airbop.client.CommonUtilities.TAG;
import static com.airbop.client.CommonUtilities.displayMessage;

import com.google.android.gcm.GCMRegistrar;
//import com.google.android.gcm.demo.app.R;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;



/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
	static boolean register(final Context context
					, final String regId
					, final String airBopId
				    , final AirBopServerData server_data) {
		if (context == null) {
			return false;
		}
		
		Log.i(TAG, "registering device (regId = " + regId + ")");
		
		String serverUrl = SERVER_URL + "/register";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(REGISTRATION_ID, regId);
		params.put(AIRBOP_KEY, airBopId);
		server_data.fillParams(params);
		displayMessage(context, "Register Params: " + params);		
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                displayMessage(context, context.getString(
                        R.string.server_registering, i, MAX_ATTEMPTS));
                
                post(serverUrl, params);
                // If we got here there was no exception so set
                // the flag to true
                GCMRegistrar.setRegisteredOnServer(context, true);
                displayMessage(context, context.getString(R.string.server_registered));
                long lifespan = GCMRegistrar.getRegisterOnServerLifespan(context);
                long expirationTime = System.currentTimeMillis() + lifespan;
                
                displayMessage(context, "Registration expires on: "+ new Timestamp(expirationTime));
                return true;
                
            } catch (IOException e) {
                /*if (e.getMessage().equals("409")) {
                	displayMessage(context
                			, context.getString(R.string.already_registered));
                	return true;
                } */
            	int error_code = Integer.valueOf(e.getMessage());
            	if ((error_code >= 400) && (error_code <= 499)) {
                	displayMessage(context
                			, context.getString(R.string.request_error, error_code));
                	return false;
                } else {
                	
	            	displayMessage(context
	            			, context.getString(R.string.airbop_server_reg_failed, e));
	            	
	            	// Here we are simplifying and retrying on any error; in a real
	                // application, it should retry only on unrecoverable errors
	                // (like HTTP error code 503).
	                Log.e(TAG, "Failed to register on attempt " + i, e);
	                if (i == MAX_ATTEMPTS) {
	                    break;
	                }
	                try {
	                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
	                    Thread.sleep(backoff);
	                } catch (InterruptedException e1) {
	                    // Activity finished before we complete - exit.
	                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
	                    Thread.currentThread().interrupt();
	                    return false;
	                }
	                // increase backoff exponentially
	                backoff *= 2;
	            }
            }
        }
        String message = context.getString(R.string.server_register_error,
                MAX_ATTEMPTS);
        displayMessage(context, message);
        return false;
    }

    /**
     * Unregister this account/device pair within the server.
     */
    static boolean unregister(final Context context
    						, final String regId
    						, final String airBopId) {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        displayMessage(context, context.getString(R.string.unregister_device));
        String serverUrl = SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        
        params.put("app", airBopId);
		params.put("reg", regId);
		
        try {
        	post(serverUrl, params);
            // If there is no exception we've unregistered so set the flag
            // to false.
            GCMRegistrar.setRegisteredOnServer(context, false);
            String message = "** " + context.getString(R.string.server_unregistered);
            displayMessage(context, message);
        } catch (IOException e) {
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            displayMessage(context, message);
            return false;
        }
        return true;
    }
    
    /**
     * Simple helper that gets the criteria that we want. 
     * @return
     */
    public static Criteria getCriteria() {
    	
    	Criteria criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	    criteria.setPowerRequirement(Criteria.POWER_LOW);
	    criteria.setAltitudeRequired(false);
	    criteria.setBearingRequired(false);
	    criteria.setSpeedRequired(false);
	    criteria.setCostAllowed(true);
	    
	    return criteria;
    }
    
    /**
     * Get the last location from the LocationManager, if it's available, if not
     * return null.
     * @param appContext
     * @return
     */
    public static Location getLastLocation(final Context appContext) {
    	Location location = null;
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
    	
    	Criteria criteria = getCriteria();
    	LocationManager locationManager = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
	    if (locationManager != null) {
	    	String provider = locationManager.getBestProvider(criteria, true);
	    	
    		locationManager.requestLocationUpdates(provider, 2000, 10,
                    locationListener);    
    		// We've posted so let the caller know
    		return true;
	    }
	    // We couldn't get the location manager so let the caller know
	    return false;
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if ((status != 200) 
            	&& (status != 201)
            	&& (status != 202)){
            	if ((status >=500) && (status <= 599)) { //500 codes
            		throw new IOException(Integer.toString(status));
            	} else if ((status >=400) && (status <= 499)) { //400 codes
            		throw new IOException(Integer.toString(status));
            	}
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
    
    
}
