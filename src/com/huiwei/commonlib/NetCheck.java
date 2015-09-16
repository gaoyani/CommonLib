package com.huiwei.commonlib;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

public class NetCheck {
	
	public static int CANCEL = 0;
	public static int SETTING = 1;

	public static boolean checkNet(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public static boolean checkWifi(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) 
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return wifi.isConnected();
	}

	public static void settingWifiNet(final Activity context, final Handler handler) {

		Builder ab = new Builder(context);
		ab.setTitle("网络连接");
		ab.setMessage("手机没有连接wifi网络");
		ab.setNegativeButton("取消",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						if (handler != null) {
							handler.sendEmptyMessage(CANCEL);
						}
						
					}
				});

		ab.setPositiveButton("设置",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						context.startActivityForResult(
								new Intent(
										android.provider.Settings.ACTION_WIFI_SETTINGS),
										SETTING);
						if (handler != null) {
							handler.sendEmptyMessage(SETTING);
						}
					}
				});
		ab.create().show();

	}

}
