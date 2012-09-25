## AirBop Client Sample

Full details on the client can be found here: https://github.com/indigorose/airbop-client

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
