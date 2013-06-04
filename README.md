airbop-client
=============

This article describes how to use the sample project provided by AirBop, as well as a summary of its contents. The project allows you to easily build a working app and see the result of the messages sent using AirBop’s service. You’re welcome to use any of its helper functions or classes in your own app.

While it’s not required for this sample, you can read a generalized overview of using AirBop in your Android app in the article [Adding AirBop to Your App](http://airbop.com/tutorials/adding-airbop-to-your-app)

If you haven’t already, you’ll first want to read the article [Getting Started with AirBop](http://www.airbop.com) which describes the steps you need to take to start using AirBop. The following items are required to work with AirBop’s servers whose values need to be defined before building the sample project code:

* Your Google Project Number (from Google’s API console)
* Your AirBop App Key
* Your AirBop App Secret

*Note:* GCM requires Android 2.2 or later. If you’re testing on an Android emulator, it must be set to Google API level 8 and must also have a Google account added to it (Android version 4.0.4 or later doesn’t require an account).

## Get the AirBop Sample Project

AirBop’s sample project is available from the following Git repository: https://github.com/indigorose/airbop-client

You can either download the project as a zip file using the following link: https://github.com/indigorose/airbop-client/zipball/master

Or you can use git to clone the repository as described below:

###### Step 1

The first step is to setup git on your computer as described in the github documentation: https://help.github.com/articles/set-up-git

###### Step 2

The next step is to get sample client code; you can do this by either forking the airbop-client repo as per these instructions: https://help.github.com/articles/fork-a-repo

Or you can clone a local version of the repo using the following git command:

    $ git clone git@github.com:indigorose/airbop-client.git
    
Or:

    $ git clone https://github.com/indigorose/airbop-client.git

## Opening the AirBop Sample Project in Eclipse

You can view and work with the AirBop sample project in Eclipse by following the steps below:

###### Step 1

Start Eclipse and browse to the downloaded ‘airbop-client’ directory to create a new workspace. You can click on the Workbench button to bypass the Welcome dialog.

*Note:* You can also import the project into an existing Eclipse Workspace if you wish. 

###### Step 2

Select File > Import from the menu.

###### Step 3

On the Import dialog, select Android > Existing Android Code Into Workspace.

###### Step 4

On the Import Projects dialog, browse to the ‘airbop-client’ directory as your Root directory. Once you do this, it should show the AirBop demo project. Make sure the AirBop demo project is checked in the in the “Projects” list and click the Finish button.

###### Step 5

Add your Google Project Number and AirBop App Key to the CommonUtilities.java file. You will be setting the GOOGLE_PROJECT_NUMBER, AIRBOP_APP_KEY, and AIRBOP_APP_SECRET static string values by replacing the dummy value <<REPLACE_ME>> with the correct number, key, and secret values. These replacements are discussed later in this document.

###### Done

Once you’ve completed the required variable replacement you can build the project in the Package Explorer by selecting Run > Run As > Android Application.

## Detailed Installation and Compilation Example

If you are having troubles adding the project to eclipse, the following is a detailed exampled explaining how to get, compile, and install the AirBop-Client sample onto an Android device. This example is indented for Windows users however the main concepts can be translated onto other operating systems. This example assumes that you have a working knowledge of eclipse, the Android SDK, and git, as well as git-bash, eclipse, and the Android SDK properly installed and configured on your computer.

If you run into any issues, or need a more detailed explanation please see the next section. If everything worked you can skip ahead to: What’s Included in the AirBop Sample Project.

###### Step 1

Start up git-bash and then browse to the root of your `C:\` drive:

    cd c:

###### Step 2
    
Create a directory called: `airbop-test`

    $ mkdir airbop-test

###### Step 4
    
Move into that directory and clone the air-bop-client repository:

    $ cd airbop-test
    $ git clone git@github.com:indigorose/airbop-client.git
 
###### Step 5
    
Start up eclipse and select the following as your Workspace Directory

    C:\airbop-test\

###### Step 6    

Click on the Workbench button to bypass the Welcome dialog. Select `File > Import` from the menu. On the `Import` dialog, select `Android > Existing Android Code Into Workspace`.

###### Step 7   

For the root directory choose `C:\airbop-test\airbop-client\airbop-client-sample`. Ensure that the `airbop-client-sample` is selected in the `Projects` area and the click the `Finish` button.

###### Step 8   

Add your Google Project Number and AirBop App Key to the CommonUtilities.java file. You will be setting the GOOGLE_PROJECT_NUMBER, AIRBOP_APP_KEY, and AIRBOP_APP_SECRET static string values by replacing the dummy value <<REPLACE_ME>> with the correct number, key, and secret values.
    
###### Step 9

Before running the sample you will need to plug in an Android device in order properly test the sample. If you need to test on an emulator be sure to test one that has the Google API's properly installed. You will also have to update the target in the `project.properties` to: 

    # Project target.
    target=Google Inc.:Google APIs:13

###### Step 10 
Once your device has been connected or your target has been updated, you can proceed. From the menu select `Run > Run`. This will bring up the `Run As` dialog. Select `Android Application` and then press the `OK` button.

## What’s Included in the AirBop Sample Project

Below is a brief summary of the java files included in AirBop’s sample project:

###### [airbop-client-sample/libs/gcm.jar](https://github.com/indigorose/airbop-client/blob/master/airbop-client-sample/libs/gcm.jar)

The Google Cloud Messaging for Android Library.

###### [airbop-client-sample/res/*](https://github.com/indigorose/airbop-client/tree/master/airbop-client-sample/res)

The various Android resources used in the sample project.

###### [airbop-client-sample/src/com/airbop/client/AirBopActivity.java](https://github.com/indigorose/airbop-client/blob/master/airbop-client-sample/src/com/airbop/client/AirBopActivity.java)

A helper class that extends ‘android.app.Activity’ and provides a basic way to interact with AirBop and GCM. It can be moved into your application for easy re-use. All of the top level registration and un-registration code can be found in this class.

###### [airbop-client-sample/src/com/airbop/client/AirBopIntentService.java](https://github.com/indigorose/airbop-client/blob/master/airbop-client-sample/src/com/airbop/client/AirBopIntentService.java)

A simple [IntentService](http://developer.android.com/reference/android/app/IntentService.html) that will register or unregister (based on the `REG_TASK` intent extra) with the AirBop servers in a separate thread. It performs the same job as the AirBopRegisterTask class, but is useful if you wanted to perform the registration outside of an [Activity](http://developer.android.com/reference/android/app/Activity.html).

###### [airbop-client-sample/src/com/airbop/client/AirBopRegisterTask.java](https://github.com/indigorose/airbop-client/blob/master/airbop-client-sample/src/com/airbop/client/AirBopRegisterTask.java)

An [AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html) that will call the ServerUtilities.register() function in a separate thread so that the UI thread is not held up. If you are going to use the AirBopActivity in your app, this class should be brought along as well. 

If you want to perform your registration outside of an Activity, might want to consider using the AirBopIntentService class.

###### [airbop-client-sample/src/com/airbop/client/AirBopServerUtilities.java](https://github.com/indigorose/airbop-client/blob/master/airbop-client-sample/src/com/airbop/client/AirBopServerUtilities.java)

A simple helper data class used to store data, register and unregister with the AirBop servers. 

###### [airbop-client-sample/src/com/airbop/client/CommonUtilities.java](https://github.com/indigorose/airbop-client/blob/master/airbop-client-sample/src/com/airbop/client/CommonUtilities.java)

This is a utility class that contains the constants for your Google Project Number, AirBop App Key, and AirBop App Secret as well as a helper method to display a message.

###### [airbop-client-sample/src/com/airbop/client/DemoActivity.java](https://github.com/indigorose/airbop-client/blob/master/airbop-client-sample/src/com/airbop/client/DemoActivity.java)

This is the sample application’s main activity. 

###### [airbop-client-sample/src/com/airbop/client/GCMIntentService.java](https://github.com/indigorose/airbop-client/blob/master/airbop-client-sample/src/com/airbop/client/GCMIntentService.java)

This is the intent service class with the required overridden callback methods called by the Broadcast Receiver.

###### [airbop-client-sample/AndroidManifest.xml](https://github.com/indigorose/airbop-client/blob/master/airbop-client-sample/AndroidManifest.xml)

The AirBop sample application’s Android manifest. Note the additional permissions, BroadcastReceiver, and Service references.

###### [airbop-client-sample/.*](https://github.com/indigorose/airbop-client/tree/master/airbop-client-sample)

The root folder for the AirBop client sample.

###### [airbop-client-sample/README.md](https://github.com/indigorose/airbop-client/blob/master/airbop-client-sample/README.md)

Simple readme for github, contains information from this article.

## Values Needing Replacement

There are two java string constants that need to be defined before you’re able to build the sample app and send messages to it through AirBop:

###### Step 1 – Open CommonUtilities.java

Open the file containing the “CommonUtilities” class located in:

    airbop-client-sample/src/com/airbop/client/CommonUtilities.java

###### Step 2 – Enter your Google Project Number
Locate the string constant definition named "GOOGLE_PROJECT_NUMBER" at the beginning of the class, and replace the text `<<REPLACE_ME>>` with your own Google Project Number you got from Google’s API console .

Line containing value to replace:

    static final String GOOGLE_PROJECT_NUMBER = <<REPLACE_ME>>;

Replace `<<REPLACE_ME>>` with your Google Project Number, for example:

    static final String GOOGLE_PROJECT_NUMBER = "918193457137";


###### Step 3 – Enter your AirBop App Key
Locate the string constant definition named "AIRBOP_APP_KEY" at the beginning of the class, and replace the text `<<REPLACE_ME>>` with your AirBop App Key for the app that you previously added to your AirBop account. This value can be found on your app's "Info" and "Edit" tabs.

Line containing value to replace:

    static final String AIRBOP_APP_KEY = <<REPLACE_ME>>;

Replace `<<REPLACE_ME>>` with your AirBop App Key, for example:

    static final String AIRBOP_APP_KEY = "3q158wj8-589d-5x94-a79y-8y1c04c3cb92";
    
###### Step 4 – Enter your AirBop App Secret
Locate the string constant definition named "AIRBOP_APP_SECRET" near the beginning of the class, and replace the text `<<REPLACE_ME>>` with your AirBop App Secret for the app that you previously added to your AirBop account. This value can be found on your app's "Info" and "Edit" tabs.

Line containing value to replace:

    static final String AIRBOP_APP_SECRET = <<REPLACE_ME>>;

Replace `<<REPLACE_ME>>` with your AirBop App Secret, for example:

    static final String AIRBOP_APP_SECRET = "3c4643055d38b7da8c175afe6288bThisIsNotARealSecret";

## Optional Values For Replacement

The optional values, like the required replacements, are located in the `CommonUtilities.java` class.

###### USE_LOCATION

Whether or not the client will collect location information and then pass that information on to the server. This value is set to false by default. If this value is set to true, you will need to uncomment the location permissions in the manifest:

    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/> 
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/> 
	
###### USE_SERVICE

Controls whether or not the client will use `AirBopIntentService.java` or `AirBopRegisterTask.java` when registering with the AirBop servers. If `USE_SERVICE` is `true` then the `AirBopIntentService.java` [IntentService]( http://developer.android.com/reference/android/app/IntentService.html) will be used, and if `USE_SERVICE` is `false` then the `AirBopRegisterTask.java` [AsyncTask]( http://developer.android.com/reference/android/os/AsyncTask.html) will be used.

This gives you the option to test both approaches easily. The approach you will use in your final app depends on your specific requirements. You generally won't want to switch between the two methods in your app but changing this value will allow you test the two different sample implementations easily.

## Required Headers

When registering or unregistering with the AirBop servers the following headers are required:

* x-app-key
* x-timestamp
* x-signature
* Content-Type

They are defined as follows:

###### x-timestamp
The timestamp of the message. Sent as the number of seconds since the Epoch, January 1, 1970 00:00 UTC.

###### x-app-key
Your AirBop App Key, which is created automatically by AirBop when you create a new app. It is shown on your app's "Info" and "Edit" tabs.

###### x-signature
An SHA-256 hash constructed exactly as follows:

    "POST" + request_uri + AIRBOP_APP_KEY + timestamp + request.body + AIRBOP_APP_SECRET

An example implementation of this can be found in the `AirBopServerUtilities.java` class. Specifically the `constructSignatureURI` and `computeHash` functions.

###### Content-Type
This header parameter controls which format the AirBop servers will expect the body to be in (JSON or form url encoded). There are two options:

    application/json
    
Or:

    application/x-www-form-urlencoded
    
The recommended format is JSON.   
    
## What is sent to the AirBop Servers

When you run the AirBop client application the following information can be sent to the server as part of the post parameters during the registration process. The request body is sent in the JSON format. Examples of how to construct the request boy and post to the AirBop servers can be found in the `AirBopServerUtilities.java` class.

The content type passed to the AirBop servers controls the format the body will be expected in. The preferred method is JSON: `application/json` but form URL encoded will also be accepted: `application/x-www-form-urlencoded`.

###### country (optional)
The [ISO 3166-1 alpha-2 country code](http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2#Officially_assigned_code_elements). This must be exactly two characters or it will be ignored.

###### label (optional)

A 50 character string that can be used to 'tag' or 'group' devices. You will be able to target devices based on this value when you send your message from the AirBop servers.

For example if you wanted to label devices registrations from tablets vs. phones you could label the phones: `phone` and the tablets: `tablet`, or `standard` and `HD`. Then you would be able to target each group separately.

###### lat (optional)
The latitude value of the device. Stored as a float and accurate to one city block.

###### long (optional)
The longitude value of the device. Stored as a float and accurate to one city block.

###### lang (optional)
The [ISO639-1 language code](http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes). Use:
    Locale.getDefault().getLanguage();
Only the first two characters of the locale will be stored.
 
###### reg
The registration ID received from the GCM servers. Described by in the [GCM Architectural Overview](http://developer.android.com/guide/google/gcm/gcm.html). You can get load this value from the GCM jar using the following: 
    GCMRegistrar.getRegistrationId(appContext); 

###### state (optional)
The two character state or province code from the [USA](http://en.wikipedia.org/wiki/List_of_U.S._state_abbreviations#PUSPS) or [Canada](http://en.wikipedia.org/wiki/Canadian_subnational_postal_abbreviations). This must be exactly two characters or it will be ignored.

If the location data is not passed the server will attempt to fill in the data by performing an IP lookup on the device that posted the data. The location data that the AirBop server get's will be quite accurate (99.5% at a country level and 90% at a state level) so you will only need to provide your own data if you need greater accuracy.

Here is an example of a JSON  registration body as generated by the [AirBop-Client sample]( https://github.com/indigorose/airbop-client):

    {
    	"country":"CA",
    	"label":"AirBop Sample",
     	"lang":"en",
    	"lat":"49.897884",
    	"long":"-97.1351",
    	"reg":"XXXXXXXXXXXXXXXXXX",
    	"state":"MB"
    }
    
The label can, and should, be adjusted to fit the needs of your project as explained above. e.g.:

    	"label":"paid",
    	
or:

    	"label":"free",
    	

###### Unregistering

When you are unregistering only the `reg` parameter is needed.

###### Re-Registering Or Updating With the AirBop Servers

After you have registered with the AirBop servers you can update your device's information by registering with the AirBop servers again. If you use the same registration Id (provided by GCM) AirBop will update the device’s information with the newly posted values. 

For example if the device has it’s default language set to English the first time it registers, and French the second time it registers. The language for the device will be stored as French after the second registration.

By default the sample client will attempt to re-register with the AirBop servers every 21 days. This is the recommended time span and is controlled by the `AIRBOP_DEFAULT_ON_SERVER_LIFESPAN_MS` value in the `AirBopActivity` class. The 21 day lifespan is passed to the GCM library using the following code:

    GCMRegistrar.setRegisterOnServerLifespan(this, AIRBOP_DEFAULT_ON_SERVER_LIFESPAN_MS);
    
This setting controls when the value returned by: `[GCMRegistrar.isRegisteredOnServer()] `(http://developer.android.com/guide/google/gcm/client-javadoc/com/google/android/gcm/GCMRegistrar.html#isRegisteredOnServer%28Context%29)` expires and will be set back to false, causing the client to re-register with AirBop.

### AirBop Response Codes

The following HTTP response code can be returned by the AirBop servers. In general any 400 errors, except for 403, mean that you have an error in your code you should fix it before attempting to register again.

###### 200 OK
* Everything is fine. The device was registered, unregegistered, and/or updated.

###### 400 Bad Request
* Bad request: missing headers.
* Bad Request: Missing parameter(s).

###### 401 Unauthorized
* Authentication failed.
* Signature is invalid.
* Request is outside the required time window of 4 minutes.

###### 403 Forbidden
* Forbidden: Plan device limit reached.
* This is the only 4xx error that should ever be retried without altering the client code (maybe next app launch). Once you upgrade your plan, it will go away.

###### 422 Unprocessable Entity
* This means you send some bad data to our server, that doesn't fit our specs.
* If your JSON was invalid, you'll get back {"error":"Invalid JSON"}.

###### 5xx
* A transient server error. Try again later.

## Ensure a Device is Connected

Before you build and run the app, you must ensure you have either a physical device, or an Android Virtual Device running and connected. For more information on how to run an Android emulator, see [Managing Virtual Devices](http://developer.android.com/tools/devices/index.html) in the Android developer documentation.

## Build and Run the Sample App

You’re now ready to build the sample app and install it on the connected device. Select “Run > Run As > Android Application” from the Eclipse menu. When the app is run on the device, it will register the device on AirBop’s servers which will allow messages to be sent to it from your AirBop account.

## Confirm the Device Registered Successfully

Login to your AirBop account located at http://www.airbop.com . The AirBop dashboard will show a list of all of the apps added to your account. If your device successfully registered with the AirBop server, you should see a ‘1’ listed next to the app under “Registered Devices”. You can also find this information on the “Info” tab when clicking the app name link.

## Send a Test Message Through AirBop

While logged into your AirBop account, perform the following steps starting from the AirBop dashboard:

1.	Click the app name link to view its available settings.
2.	Click the Messages tab.
3.	Click the “Send New Message” button.
4.	You should now see the various options available in a Message Details area. Enter a title for your message.
5.	Select “Text” as the mode for this example.
6.	Enter the message body you want shown in the notification message.
7.  Enter the Target URL that you want the user to jump to when they click on the notification.
8.	For this example, you can leave the rest of the settings as their default values.
9.	When you’re ready to send the message, click the “Send Message” button and agree to the confirmation.

## Go Read your Notification Message

Now that you’ve sent the message, you should now have a notification message waiting for you on your device in the notification area. Take a look!
