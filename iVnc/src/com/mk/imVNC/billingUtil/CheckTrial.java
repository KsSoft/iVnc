package com.mk.imVNC.billingUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mk.imVNC.R;
import com.mk.imVNC.iVnc;

public class CheckTrial {
	
	public final static String marketLink = "market://details?id=com.mk.iVncLicence&c=apps";
	public final String appLink = "market://details?id=com.mk.imVNC&c=apps";
	//private final String licenseLink = "market://details?id=com.mk.iPlayAudioLicence&c=apps";
	public static final String fullVersionUri = "com.mk.iVncLicence";
	public static final String marketUri = "com.mk.imVNC";
	
	static private String StorageFileName = ".23Droid";
	static private Context appContext = null;
	final static long TrialSec = 4*86400; // 2 days 172800 sec 604800 for 7 days
	final static boolean isAmazonBuild = false;
	private static String OperationType = null;
	

	//Type of operation on inAppBilling
	public static final String PURCHASE_LICENSE = "purchase_license";
	public static final String CHECK_LICENSE = "check_license";
	
	//Shared preference Name
	public static final String IS_LICENSE_PURCHASED = "isLicensePurchase";
	private static SharedPreferences sp = null;
	private static boolean shouldFinishActivity = false;
	
	final private static String base64EncodedPublicKey =  "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm3gKl6eGFRvmErJ1ZHs30QJFAffP9CP5OI45YblyAWv9n1x2LzPKS+bQ5vfhcOMMtQXZyNBOermzqzmNipl/xLAhkY/iV/GanrMVTLVd2V+CWi+FDkE1QumO/9AsRiG/bluI4C/LCTwC21VNNrqJmlF+CaSjBEJKi712+igsn4com0MqQD2ucFGsfoB9/FMJ0Jgxya1wouAg8ZY/MYQuonNbfl/X2xt1f5Um882tLABVqc1qsq2OASNZyP3E44SLcyq7krBhbOus4YB+DAZ920HYYhTWwsJCKjBU18vipgtH9a+BlM5WxVOx+9314jdcPXKhlKraHBdglE/66o442QIDAQAB";
	// The helper object
    static IabHelper mHelper ;
    // Does the user have the premium upgrade?
    static boolean mIsPremium = false;
    static final String SKU_PREMIUM = "com.mk.ivnclicence";
    /**
     * android.test.purchased
     * android.test.canceled
     * android.test.refunded
     * android.test.item_unavailable
     */
 // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 131398;
	public static final int TRIALEXPIRED_NOTIFICATION_ID = 198301;
    static Activity calledActivity = null;
	
	
	
    public static Boolean isTrialExpired(Context context)
	{
		Boolean ret = true;
		appContext = context;
		
		long currentTimeMsec = System.currentTimeMillis();
		long startTimeMsec = getStartTimeStamp();
		long runningTime =  ((currentTimeMsec - startTimeMsec) / 1000);
		
		if(runningTime < TrialSec)
			ret = false;
		
		return ret;
	}
	
	public static String timeRemaining(Context context)
	{
		String ret = context.getString(R.string.Trial_expired);
		appContext = context;
		
		long currentTimeMsec = System.currentTimeMillis();
		long startTimeMsec = getStartTimeStamp();
		long runningTime =  ((currentTimeMsec - startTimeMsec) / 1000);
		
		if(runningTime < TrialSec)
		{
			long secsRemaining = TrialSec - runningTime ;
			
			int days = (int) (secsRemaining / 86400);
			int secsStill = (int) (secsRemaining - (days * 86400));
			int hours = (int) (secsStill / 3600);
			secsStill -= (hours * 3600);
			int minutes = (int) (secsStill / 60);
			int seconds = (int) (secsRemaining % 60);
			
			if(days > 0)
				ret = context.getString(R.string.Trial_expires_in) + " "+ days + " "+ context.getString(R.string.days) ;
			else if(hours > 0)
				ret = context.getString(R.string.Trial_expires_in) + " "+ hours + " "+ context.getString(R.string.hours) ;
			else if(minutes > 0)
				ret = context.getString(R.string.Trial_expires_in) + " "+ minutes + " "+ context.getString(R.string.minutes) ;
			else if(seconds > 0)
				ret = context.getString(R.string.Trial_expires_in) + " "+ seconds + " "+ context.getString(R.string.seconds) ;
		}
		else
		{
			ret = context.getString(R.string.Trial_expired);
		}
		
		return ret;
	}
	
	public static void saveStartTimeStamp(Context context) throws IOException
	{
		appContext = context;
		long currentTime = System.currentTimeMillis();
		String string = String.valueOf(currentTime);
		
		/* Write to internal storage */
		String IntRoot = appContext.getFilesDir().getAbsolutePath();
		File intFile = new File (IntRoot, StorageFileName);
		String ExtRoot = Environment.getExternalStorageDirectory().toString();
		File file = new File (ExtRoot, StorageFileName);
		/*Write to internal storage*/
		if (!intFile.exists() && !file.exists())
		{
		    try {
		           FileOutputStream out = new FileOutputStream(intFile);
		           out.write(string.getBytes());
		           out.close();	
		    } catch (Exception e) {
		           e.printStackTrace();
		    }
		}
		else if (file.exists() && !intFile.exists()) //App uninstalled... External available
		{
			try {
					string = String.valueOf(getExternalFileTime());   
					FileOutputStream out = new FileOutputStream(intFile);
					out.write(string.getBytes());
					out.close();	
		    } catch (Exception e) {
		           	e.printStackTrace();
		    }
		}
			
		
		/*Write to external storage*/
		if (!file.exists ()) 
		    try {
		    		FileOutputStream out = new FileOutputStream(file);
		    		out.write(string.getBytes());
		    		out.close();
		    		
		    } catch (Exception e) {
		           	e.printStackTrace();
		}
	}	
	
	private static long getStartTimeStamp()
	{
		long timestampFromStorage = getInternalFileTime();
		if(timestampFromStorage == 0)
			timestampFromStorage = getExternalFileTime();			

		return timestampFromStorage;
	}

	private static long getInternalFileTime() {
		long ret = 0;
		File file = new File(appContext.getFilesDir().getAbsolutePath(),StorageFileName);
		final StringBuffer storedString = new StringBuffer();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		    	storedString.append(line);
		    	//storedString.append('\n');
		    }
		    
		    String longString = storedString.toString();
		    ret = Long.parseLong(longString);
		}
		catch  (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	private static long getExternalFileTime() {
		long ret = 0;
		File file = new File(Environment.getExternalStorageDirectory(),StorageFileName);
		final StringBuffer storedString = new StringBuffer();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		    	storedString.append(line);
		    	//storedString.append('\n');
		    }
		    
		    String longString = storedString.toString();
		    ret = Long.parseLong(longString);
		}
		catch  (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}
	 public static Boolean shouldLaunch(Context context, String uri){
	    	if(isLicensePurchased(uri, context))
	    		return true;
	    	else if(!isTrialExpired(context))
	    		return true;
	    	else
	    		return false;
	    }
	
	public static boolean isLicensePurchased(String uri, Context context)
    {
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		boolean app_installed = false;
		if(!isAmazonBuild){
			if(inAppPurchased()){
				app_installed = true;
			}
			else{
				PackageManager pm = context.getPackageManager();
		        try
		        {
		        	 	PackageInfo pckgInfo = pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
		        		if(pckgInfo != null)
		        			app_installed = true;
		        		else
		        			app_installed = false;
		        }
		        catch (PackageManager.NameNotFoundException e)
		        {
		               app_installed = false;
		        }
			}
        }
        else{
        	app_installed = true;
        }
        return app_installed ;
    }
	
	private static boolean inAppPurchased() {
		boolean ret = sp.getBoolean(IS_LICENSE_PURCHASED, false);
		return ret;
	}

	public static String getVersion(Context mContext) {
		String Version = "Not Availabale";
		if(mContext != null){
			try {
	            String pkg = mContext.getPackageName();
	            Version = mContext.getPackageManager().getPackageInfo(pkg, 0).versionName;
	        } catch (NameNotFoundException e) {
	        	Version = "Not Availabale";
	        }
		}
		
		return Version;
	}

	public void feedbackPreference(boolean shouldCheck, final Context context) {
		/*feedbackIntent = new Intent(Intent.ACTION_VIEW);
		//feedbackIntent.setData(Uri.parse(appLink));
	   	 if( !isLicensePurchased(com.mk.iRemote.all.ControlActivity.licenseUri,context.getApplicationContext())){
	   		feedbackIntent.setData(Uri.parse(com.mk.iRemote.all.ControlActivity.appLink));
	   	 }
	   	 else{
	   		feedbackIntent.setData(Uri.parse(com.mk.iRemote.all.ControlActivity.marketLink));
	   	 }
		feedbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(feedbackIntent);
        
		/*Preference feedback = (Preference)findPreference("feedback");
		//feedback.setSummary(com.mk.iPlayerTrial.Utility.CheckTrial.timeRemaining(getApplicationContext()));
		feedback.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			 
            public boolean onPreferenceClick(Preference preference) {
            	context.startActivity(feedbackIntent);
            	//finish();
                return true;
            }          
		});*/
		
	}
	
	public static boolean startInAppBilling(Context context, final Activity self, String opType, boolean shouldFinish){
		
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(marketLink));
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		self.startActivity(i);
		self.finish();
		return true;
		/*
		// Create the helper, passing it our context and the public key to verify signatures with
		calledActivity = self;
		OperationType = opType;
		shouldFinishActivity = shouldFinish;
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		Log.d("iRemote License", "Creating IAB helper.");
        mHelper = new IabHelper(context, base64EncodedPublicKey);
        
        // enable debug logging (for a production application, you should set this to false).
        //mHelper.enableDebugLogging(false);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d("iRemote License", "Starting setup.");
        
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d("iRemote License", "Setup finished.");
                
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                	Log.d("iRemote License", "Problem setting up in-app billing: " + result);
                	// cant goto inApp billing... launch license app purchase
                	Intent i = new Intent(Intent.ACTION_VIEW, 
                    Uri.parse(marketLink));
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					self.startActivity(i);
					self.finish();             	
                	return;
                }

                // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d("iRemote License", "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
		return false;*/
	}
	
	public static void PurcahseLicense(Activity context){
		if(context != null){
			mHelper.launchPurchaseFlow(context, SKU_PREMIUM, RC_REQUEST, mPurchaseFinishedListener);
		}
		else{
			Log.d("iRemote License", "launchPurchaseFlow. Context null.");
		}
			
	}
	
	public static boolean receivedActivityResult(int requestCode, int resultCode, Intent data){
		return mHelper.handleActivityResult(requestCode, resultCode, data);
		
	}
	
	// Listener that's called when we finish querying the items we own
    static IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d("iRemote License", "Query inventory finished.");
            if (result.isFailure()) {
                //complain("Failed to query inventory: " + result);
            	Log.d("iRemote License", "Failed to query inventory: " + result);
            	notifyTrialExpired(null);
                return;
            }

            Log.d("iRemote License", "Query inventory was successful.");

            // Do we have the premium upgrade?
            mIsPremium = inventory.hasPurchase(SKU_PREMIUM);
            Log.d("iRemote License", "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
            if(!mIsPremium){
            	if(OperationType.equalsIgnoreCase(PURCHASE_LICENSE))
            		PurcahseLicense(calledActivity);
            }
            else{
            	StoreToSharedPreference(true);
            	String Title = "You have already purchased license";
                String Message = "Your iRemote license has been retrived. Please restart the iRemote.";
                CancelNotification(appContext, TRIALEXPIRED_NOTIFICATION_ID);
            	showDialogue(calledActivity , Title, Message, false);
            }
        }
    };
    
 // Callback for when a purchase is finished
    static IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d("iRemote License", "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
            	StoreToSharedPreference(false);
            	String Title = "Error purchasing license";
                String Message = "Purchase cancelled. Please try again.";
                notifyTrialExpired(null);
            	showDialogue(calledActivity , Title, Message, shouldFinishActivity);
            	Log.d("iRemote License", "Error purchasing: " + result);
                return;
            }

            Log.d("iRemote License", "Purchase successful.");
            
            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.d("iRemote License", "Purchase is premium upgrade. Congratulating user.");
                mIsPremium = true;
                StoreToSharedPreference(mIsPremium);
                String Title = "Congratulations!!!";
                String Message = "Licensed purchased sucessfully. Please restart iRemote.";
                CancelNotification(appContext, TRIALEXPIRED_NOTIFICATION_ID);
                showDialogue(calledActivity , Title, Message, true);
            }
        }
    };
    
    public static void destroyHelper() {
    	   if (mHelper != null) mHelper.dispose();
    	   mHelper = null;
    	}

	protected static void StoreToSharedPreference(boolean state) {
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(IS_LICENSE_PURCHASED,state);
		editor.commit();	
	}
	
	private static void showDialogue(final Context context, String Title, String Message, final boolean shouldFinish){
	    	AlertDialog.Builder ad = new AlertDialog.Builder(context);
	    	ad.setTitle(Title);
	    	ad.setIcon(R.drawable.market);
	    	ad.setOnCancelListener(new DialogInterface.OnCancelListener() {         
	       	    @Override
	       	    public void onCancel(DialogInterface dialog) {
	       	    	if(shouldFinish)
		        		  ((Activity) context).finish();
	       	    }
	       	});
	    	ad.setMessage(Message);
	    	ad.setPositiveButton("ok", new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog, int whichButton) {
	        	  if(shouldFinish)
	        		  ((Activity) context).finish();
	          }
	        }).show();
	}
	
	public static void notifyTrialExpired(Context context) {
		if(context != null )
			appContext = context;
		
		if(appContext != null)
		{
			//String logText = (String) getApplicationContext().getText(R.string.select_trial_expired_body);
			Notification notification = new Notification(R.drawable.notification_error_icon, appContext.getString(R.string.iPlay_Audio_Trial_Expired), System.currentTimeMillis());
			Intent notificationIntent = new Intent(appContext, iVnc.class);
			
			//notificationIntent.putExtra(SendBugReportUiActivity.CRASH_REPORT_TEXT, logText);
			
			PendingIntent contentIntent = PendingIntent.getActivity(appContext, 0, notificationIntent, 0);
	
			notification.setLatestEventInfo(appContext, 
					appContext.getString(R.string.iPlay_Audio_Trial_Expired), 
					appContext.getString(R.string.Please_purchase_iPlay_Audio_license_from_market),
					contentIntent);
			notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
			notification.flags |= Notification.FLAG_NO_CLEAR;
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			// notifying
			NotificationManager notificationManager = (NotificationManager)appContext.getSystemService(Context.NOTIFICATION_SERVICE);
			//cancel previous notification
			notificationManager.notify(TRIALEXPIRED_NOTIFICATION_ID, notification);
		}
	}
	
	@SuppressWarnings("static-access")
	public static void CancelNotification(Context context, int NotificationID) {
		if(context != null )
			appContext = context;
		
		if(appContext != null)
		{
			if (appContext.NOTIFICATION_SERVICE!=null) {
		        String ns = Context.NOTIFICATION_SERVICE;
		        NotificationManager nMgr = (NotificationManager) appContext.getSystemService(ns);
		        nMgr.cancel(NotificationID);
		    }
		}
	}

}
