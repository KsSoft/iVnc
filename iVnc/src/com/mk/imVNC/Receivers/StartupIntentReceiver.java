package com.mk.imVNC.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		try{
				
				if(!com.mk.imVNC.billingUtil.CheckTrial.shouldLaunch(context, com.mk.imVNC.billingUtil.CheckTrial.fullVersionUri)){
					com.mk.imVNC.billingUtil.CheckTrial.notifyTrialExpired(context);
				}
				
				
			}
			catch(Exception e)
			{
				Log.e("iPlay","StartupIntentReceiver Error");
			}	
	    
		}

}
