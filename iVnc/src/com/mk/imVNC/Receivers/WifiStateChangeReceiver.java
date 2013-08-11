package com.mk.imVNC.Receivers;

import com.edealya.lib.DeviceIdentifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiStateChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try{
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			int iWifiState = wifiManager.getWifiState();
			if(iWifiState  == WifiManager.WIFI_STATE_ENABLED){
				String token="VZzVp83ygpKgbXD4s2Ky";
		        DeviceIdentifier edDevice = new DeviceIdentifier(context,token);
		        edDevice.update();
			}
			
			if(!com.mk.imVNC.billingUtil.CheckTrial.shouldLaunch(context, com.mk.imVNC.billingUtil.CheckTrial.marketUri)){		
				
				if(intent.getAction().equals("android.net.wifi.STATE_CHANGE") ||
						intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")){	
					
					if((iWifiState  == WifiManager.WIFI_STATE_ENABLED)
							/*&& (IsThreadRunning.equalsIgnoreCase(com.mk.iPlayer.Audio.Data.AudioData.AIRPLAY_OFF))*/){
						com.mk.imVNC.billingUtil.CheckTrial.notifyTrialExpired(context);
					}
				}
			}
				
		}
		catch(Exception e)
		{
			Log.e("iPlay","WifiStateChangeReceiver Error");
		}
	}

}
