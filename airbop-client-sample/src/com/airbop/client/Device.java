/*
 * Copyright 2012 Indigo Rose Corporation
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

import java.util.UUID;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Context;

public class Device {
    private static String sID = null;
    private static final String INSTALLATION = "INSTALLATION";
    private static final String PREFERENCES = "com.airbop.client.installation.id";
    
    public synchronized static String id(Context context) {
        if (sID == null) {     
        	sID = readInstallationPref(context);
        	if (sID == null) {
        		writeInstallationPref(context);
        		sID = readInstallationPref(context);
        	}
        }
        return sID;
    }

    private static String readInstallationPref(Context context){
    	final SharedPreferences prefs = getPreferences(context);
    	if(prefs != null) {
    		return prefs.getString(INSTALLATION, null);
    	}
    	return null;
    }
    
    private static void writeInstallationPref(Context context){
    	final SharedPreferences prefs = getPreferences(context);
    	if(prefs != null) {
    		UUID uuid = UUID.randomUUID();
    		Editor editor = prefs.edit();
            editor.putString(INSTALLATION, uuid.toString());
            editor.commit();
    	}
    }
    
    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }
}
