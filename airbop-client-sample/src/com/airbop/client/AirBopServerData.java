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

//import static com.airbop.client.CommonUtilities.TAG;
//import static com.airbop.client.CommonUtilities.displayMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
//import android.os.Build;
import android.util.Log;

public class AirBopServerData {
	
	private static final String TAG = "AirBopServerData";
	 
	public Location mLocation = null;
	//public String mBuildModel = null;
	//public String mVersionRelease = null;
	//public int mVersionSDK = -1;
	public String mLanguage = null;
	public String mCountry = null;
	public String mState = null;
	
	static final String LATITUDE = "lat";
	static final String LONGITUDE = "long";
	static final String LANGUAGE = "lang";
	static final String COUNTRY = "country";
	static final String STATE = "state";
	//static final String MODEL = "model";
	//static final String OS_VERSION = "os_version";
	//static final String SDK_VERSION = "sdk_version";
	

    private static final String PREFERENCES = "com.airbop.client.data";
	
	public AirBopServerData() {
		
	}
	
	public static AirBopServerData fillDefaults() {
		AirBopServerData server_data = new AirBopServerData();
		if (server_data != null) {
		//	server_data.mBuildModel = Build.MODEL;
		//	server_data.mVersionRelease = Build.VERSION.RELEASE;
		//	server_data.mVersionSDK = Build.VERSION.SDK_INT;
			Locale default_locale = Locale.getDefault();
			if (default_locale != null) {
				server_data.mLanguage = default_locale.toString();
				server_data.mCountry = default_locale.getCountry();
			}
			
			
		}
		return server_data;
	}

	public void fillParams(Map<String, String> params) {
		
		 Log.d(TAG, "LANGUAGE: " + mLanguage);
		 Log.d(TAG, "Location: " + mLocation);
		 Log.d(TAG, "COUNTRY: " + mCountry);
		 Log.d(TAG, "STATE: " + mState);
		 
		
		if (mLanguage != null) {
			params.put(LANGUAGE, mLanguage);
		}
		
		if (mLocation != null) {
			Float latitude = Float.valueOf(Double.toString(mLocation.getLatitude()));
			Float longitude = Float.valueOf(Double.toString(mLocation.getLongitude()));
			
			params.put(LATITUDE, latitude.toString());
			params.put(LONGITUDE, longitude.toString());
			
			Log.d(TAG, "LAT float: " + latitude.toString());
			Log.d(TAG, "LONG float: " + longitude.toString());
			Log.d(TAG, "LAT Double: " + Double.toString(mLocation.getLatitude()));
			Log.d(TAG, "LONG Double: " + Double.toString(mLocation.getLongitude()));
			//params.put(LATITUDE, Double.toString(mLocation.getLatitude()));
			//params.put(LONGITUDE, Double.toString(mLocation.getLongitude()));
		}
		
		if (mCountry != null) {
			params.put(COUNTRY, mCountry);
		}
		
		if (mState != null) {
			params.put(STATE, mState);
		}
		
		/*if (mBuildModel != null) {
			params.put(MODEL, mBuildModel);
		}
		
		if (mVersionRelease != null) {
			params.put(OS_VERSION, mVersionRelease);
		}
		
		if (mVersionSDK != -1) {
			params.put(SDK_VERSION, Integer.toString(mVersionSDK));
		}
		*/
	}
	
	public void loadCurrentLocation(Context context) {
		mLocation = null;
		if (context != null) {
			mLocation = readLocationFromPrefs(context);
			if (mLocation != null) {
				Geocoder gcd = new Geocoder(context, Locale.getDefault());
	
				try {
					List<Address> addresses = gcd.getFromLocation(mLocation.getLatitude()
							, mLocation.getLongitude()
							, 1);
					if (addresses.size() > 0) {
						Address ad = addresses.get(0);
						if (ad != null) {
							mCountry = ad.getCountryCode();
							mState = stateToStateCode(ad.getAdminArea());
						}
					} 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void saveCurrentLocation(Context context, Location location) {
		mLocation = location;
		if (context != null) {
			writeLocationToPrefs(context, location);
		}
	}
	
	public static void writeLocationToPrefs(Context context, Location location){
    	final SharedPreferences prefs = getPreferences(context);
    	if(prefs != null) {
    		
    		Editor editor = prefs.edit();
            editor.putString(LATITUDE, Double.toString(location.getLatitude()));
            editor.putString(LONGITUDE, Double.toString(location.getLongitude()));
            editor.commit();
    	}
    }
	
	public static Location readLocationFromPrefs(Context context){
    	final SharedPreferences prefs = getPreferences(context);
    	Location location = null;
    	if(prefs != null) {
    		
    		String latitude = prefs.getString(LATITUDE, null);
    		String longitude = prefs.getString(LONGITUDE, null);
    		
    		if ((latitude != null) && (longitude != null)) {
    			location = new Location("");
    			
    			location.setLatitude(Double.valueOf(latitude));
    			location.setLongitude(Double.valueOf(longitude));
    		}
    	}
    	return location;
    }
	
	public static void clearLocationPrefs(Context context){
    	final SharedPreferences prefs = getPreferences(context);
    	if(prefs != null) {
    		
    		Editor editor = prefs.edit();
            editor.remove(LATITUDE);
            editor.remove(LONGITUDE);
            editor.commit();
    	}
    }
    
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }
    
    public static String stateToStateCode(final String state) {
	    Map<String, String> states = new HashMap<String, String>();
	    states.put("Alabama","AL");
	    states.put("Alaska","AK");
	    states.put("Alberta","AB");
	    states.put("American Samoa","AS");
	    states.put("Arizona","AZ");
	    states.put("Arkansas","AR");
	    states.put("Armed Forces (AE)","AE");
	    states.put("Armed Forces Americas","AA");
	    states.put("Armed Forces Pacific","AP");
	    states.put("British Columbia","BC");
	    states.put("California","CA");
	    states.put("Colorado","CO");
	    states.put("Connecticut","CT");
	    states.put("Delaware","DE");
	    states.put("District Of Columbia","DC");
	    states.put("Florida","FL");
	    states.put("Georgia","GA");
	    states.put("Guam","GU");
	    states.put("Hawaii","HI");
	    states.put("Idaho","ID");
	    states.put("Illinois","IL");
	    states.put("Indiana","IN");
	    states.put("Iowa","IA");
	    states.put("Kansas","KS");
	    states.put("Kentucky","KY");
	    states.put("Louisiana","LA");
	    states.put("Maine","ME");
	    states.put("Manitoba","MB");
	    states.put("Maryland","MD");
	    states.put("Massachusetts","MA");
	    states.put("Michigan","MI");
	    states.put("Minnesota","MN");
	    states.put("Mississippi","MS");
	    states.put("Missouri","MO");
	    states.put("Montana","MT");
	    states.put("Nebraska","NE");
	    states.put("Nevada","NV");
	    states.put("New Brunswick","NB");
	    states.put("New Hampshire","NH");
	    states.put("New Jersey","NJ");
	    states.put("New Mexico","NM");
	    states.put("New York","NY");
	    states.put("Newfoundland","NF");
	    states.put("North Carolina","NC");
	    states.put("North Dakota","ND");
	    states.put("Northwest Territories","NT");
	    states.put("Nova Scotia","NS");
	    states.put("Nunavut","NU");
	    states.put("Ohio","OH");
	    states.put("Oklahoma","OK");
	    states.put("Ontario","ON");
	    states.put("Oregon","OR");
	    states.put("Pennsylvania","PA");
	    states.put("Prince Edward Island","PE");
	    states.put("Puerto Rico","PR");
	    states.put("Quebec","PQ");
	    states.put("Rhode Island","RI");
	    states.put("Saskatchewan","SK");
	    states.put("South Carolina","SC");
	    states.put("South Dakota","SD");
	    states.put("Tennessee","TN");
	    states.put("Texas","TX");
	    states.put("Utah","UT");
	    states.put("Vermont","VT");
	    states.put("Virgin Islands","VI");
	    states.put("Virginia","VA");
	    states.put("Washington","WA");
	    states.put("West Virginia","WV");
	    states.put("Wisconsin","WI");
	    states.put("Wyoming","WY");
	    states.put("Yukon Territory","YT");
	    
	    return states.get(state);
    }
}
