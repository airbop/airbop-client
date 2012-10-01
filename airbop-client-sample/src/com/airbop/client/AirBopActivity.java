/*
 * Copyright 2012 Indigo Rose Software Design Corporation
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

import static com.airbop.client.CommonUtilities.AIRBOP_APP_KEY;
import static com.airbop.client.CommonUtilities.PROJECT_ID;
import static com.airbop.client.CommonUtilities.SERVER_URL;
import static com.airbop.client.CommonUtilities.displayMessage;


import com.google.android.gcm.GCMRegistrar;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;



/**
 * Simply base class 
 */
public class AirBopActivity extends Activity implements AirBopRegisterTask.RegTaskCompleteListener, LocationListener{
	
	//14 days in milliseconds
	public static final long AIRBOP_DEFAULT_ON_SERVER_LIFESPAN_MS =
            1000 * 3600 * 24 * 14;
  
    private AirBopRegisterTask  mRegisterTask = null;
    private AsyncTask<Void, Void, Void> mUnRegisterTask = null;
    protected AirBopServerData mServerData = null;
    private AirBopRegisterReceiver mRegisterReceiver = null;
    protected boolean mServiceRunning = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkNotNull(SERVER_URL, "SERVER_URL");
        checkNotNull(AIRBOP_APP_KEY, "AIRBOP_APP_KEY");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        
        
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);  
        
        //Ser the AirBop registration lifespan to be 14 days. This means that after 14 days
        //this client will attempt to reregister with AirBop. 
        GCMRegistrar.setRegisterOnServerLifespan(this, AIRBOP_DEFAULT_ON_SERVER_LIFESPAN_MS);
        
        mServerData = AirBopServerData.fillDefaults();
    }
   
    protected void register(final boolean withLocation) {	
    	if (withLocation) {
    		
    		Location location = ServerUtilities.getLastLocation(this);
    		if (location == null) {
    			// We didn't get the location
    			displayMessage(this, "Going to get current location.");
    			// We have to query the Location Manager for the location
    			// and get the result in onLocationChanged
    			ServerUtilities.getCurrentLocation(this
	    				, this);
    		} else {    			
    			mServerData.saveCurrentLocation(getApplicationContext()
    					, location);
    			//register
        		internalRegister();
    		}
    	} else {
    		//Remove any old location data
    		AirBopServerData.clearLocationPrefs(getApplicationContext());
    		//register
    		internalRegister();
    	}
    }
    
    private  void internalRegister() {	
     	final Context appContext = getApplicationContext();
    	final String regId = GCMRegistrar.getRegistrationId(appContext);
    	if (regId.equals("")) {
        	// We don't have a REG ID from GCM so let's ask for one. 
        	// The response from the GCM server will trigger: 
        	// GCMIntentService.onRegistered() where we will then register with
        	// AiRBop's servers.
    		displayMessage(appContext, "Asking GCM for th reg ID");
        	GCMRegistrar.register(appContext, PROJECT_ID);
        } else {
        	
            // We have a regID from the GCM, now check to see if
        	// we have successfully registered with AirBop's servers:
            if (GCMRegistrar.isRegisteredOnServer(appContext)) {
                // This device is already registered with GCM and with the AitBop
            	// servers, nothing to do here.
            	displayMessage(appContext, getString(R.string.already_registered));
            
            	return;
            }// else if (mRegisterTask == null){
            else if (mServiceRunning == false) {
            	// We have previously gotten the regID from the GCM server, but
            	// when we tried to register with AirBop in the
            	// GCMIntentService.onRegistered() callback it failed, so let's try again
            	
                // This will be done outside of the GUI thread.
            	
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
            	
            	
        		//mRegisterTask = new AirBopRegisterTask(this
	    		//	 , appContext
	    		//	 , regId
	    		//	 , mServerData);
	    	 	//mRegisterTask.execute(null, null, null);

            	
	    	 	if (mRegisterReceiver == null) {
	    	 		// Register receiver
					registerAirBopRegisterReceiver();
	    	 	}
	    	 	
	    	 	
    	 		Intent intent = new Intent(this, AirBopIntentService.class);
				intent.putExtra(AirBopIntentService.BUNDLE_REG_ID, regId);
									
				mServiceRunning = true;
				// Start Service		
				startService(intent);
	    	 	
            } else {
            	displayMessage(appContext, getString(R.string.reg_thread_running));
            }
        }
    }
    
    protected void unRegister(final boolean bUnregisterFromGCM) {
    	// Try to unregister, but not in the UI thread.
        // It's also necessary to cancel the thread onDestroy(),
        // hence the use of AsyncTask instead of a raw thread.
    	final Context appContext = getApplicationContext();
    	
    	if (mUnRegisterTask == null) {
    		
	    	final String regId = GCMRegistrar.getRegistrationId(this);
	    	// Only bother if we actually have a regID from GCM, otherwise
	    	// there is nothing to unregister
	        if (!regId.equals("")) {
		    	//final Context context = this;
		        mUnRegisterTask = new AsyncTask<Void, Void, Void>() {
		
		            @Override
		            protected Void doInBackground(Void... params) {
		            	
		                boolean unregistered = ServerUtilities.unregister(appContext
		                        		, regId
		                        		, AIRBOP_APP_KEY);
		                // If this worked unregister from the GCM servers
		                if ((unregistered) && (bUnregisterFromGCM)) {
		                    GCMRegistrar.unregister(appContext);
		                }
		                return null;
		            }
		
		            @Override
		            protected void onPostExecute(Void result) {
		            	mUnRegisterTask = null;
		            }
		
		        };
		        mUnRegisterTask.execute(null, null, null);
	        }
    	} else {
    		displayMessage(appContext, getString(R.string.unreg_thread_running));
    	}
    }

    
    
    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        if (mUnRegisterTask != null) {
        	mUnRegisterTask.cancel(true);
        }
        unregisterAirBopRegisterReceiver();
        unregisterFromLocationManager();
        GCMRegistrar.onDestroy(getApplicationContext());
        super.onDestroy();
    }

    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }
	public void onTaskComplete() {
		// We have registered
		mRegisterTask = null;		
	}
	
	private void unregisterFromLocationManager() {
		LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	    if (locationManager != null) {
	    	locationManager.removeUpdates(this);
	    }
	}
	
	public void onLocationChanged(Location location) {
		
		// Unregister from location manager
		unregisterFromLocationManager();
		displayMessage(this, "Activity onLocationChanged latitude: " 
    			+ location.getLatitude()
    			+ " longitude: " + location.getLongitude());
			
		//Set the location
		mServerData.saveCurrentLocation(getApplicationContext()
				, location);
		//register
		internalRegister();
		/*
		
		//Start the Task to register
		final Context appContext = getApplicationContext();
		if (appContext != null) {
		    
			
	    	displayMessage(appContext, "Activity onLocationChanged latitude: " 
	    			+ location.getLatitude()
	    			+ " longitude: " + location.getLongitude());
	    	
	    	displayMessage(appContext, "Going to register with airbop now using AirBopRegisterTask.");
    		
	    	mRegisterTask = new AirBopRegisterTask(this
    			 , appContext
    			 , GCMRegistrar.getRegistrationId(appContext)
    			 , mServerData);
    	 	mRegisterTask.execute(null, null, null);
    	 	
    	}
    	*/
	}
	
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
    
	public void registerAirBopRegisterReceiver() {
		
		if (mRegisterReceiver == null) {
			//Log.v(TAhttp://www.trucktrend.com/rss/all/index.htmlG, "registerRSSReceiver");
			//Receiver
			IntentFilter filter = new IntentFilter(AirBopIntentService.ACTION_REGISTRATION_PROCESSED);
			//filter.addAction(RssService.ACTION_FEED_STARTING);
			filter.addCategory(Intent.CATEGORY_DEFAULT);
			mRegisterReceiver = new AirBopRegisterReceiver();
			registerReceiver(mRegisterReceiver, filter);
		}
	}

	protected void unregisterAirBopRegisterReceiver() {
		if (mRegisterReceiver != null) {
			//Log.v(TAG, "unregisterRSSReceiver");
			unregisterReceiver(mRegisterReceiver);
			mRegisterReceiver = null;
		}
	}
	
	public class AirBopRegisterReceiver extends BroadcastReceiver {
		

		@Override
		public void onReceive(Context context, Intent intent) {
			displayMessage(context, "AirBopRegisterReceiver onReceive");
			mServiceRunning = false;
			displayMessage(context, "AirBopRegisterReceiver result: "
					+ intent.getBooleanExtra(AirBopIntentService.BUNDLE_AIRBOP_SUCCESS, false));
			unregisterReceiver(mRegisterReceiver);
		}
	}
}
