/*
 * ��¼app��Ϣ��ģ��
 */

package com.huiwei.commonlib;

import android.R.bool;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences
{
	public static String GetString(Context context, String Key)
	{
		SharedPreferences mPerferences = null;
		mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		return mPerferences.getString(Key, "");
	}
	
	public static boolean GetBoolean(Context context, String Key, boolean ldefaultVaule)
	{
		SharedPreferences mPerferences = null;
		mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		return mPerferences.getBoolean(Key, ldefaultVaule);
	}
	
	public static int GetInt(Context context, String Key)
	{
		SharedPreferences mPerferences = null;
		mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		
		return mPerferences.getInt(Key, 0);
	}
	
	public static void SetString(Context context, String Key, String Values)
	{
		SharedPreferences mPerferences = null;
		mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor Editor = mPerferences.edit();
		Editor.putString(Key, Values);
		Editor.commit();
	}
	
	public static void SetBoolean(Context context, String Key, boolean Values)
	{
		SharedPreferences mPerferences = null;
		mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor Editor = mPerferences.edit();
		Editor.putBoolean(Key, Values);
		Editor.commit();
	}
	
	public static void SetInt(Context context, String Key, int Values)
	{
		SharedPreferences mPerferences = null;
		mPerferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor Editor = mPerferences.edit();
		Editor.putInt(Key, Values);
		Editor.commit();
	}

}
