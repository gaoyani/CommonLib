package com.huiwei.commonlib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.util.ArrayList;

import android.net.wifi.WifiConfiguration;

public class StaticIPAddressAPI {
	// ******************************************************************************/
	// 以下代码为android3.0及以上系统设置静态ip地址的方法
	/* 设置ip地址类型 assign：STATIC/DHCP 静态/动态 */
	public static void setIpAssignment(String assign, WifiConfiguration wifiConf)
			throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		setEnumField(wifiConf, assign, "ipAssignment");
	}

	/* 设置ip地址 */
	@SuppressWarnings("unchecked")
	public static void setIpAddress(InetAddress addr, int prefixLength,
			WifiConfiguration wifiConf) throws SecurityException,
			IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, NoSuchMethodException,
			ClassNotFoundException, InstantiationException,
			InvocationTargetException {
		Object linkProperties = getField(wifiConf, "linkProperties");
		if (linkProperties == null)
			return;

		Class<?> laClass = Class.forName("android.net.LinkAddress");
		Constructor<?> laConstructor = laClass.getConstructor(new Class[] {
				InetAddress.class, int.class });
		Object linkAddress = laConstructor.newInstance(addr, prefixLength);
		ArrayList<Object> mLinkAddresses = (ArrayList<Object>) getDeclaredField(
				linkProperties, "mLinkAddresses");
		mLinkAddresses.clear();
		mLinkAddresses.add(linkAddress);
	}

	/* 设置网关 */
	@SuppressWarnings("unchecked")
	public static void setGateway(InetAddress gateway,
			WifiConfiguration wifiConf) throws SecurityException,
			IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, ClassNotFoundException,
			NoSuchMethodException, InstantiationException,
			InvocationTargetException {
		Object linkProperties = getField(wifiConf, "linkProperties");
		if (linkProperties == null)
			return;

		if (android.os.Build.VERSION.SDK_INT >= 14) {
			// android4.x版本
			Class<?> routeInfoClass = Class.forName("android.net.RouteInfo");
			Constructor<?> routeInfoConstructor = routeInfoClass
					.getConstructor(new Class[] { InetAddress.class });
			Object routeInfo = routeInfoConstructor.newInstance(gateway);
			ArrayList<Object> mRoutes = (ArrayList<Object>) getDeclaredField(
					linkProperties, "mRoutes");
			mRoutes.clear();
			mRoutes.add(routeInfo);
		} else {
			// android3.x版本
			ArrayList<InetAddress> mGateways = (ArrayList<InetAddress>) getDeclaredField(
					linkProperties, "mGateways");
			mGateways.clear();
			mGateways.add(gateway);
		}
	}

	/* 设置域名解析服务器 */
	@SuppressWarnings("unchecked")
	public static void setDNS(InetAddress dns, WifiConfiguration wifiConf)
			throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		Object linkProperties = getField(wifiConf, "linkProperties");
		if (linkProperties == null)
			return;

		ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(
				linkProperties, "mDnses");
		mDnses.clear();
		// 清除原有DNS设置（假设只想增加，不想清除，词句可省略）
		mDnses.add(dns);
		// 增加新的DNS
	}

	public static Object getField(Object obj, String name)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getField(name);
		Object out = f.get(obj);
		return out;
	}

	public static Object getDeclaredField(Object obj, String name)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getDeclaredField(name);
		f.setAccessible(true);
		Object out = f.get(obj);
		return out;
	}

	@SuppressWarnings("unchecked")
	public static void setEnumField(Object obj, String value, String name)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field f = obj.getClass().getField(name);
		f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
	}
	// ***以上是android3.x以上设置静态ip地址的方法*********************************************************************************
}
