package com.huiwei.commonlib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;

public class AppVersionUpdate {
	public static final int NOT_UPDATE = 0;
	public static final int UPDATE_COMPLETE = 1;
	
	private ProgressDialog pBar;
	private Context mContext;
	private Handler mHandler;
	
	public void doNewVersionUpdate(final String version, final String url,
			String desc, Context context, Handler handler) {
		this.mContext = context;
		this.mHandler = handler;
		
		StringBuffer sb = new StringBuffer();
		sb.append(desc);
		Dialog dialog = new AlertDialog.Builder(mContext)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				.setPositiveButton("马上升级",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// handleLoading.removeCallbacks(runableLoading);
								pBar = new ProgressDialog(mContext);
								pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								pBar.setTitle("正在下载");
								pBar.setMessage("请稍候…");
								downFile(url, version);
							}
						})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								mHandler.sendEmptyMessage(NOT_UPDATE);
							}
						}).create();
		dialog.show();
	}
	
	void downFile(final String url, final String verson) {
		final String apkName = "RoomReservation_" + verson + ".apk";
		pBar.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					pBar.setMax((int) length);
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(
								Environment.getExternalStorageDirectory(),
								apkName);
						fileOutputStream = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							if (length > 0) {
							}
							pBar.setProgress(count);
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down(verson);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private Handler updateHandler = new Handler();

	void down(final String verson) {
		updateHandler.post(new Runnable() {
			public void run() {
				pBar.cancel();
				update(verson);
			}
		});
	}

	void update(String verson) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		final String apkName = "RoomReservation_" + verson + ".apk";
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), apkName)),
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
		mHandler.sendEmptyMessage(UPDATE_COMPLETE);
	}
}
