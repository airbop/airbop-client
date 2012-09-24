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

import static com.airbop.client.CommonUtilities.AIRBOP_APP_KEY;

import java.lang.ref.WeakReference;

import com.google.android.gcm.GCMRegistrar;

import android.content.Context;
import android.os.AsyncTask;

public class AirBopRegisterTask extends AsyncTask<Void, Void, Void> {
	
	private WeakReference<RegTaskCompleteListener> mListener;
	private Context mAppContext = null; 
	private AirBopServerData mServerData = null;
	private String mRegId;
	
	public AirBopRegisterTask(RegTaskCompleteListener listener
			, final Context appContext
			, final String redId
			, AirBopServerData server_data) {
		
			mAppContext  = appContext;
			mRegId = redId;
			mServerData = server_data;
			mListener = new WeakReference<RegTaskCompleteListener>(listener);
		}
	
	@Override
	protected Void doInBackground(Void... params) {
		boolean registered = ServerUtilities.register(mAppContext
            		, mRegId
            		, AIRBOP_APP_KEY
            		, mServerData);
	    // At this point all attempts to register with the AirBop
	    // server failed, so we need to unregister the device
	    // from GCM - the app will try to register again when
	    // it is restarted. Note that GCM will send an
	    // unregistered callback upon completion, but
	    // GCMIntentService.onUnregistered() will ignore it.
	    if (!registered) {
	        GCMRegistrar.unregister(mAppContext);
	    }
		
	    return null;
	}
	
	protected void onPostExecute(Void result) {
		 
		if (mListener != null) {
			RegTaskCompleteListener listener = mListener.get();
			if (listener != null) {
				listener.onTaskComplete();
			}
		}
	}
	/**
	* Defines an interface for a callback that will be called
	* when the task is done
	*/
	public interface RegTaskCompleteListener {
		public void onTaskComplete();
		
	}
}
