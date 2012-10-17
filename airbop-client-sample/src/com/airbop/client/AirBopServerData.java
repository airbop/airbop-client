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

//import static com.airbop.client.CommonUtilities.displayMessage;


import static com.airbop.client.CommonUtilities.AIRBOP_APP_KEY;
import static com.airbop.client.CommonUtilities.SECRET_KEY;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
import java.net.URL;

public class AirBopServerData {
	
	private static final String TAG = "AirBopServerData";
	
	//Post Params
	public Location mLocation = null;
	public String mLanguage = null;
	public String mCountry = null;
	public String mState = null;
	public String mLabel = null;
	//public String mAppKey = AIRBOP_APP_KEY;
	public String mRegId = null;
	
	//Headers
	static final String HEADER_TIMESTAMP = "x-timestamp";
	static final String HEADER_APP = "x-app-key";
	static final String HEADER_SIGNATURE = "x-signature";
	
	
	static final String LATITUDE = "lat";
	static final String LONGITUDE = "long";
	static final String LANGUAGE = "lang";
	static final String COUNTRY = "country";
	static final String STATE = "state";
	static final String REGISTRATION_ID = "reg";
	//static final String APP_KEY = "app";
	static final String LABEL = "label";
	
	
	
    private static final String PREFERENCES = "com.airbop.client.data";
    public static final String OUTPUT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";
    
	public AirBopServerData() {
		
	}
	
	public AirBopServerData(final String regId) {
		mRegId = regId;
	}
	
	public static AirBopServerData fillDefaults(final String regId) {
		AirBopServerData server_data = new AirBopServerData(regId);
		if (server_data != null) {
			Locale default_locale = Locale.getDefault();
			if (default_locale != null) {
				server_data.mLanguage = default_locale.toString();
			}	
		}
		return server_data;
	}

	public void fillParams(Map<String, String> params) {
		
		Log.d(TAG, "LANGUAGE: " + mLanguage);
		Log.d(TAG, "Location: " + mLocation);
		Log.d(TAG, "COUNTRY: " + mCountry);
		Log.d(TAG, "STATE: " + mState);
		
		if (mCountry != null) {
			params.put(COUNTRY, mCountry);
		}
		
		if (mLabel != null) {
			params.put(LABEL, mLabel);
		}
		
		if (mLanguage != null) {
			params.put(LANGUAGE, mLanguage);
		}
		
		if (mLocation != null) {
			Float latitude = Float.valueOf(Double.toString(mLocation.getLatitude()));
			Float longitude = Float.valueOf(Double.toString(mLocation.getLongitude()));
		
			params.put(LATITUDE, latitude.toString());
			params.put(LONGITUDE, longitude.toString());
		}
		
		if (mState != null) {
			params.put(STATE, mState);
		}
		
		if (mRegId != null) {
			params.put(REGISTRATION_ID, mRegId);
		}
	}

	public void fillAlphaPairList(List<Pair<String, String>> list_params
			, final boolean isRegister) {
				
		//if (mAppKey != null) {
		//	list_params.add(Pair.create(AIRBOP_KEY, mAppKey));
		//}
		
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
		
		if ((isRegister) && (mState != null)) {
			list_params.add(Pair.create(STATE, mState));
		}
		
		if (mRegId != null) {
			list_params.add(Pair.create(REGISTRATION_ID, mRegId));
		}
		
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
    
    public static String getCurrentTimestamp() {
    	
    	String timestamp = "";
    	Date date = new Date();
        if (date != null) {
        	SimpleDateFormat sdf = new SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.getDefault());
        	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        	timestamp = sdf.format(date);
		}
        return timestamp;
    }
    
    
    private String constructSignatureURI(
    		final String method
    		, final String url
    		, final String timestamp
    		, final String body) {
//    	/signature_string = request_uri + APP_KEY + timestamp + request.body + SECRET
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
    	uriBuilder.append(SECRET_KEY);
    	
    	return uriBuilder.toString();
    	
    }
    
    /*
    public void addHeadersToConnection(HttpURLConnection connection
    		, final String url
    		, final boolean isRegister) {
    	//if (connection == null){
    		//ERROR
    	//	return;
    	//}
    	List<String> list_headers = new ArrayList<String>();
    	List<String> list_params = new ArrayList<String>();
    	
    	//X-Headers
    	//Get the time stamp
    	mTimeStamp = getCurrentTimestamp();
    	list_headers.add(mTimeStamp);
    	
    	//Post Params
    	if (mAppKey != null) {
    		list_params.add(mAppKey);
		}
		
		if ((isRegister) && (mCountry != null)) {
			list_params.add(mCountry);
		}
		
		if ((isRegister) && (mLabel != null)) {
			list_params.add(mLabel);
		}
		
		if ((isRegister) && (mLanguage != null)) {
			list_params.add(mLanguage);
		}
		
		if ((isRegister) && (mLocation != null)) {
			Float latitude = Float.valueOf(Double.toString(mLocation.getLatitude()));
			Float longitude = Float.valueOf(Double.toString(mLocation.getLongitude()));
		
			list_params.add(latitude.toString());
			list_params.add(longitude.toString());
		}
		
		if ((isRegister) && (mState != null)) {
			list_params.add(mState);
		}
		
		if (mRegId != null) {
			list_params.add(mRegId);
		}
		
		String signature_uri = getRegisterURI(url
				, list_headers
				, list_params);
		
		Log.i(TAG, "sig URI = " + signature_uri);
    }
    */
    /*
    public String getRegisterURI( final String url
    		, List<String> list_headers
    		, List<String> list_params) {
    	StringBuilder uriBuilder = new StringBuilder();
    	uriBuilder.append(url);
    	
    	
    	//Alpha x-headers
    	for (String header : list_headers) {
    		uriBuilder.append(":");
    		uriBuilder.append(header);	
    	}

    	//Alpha post params
    	for (String post_param : list_params) {
    		uriBuilder.append(":");
    		uriBuilder.append(post_param);	
    	}
    	return uriBuilder.toString();
    }
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
    
    public String getBody(final boolean isRegister
						, final boolean asJSON) {
    	if (asJSON) {
    		return getBodyAsJSON(isRegister);
    	} else {
    		return getBodyAsUrlEncoded(isRegister);
    	}
    }
    
    
    
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
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    public void post(String url_endpoint
			, final boolean isRegister
			, final boolean asJSON)
            throws IOException {
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
		
		Log.v(TAG, "signature: '" + signature);
		Log.v(TAG, "signature_hash: '" + signature_hash);
        
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
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
