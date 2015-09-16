package com.huiwei.commonlib;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class CommonFunction {
	
	 public static int dip2px(Context context, float dpValue) {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (dpValue * scale + 0.5f);  
	    }  
	
	public static int px2sp(Context context, float pxValue) { 
		 final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
		 return (int) (pxValue / fontScale + 0.5f); 
	} 
	
	public static int sp2px(Context context, float spValue) { 
		 final float fontScale = context.getResources().getDisplayMetrics().scaledDensity; 
		 return (int) (spValue * fontScale + 0.5f); 
	} 
	
	public static int HexadecimalStrToColor(String strColor) {
		return Color.rgb(  
	            Integer.valueOf(strColor.substring(0, 2), 16),  
	            Integer.valueOf(strColor.substring(2, 4), 16),  
	            Integer.valueOf(strColor.substring(4, 6), 16));
	}

	
	public static boolean isURLAddress(String src) {
		try {
		     URL url = new URL(src);
		} catch (MalformedURLException e) {
		     return false;
		}
		
		return true;
	}
	
	public static String getVersionName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo;
		String version = "";
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			version = packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return version;
	}
	
	public static String getLocalMacAddress(Context context) { 
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
        WifiInfo info = wifi.getConnectionInfo(); 
        return info.getMacAddress();
   }  
	
	public static String getSSID(Context contex) { 
		WifiManager wifiManager = (WifiManager)contex.getSystemService(Context.WIFI_SERVICE);  
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();  
		if (wifiInfo.getSSID() == null) {
			return "";
		}
		
		return wifiInfo.getSSID().replace("\"", "");  
   } 
	
	public static String getGetway(Context contex) { 
		WifiManager wifiManager = (WifiManager)contex.getSystemService(Context.WIFI_SERVICE);  
		DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();  
		return Formatter.formatIpAddress(dhcpInfo.gateway);  
   }  
	
	public static boolean checkAppVersion(String localVersion, String urlVersion) {
		String urlVersions[] = urlVersion.split("\\.");
		String localVersions[] = localVersion.split("\\.");
		
		boolean updateVersion = false;
		if (urlVersions.length != localVersions.length) {
			updateVersion = true;
		} else {
			for (int i = 0; i < localVersions.length; i++) {
				if (Integer.parseInt(urlVersions[i]) > Integer
						.parseInt(localVersions[i])) {
					updateVersion = true;
					break;
				} else if (Integer.parseInt(urlVersions[i]) == Integer
						.parseInt(localVersions[i])) {
					continue;
				} else {
					break;
				}
			}
		}
		
		return updateVersion;
	}
	
	public static boolean checkGPS(Context context) {
		LocationManager locationManager = (LocationManager)context.
				getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public static void settingGPS(final Activity context, Handler handler) {
		if (!checkGPS(context)) {
			Builder ab = new Builder(context);
			ab.setTitle("打开GPS");
			ab.setMessage("为了更精确的定位，请打开手机GPS");
			ab.setNegativeButton("跳过",
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

			ab.setPositiveButton("设置",
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							Intent intent = new Intent();
					        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					        context.startActivity(intent);
						}
					});
			ab.create().show();
		}
	}
	
	/**
	 * 设置手机飞行模式
	 */
	public static void setAirplaneModeOn(Context context,boolean enabling) {
		Settings.System.putInt(context.getContentResolver(),
                          Settings.System.AIRPLANE_MODE_ON,enabling ? 1 : 0);
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", enabling);
		context.sendBroadcast(intent);
	}
	/**
	 * 判断手机是否是飞行模式
	 */
	public static boolean getAirplaneMode(Context context){
		int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),
                           Settings.System.AIRPLANE_MODE_ON, 0) ;
		return (isAirplaneMode == 1)?true:false;
	}
	
	public static Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();	
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),		
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));	
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());	
		view.setDrawingCacheEnabled(true);	
		Bitmap bitmap = view.getDrawingCache(true);	
		return bitmap;
	}
	
	public static boolean isBackground(Context context) {  
	    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
	    for (RunningAppProcessInfo appProcess : appProcesses) {
	         if (appProcess.processName.equals(context.getPackageName())) {
	                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
	                          Log.i("后台", appProcess.processName);
	                          return true;
	                }else{
	                          Log.i("前台", appProcess.processName);
	                          return false;
	                }
	           }
	    }
	    return false;
	}
}
