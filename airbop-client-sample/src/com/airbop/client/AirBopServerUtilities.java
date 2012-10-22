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

import static com.airbop.client.CommonUtilities.AIRBOP_APP_KEY;
import static com.airbop.client.CommonUtilities.AIRBOP_APP_SECRET;
import static com.airbop.client.CommonUtilities.SERVER_URL;
import static com.airbop.client.CommonUtilities.USE_LOCATION;
import static com.airbop.client.CommonUtilities.displayMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
//import android.os.Build;
import android.util.Log;
import android.util.Pair;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import com.google.android.gcm.GCMRegistrar;

/**
 * A simple helper data class used to store data, register and 
 * unregister with the AirBop servers. 
 * 
 * This class contains all the necessary code to register and
 * unregister with the AirBop servers. It also shows how to format
 * the headers and body elements when posting to AirBop.
 * 
 * You are free to use it within your app, or translate the code
 * as necessary to fit your needs.
 */
public class AirBopServerUtilities {
	
	/**
	 * Simple  Exception class to store HTTP response codes returned by
	 * the AirBop serves in case of an error.
	 */
	public class AirBopException extends Exception {
	
		private static final long serialVersionUID = 2964933186957972677L;
		
		private int mHTTPResponseCoder = 0;
		
		public AirBopException(){
			super();             
		}
		public AirBopException(String message) {
			super(message); 
		}
		public AirBopException(String message, int responseCode) {
			 super(message); 
			 mHTTPResponseCoder = responseCode;
		}
		public AirBopException(String message, Throwable cause) {
			super(message, cause); 
		}
		public int getHTTPResponseCode() {
			return mHTTPResponseCoder;
		}
	}
	
	private static final String TAG = "AirBopServerUtilities";
	
	//Post Param values
	public Location mLocation = null;
	public String mLanguage = null;
	public String mCountry = null;
	public String mState = null;
	public String mLabel = null;
	public String mRegId = null;
	//Post Param keys
	public static final String COUNTRY = "country";
	public static final String LABEL = "label";
	public static final String LANGUAGE = "lang";
	public static final String LATITUDE = "lat";
	public static final String LONGITUDE = "long";
	public static final String REGISTRATION_ID = "reg";
	public static final String STATE = "state";
	
	//Header keys
	public static final String HEADER_APP = "x-app-key";
	public static final String HEADER_TIMESTAMP = "x-timestamp";
	public static final String HEADER_SIGNATURE = "x-signature";
	
	//Defines
	private  static final int MAX_ATTEMPTS = 5;
	private  static final int BACKOFF_MILLI_SECONDS = 2000;
	private  static final int AIRBOP_TIMOUT_MILLI_SECONDS = 1000 * 30;
    private static final String PREFERENCES = "com.airbop.client.data";
    public static final String OUTPUT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";
    
    
    
    private static final Random random = new Random();
    
	public AirBopServerUtilities() {
		
	}
	
	public AirBopServerUtilities(final String regId) {
		mRegId = regId;
	}
	
	/**
	 * Static helper function that creates a AirBopServerData instance and
	 * populates it with the default data. For now this is the language.
	 * @param regId Registration ID that we will store in the created instance.
	 * @return AirBopServerData
	 */
	public static AirBopServerUtilities fillDefaults(final String regId) {
		AirBopServerUtilities server_data = new AirBopServerUtilities(regId);
		if (server_data != null) {
			Locale default_locale = Locale.getDefault();
			if (default_locale != null) {
				server_data.mLanguage = default_locale.getLanguage();
			}	
		}
		return server_data;
	}
	
	/**
	 * Fill a list with the current data values in alphabetical order. Used to construct
	 * the body when posting.
	 * @param list_params Will be filled with the data. A list of pairs.
	 * @param isRegister Is this a registration or an unregistration? This controls
	 * which parameters will be added to the list.
	 */
	public void fillAlphaPairList(List<Pair<String, String>> list_params
			, final boolean isRegister) {
				
		if ((isRegister) && (mCountry != null)) {
			list_params.add(Pair.create(COUNTRY, mCountry));
		}
		
		if ((isRegister) && (mLabel != null)) {
			list_params.add(Pair.create(LABEL, mLabel));
		}
		
		if ((isRegister) && (mLanguage != null)) {
			list_params.add(Pair.create(LANGUAGE, mLanguage));
		}
		
		if ((isRegister) && (mLocation != null)) {
			Float latitude = Float.valueOf(Double.toString(mLocation.getLatitude()));
			Float longitude = Float.valueOf(Double.toString(mLocation.getLongitude()));
		
			list_params.add(Pair.create(LATITUDE, latitude.toString()));
			list_params.add(Pair.create(LONGITUDE, longitude.toString()));
		}
		
		if (mRegId != null) {
			list_params.add(Pair.create(REGISTRATION_ID, mRegId));
		}
		
		if ((isRegister) && (mState != null)) {
			list_params.add(Pair.create(STATE, mState));
		}
	}
	
	/***************************************************
	 * START: Preference work
	 ***************************************************/
	/**
	 * Helper function to get the shared prefs
	 * @param context Context used to get the prefs
	 */
	private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }
	/**
	 * Load the necessary data from the prefs. We will load the label,
	 * latitude, and the longitude. The lat and long will be converted
	 * into a location object and used to set the country and state.
	 * @param context Context used to get the prefs
	 */
	public void loadDataFromPrefs(Context context) {
		if (context != null) {
			final SharedPreferences prefs = getPreferences(context);
	    	
	    	if(prefs != null) {
	    		mLabel = prefs.getString(LABEL, null);
	    		Log.v(TAG, "loadDataFromPrefs label: " + mLabel);
	    		
	    		if (USE_LOCATION) {
		    		String latitude = prefs.getString(LATITUDE, null);
		    		String longitude = prefs.getString(LONGITUDE, null);
		    		
		    		if ((latitude != null) && (longitude != null)) {
		    			mLocation = new Location("");
		    			
		    			mLocation.setLatitude(Double.valueOf(latitude));
		    			mLocation.setLongitude(Double.valueOf(longitude));
		    			
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
							e.printStackTrace();
						}
		    		}
	    		}
	    	}
		}
	}
	
	/**
	 * Save the current server data to the prefs so that the
	 * intent service can read them. We store the label, latitude,
	 * and the longitude.
	 * @param context Context needed to access the preferences
	 */
	public void saveCurrentDataToPrefs(Context context) {
		if (context != null) {
			
			final SharedPreferences prefs = getPreferences(context);
	    	if(prefs != null) {
	    		Editor editor = prefs.edit();
	    		if (USE_LOCATION) {
	    			
	    			editor.putString(LATITUDE, Double.toString(mLocation.getLatitude()));
	    			editor.putString(LONGITUDE, Double.toString(mLocation.getLongitude()));
	    		}
	    		
	    		editor.putString(LABEL, mLabel); 
	            editor.commit();
	    	}
		}
	}
	/**
	 * Remove the location data from the preferences
	 * @param context The context needed to get the shared prefs
	 */
	public static void clearLocationPrefs(Context context){
    	final SharedPreferences prefs = getPreferences(context);
    	if(prefs != null) {
    		
    		Editor editor = prefs.edit();
            editor.remove(LATITUDE);
            editor.remove(LONGITUDE);
            editor.commit();
    	}
    }
	/***************************************************
	 * END: Preference work
	 ***************************************************/
    
	/**
     * Convert the name of a state to a state abbreviation
     * @param state The full name of the state
     * @return whether the registration succeeded or not.
     */
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
    
    /**
     * Calculate the current timestamp since the Epoch, January 1, 1970 00:00 UTC.
     * @return The current timestamp in seconds
     */
    public static String getCurrentTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }
    
    /**
     * Construct the URL for the signature header
     * @param method The method being use to contact the header. POST for now.
     * @param url The URL we are going to contact.
     * @param timestamp The timestamp of the message
     * @param body The body that we will be sending
     * @return The created URI.
     */
    private String constructSignatureURI(
    		final String method
    		, final String url
    		, final String timestamp
    		, final String body) {
    	/**
    	 * The signature is constructed using the following value:
    	 * "POST" + request_uri + AIRBOP_APP_KEY + timestamp + request.body + AIRBOP_APP_SECRET
    	 */

    	StringBuilder uriBuilder = new StringBuilder();
    	// METHOD
    	uriBuilder.append(method);
    	// URL
    	uriBuilder.append(url);
    	// APP_KEY
    	uriBuilder.append(AIRBOP_APP_KEY);
    	// timestamp
    	uriBuilder.append(timestamp);
    	// body
    	uriBuilder.append(body);
    	// ssssshhhhhh
    	uriBuilder.append(AIRBOP_APP_SECRET);
    	
    	return uriBuilder.toString();
    	
    }
    
    /**
     * Get the body to post to the server as JSON.  
     * @param isRegister Is this a registration or an unregistration? Controls
     * the parameters that will be in the body
     * @return The body to post.
     */
    public String getBodyAsJSON(final boolean isRegister){
    	List<Pair<String, String>> list_params = new ArrayList<Pair<String, String>>();
    	fillAlphaPairList(list_params, isRegister);
    	
    	StringBuilder bodyBuilder = new StringBuilder();
    	//Starting bracket
    	bodyBuilder.append("{");
        Iterator<Pair<String, String>> iterator = list_params.iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
        	Pair<String, String> keyValue = iterator.next();
        	// name
        	bodyBuilder.append('"').append(keyValue.first)
            	.append("\":");
        	// value
        	bodyBuilder.append('"').append(keyValue.second)
        		.append('"');
        	//Do we need the comma?
            if (iterator.hasNext()) {
                bodyBuilder.append(',');
            }
        }
        //ending bracket
        bodyBuilder.append("}");
        
        return bodyBuilder.toString();
    }
    
    /**
     * Get the body to post to the server as A url encoded string. 
     * @param isRegister Is this a registration or an unregistration? Controls
     * the parameters that will be in the body
     * @return The body to post.
     */
    public String getBodyAsUrlEncoded(final boolean isRegister){
    	List<Pair<String, String>> list_params = new ArrayList<Pair<String, String>>();
    	fillAlphaPairList(list_params, isRegister);
    	
    	StringBuilder bodyBuilder = new StringBuilder();
    	//Starting bracket
    	Iterator<Pair<String, String>> iterator = list_params.iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
        	Pair<String, String> keyValue = iterator.next();
            bodyBuilder.append(keyValue.first).append('=')
                    .append(keyValue.second);
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        
        return bodyBuilder.toString();
    }
    
    /**
     * Get the Body to post to the sever
     * @param isRegister A registration or uneregistration?
     * @param asJSON Do we want a JSON body or a url encoded body?
     * @return The body
     */
    public String getBody(final boolean isRegister
						, final boolean asJSON) {
    	if (asJSON) {
    		return getBodyAsJSON(isRegister);
    	} else {
    		return getBodyAsUrlEncoded(isRegister);
    	}
    }
    
    /**
     * computes the SHA-256 hash of a string
     * @param input The string to hash
     * @return The hashed string
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String computeHash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();

        byte[] byteData = digest.digest(input.getBytes("UTF-8"));
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < byteData.length; i++){
          sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    
    /**
     * Issue a POST request to the AirBop server.
     * @param url_endpoint The URL to post to 
     * @param isRegister IS this a registration or unregistration?
     * @param asJSON Do we want the post body as JSON or URL encoded
     * @throws IOException
     * @throws AirBopException Used to return the HTTP reponse code.
     */
    public void post(String url_endpoint
			, final boolean isRegister
			, final boolean asJSON)
            throws IOException, AirBopException {
        URL url;
        try {
            url = new URL(url_endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + url_endpoint);
        }
        
        //Timestamp
        String timestamp = getCurrentTimestamp();
        String body = getBody(isRegister, asJSON);
        String signature = constructSignatureURI("POST"
        		, url_endpoint
        		, timestamp
        		, body);
        String signature_hash = signature;
        try {
			signature_hash = computeHash(signature);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.v(TAG, "Posting '" + body + "' to " + url);
        
        byte[] bytes = body.getBytes();
        
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(AIRBOP_TIMOUT_MILLI_SECONDS);
            conn.setReadTimeout (AIRBOP_TIMOUT_MILLI_SECONDS);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            
            if (asJSON) {
            	conn.setRequestProperty("Content-Type",
            		"application/json;charset=UTF-8");
            } else {
            	conn.setRequestProperty("Content-Type",
        			"application/x-www-form-urlencoded;charset=UTF-8");
            }
            // X Headers
            conn.addRequestProperty(HEADER_TIMESTAMP, timestamp);
            conn.addRequestProperty(HEADER_APP, AIRBOP_APP_KEY);
            conn.addRequestProperty(HEADER_SIGNATURE, signature_hash);
            
            Log.i(TAG, "About to post");
           
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
            		throw new AirBopException("Error response code returned by the AirBop servers.", status);
            	} else if ((status >=400) && (status <= 499)) { //400 codes
            		throw new AirBopException("Error response code returned by the AirBop servers.", status);
            	}
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
    
    /**
     * Attempt to register with the AirBop servers
     * @param context Just used for messages
     * @param server_data Server data containing info to register
     * with
     * @return success or failure
     */
    static boolean register(final Context context
		    , final AirBopServerUtilities server_data) {
		if (context == null) {
			return false;
		}
		
		Log.i(TAG, "registering device (regId = " + server_data.mRegId + ")");
		
		String serverUrl = SERVER_URL + "register";
		
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		// Once GCM returns a registration id, we need to register it in the
		// demo server. As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
		    Log.d(TAG, "Attempt #" + i + " to register");
		    try {
		        displayMessage(context, context.getString(
		                R.string.server_registering, i, MAX_ATTEMPTS));
		       
		        server_data.post(serverUrl, true, true);
		
		        // If we got here there was no exception so set
		        // the flag to true
		        GCMRegistrar.setRegisteredOnServer(context, true);
		        displayMessage(context, context.getString(R.string.server_registered));
		        long lifespan = GCMRegistrar.getRegisterOnServerLifespan(context);
		        long expirationTime = System.currentTimeMillis() + lifespan;
		        
		        displayMessage(context, "Registration expires on: "+ new Timestamp(expirationTime));
		        return true;
		        
		    }  
		    catch (AirBopException airbop_e) {
		        
		    	String error_msg = airbop_e.getMessage();
		    	Log.i(TAG, "Error message:  " + error_msg);
		    	int error_code = airbop_e.getHTTPResponseCode();
		    	
		    	            	
		    	if ((error_code >= 400) && (error_code <= 499)) {
		        	displayMessage(context
		        			, context.getString(R.string.request_error, error_code));
		        	return false;
		        } else {
		        	
		        	displayMessage(context
		        			, context.getString(R.string.airbop_server_reg_failed, airbop_e));
		        	
		        	// Here we are simplifying and retrying on any error; in a real
		            // application, it should retry only on unrecoverable errors
		            // (like HTTP error code 503).
		            Log.e(TAG, "Failed to register on attempt " + i, airbop_e);
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
		            // increase the backoff exponentially
		            backoff *= 2;
		        }
		    }
		    catch (SocketTimeoutException e) {
		    	 Log.e(TAG, "Failed to register on attempt (timeout)" + i, e);
		            displayMessage(context, context.getString(R.string.airbop_server_reg_failed_timeout, e.getMessage()));
		            return false;
		    }
		    catch (IOException e) {
		    	// Probably caused by a 401 error code
	            Log.e(TAG, "Failed to register on attempt " + i, e);
	            displayMessage(context, context.getString(R.string.airbop_server_reg_failed_401, e.getMessage()));
	            return false;
		    }
		    
		}
		String message = context.getString(R.string.server_register_error,
		        MAX_ATTEMPTS);
		displayMessage(context, message);
		return false;
	}
    
    /**
     * Get the current location from the location manager, and when we get it
     * post that information to the Airbop servers
     * @param context Used to display messages
     * @param regId Registration ID to unregister
     * @return success or failure
     */
    static boolean unregister(final Context context
			, final String regId) {
		Log.i(TAG, "unregistering device (regId = " + regId + ")");
		displayMessage(context, context.getString(R.string.unregister_device));
		String serverUrl = SERVER_URL + "unregister";
		
		AirBopServerUtilities server_data = new AirBopServerUtilities(regId);
		try {
			
			server_data.post(serverUrl, false, true);
			
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
			Log.e(TAG, "Failed to unregister on attempt " + e);
			String message = context.getString(R.string.server_unregister_error,
					e.getMessage());
			displayMessage(context, message);
			return false;
		}catch (AirBopException e) {
			Log.e(TAG, "Failed to unregister on attempt " + e);
			String message = context.getString(R.string.server_unregister_error,
					e.getMessage());
			displayMessage(context, message);
			return false;
		}
		return true;
	}
    
    
}
