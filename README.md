airbop-client
=============

This article describes how to use the sample project provided by AirBop, as well as a summary of its contents. The project allows you to easily build a working app and see the result of the messages sent using AirBop’s service. You’re welcome to use any of its helper functions or classes in your own app.

While it’s not required for this sample, you can read a generalized overview of using AirBop in your Android app in the article [Using AirBop in your Android App](http://www.airbop.com)

If you haven’t already, you’ll first want to read the article [Getting Started with AirBop](http://www.airbop.com) which describes the steps you need to take to start using AirBop. The following items are required to work with AirBop’s servers whose values need to be defined before building the sample project code:

•	Your Project ID (from Google’s API console)
•	Your AirBop App Key

*Note:* GCM requires Android 2.2 or later. If you’re testing on an Android emulator, it must be set to Google API level 8 and must also have a Google account added to it (Android version 4.0.4 or later doesn’t require an account).

Get the AirBop Sample Project

AirBop’s sample project is available from the following Git repository: https://github.com/indigorose/airbop-client

<< FURTHER INSTRUCTIONS ABOUT CLONING/GETTING PROJECT CODE ?>>

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

Add your Project ID and AirBop App Key to the CommonUtilities.java file. You will be setting the PRROJECT_ID and AIRBOP_APP_KEY static string values by replacing the dummy value <<REPLACE_ME>> with the correct ID and Key values. These replacements are discussed later in this document.

###### Done

Once you’ve completed the required variable replacement you can build the project in the Package Explorer by selecting Run > Run As > Android Application.

## What’s Included in the AirBop Sample Project

Below is a brief summary of the java files included in AirBop’s sample project:

###### airbop-client/libs/gcm.jar

The Google Cloud Messaging for Android Library.

###### airbop-client/res/*

The various Android resources used in the sample project.

###### airbop-client /src/com/airbop/client/ AirBopActivity.java

A helper class that extends ‘android.app.Activity’ and provides a basic way to interact with AirBop and GCM. It can be moved into your application for easy re-use. All of the top level registration and un-registration code can be found in this class.

###### airbop-client /src/com/airbop/client/ AirBopRegisterTask.java

An AnsynTask that will call the ServerUtilities.register() function in a separate thread so that the UI thread is not held up. If you are going to use the AirBopActivity in your app, this class should be brought along as well.

###### airbop-client /src/com/airbop/client/ AirBopServerData.java

A simple helper data class used to pass data from the App to the ServerUtilities.register() function. <<MORE LATER>>

###### airbop-client/src/com/airbop/client/ CommonUtilities.java

This is a utility class that contains the constants for your Project ID and AirBop App Key as well as a helper method to display a message.

###### airbop-client/src/com/airbop/client/DemoActivity.java

This is the sample application’s main activity. 

###### airbop-client/src/com/airbop/client/Device.java

A utility class which can be used to create a UUID value that can be used for your AirBop device Id. The UUID will be stored in the app’s shared preferences and will not change until the app is uninstalled.

###### airbop-client/src/com/airbop/client/Installation.java

A utility class which can be used to create a UUID value that can be used for your AirBop device Id. The UUID will be stored in file in the App’s application file directory, and will not change until the app is uninstalled.

###### airbop-client/src/com/airbop/client/GCMIntentService.java

This is the intent service class with the required overridden callback methods called by the Broadcast Receiver.

###### airbop-client/src/com/airbop/client/ServerUtilities.java

A helper class containing static functions that will perform the registration and unregistration actions with the AirBop servers.

###### airbop-client/AndroidManifest.xml

The AirBop sample application’s Android manifest. Note the additional permissions, BroadcastReceiver, and Service references.

###### airbop-client/.*

The root folder for the AirBop client sample.

###### airbop-client/README

Some extra information about the AirBop client sample.

## Values Needing Replacement

There are two java string constants that need to be defined before you’re able to build the sample app and send messages to it through AirBop:

###### Step 1 – Open CommonUtilities.java

Open the file containing the “CommonUtilities” class located in:

    airbop-client/src/com/airbop/client/CommonUtilities.java

###### Step 2 – Enter your Project ID
Locate the string constant definition named “PROJECT_ID” at the beginning of the class, and replace the text <<REPLACE_ME>> with your own Project ID you got from Google’s API console .

Line containing value to replace:

    static final String PROJECT_ID = <<REPLACE_ME>>;

Replace “<<REPLACE_ME>>” with your Project ID, for example:

    static final String PROJECT_ID = "918193457137";


###### Step 3 – Enter your AirBop App Key
Locate the string constant definition named “AIRBOP_APP_KEY” at the beginning of the class, and replace the text <<REPLACE_ME>> with your AirBop App Key for the app that you previously added to your AirBop account.

Line containing value to replace:

    static final String AIRBOP_APP_KEY = <<REPLACE_ME>>;

Replace “<<REPLACE_ME>>” with your AirBop App Key, for example:

    static final String AIRBOP_APP_KEY = "3q158wj8-589d-5x94-a79y-8y1c04c3cb92";

## Ensure a Device is Connected

Before you build and run the app, you must ensure you have either a physical device, or an Android Virtual Device running and connected. For more information on how to run an Android emulator, see [Managing Virtual Devices](http://developer.android.com/tools/devices/index.html) in the Android developer documentation.

##  Build and Run the Sample App


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
7.	For this example, you can leave the rest of the settings as their default values.
8.	When you’re ready to send the message, click the “Send Message” button and agree to the confirmation.

## Go Read your Notification Message

Now that you’ve sent the message, you should now have a notification message waiting for you on your device in the notification area. Take a look!
