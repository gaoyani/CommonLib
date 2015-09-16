package com.huiwei.commonlib;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class SyncImageLoader {

	private Object lock = new Object();

	private boolean mAllowLoad = true;

	private boolean firstLoad = true;

	private int mStartLoadLimit = 0;

	private int mStopLoadLimit = 0;
	private int inSampleSize = 1;

	final Handler handler = new Handler();

	private Context mContext;

	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	RunInOtherThread runInOutherThread;

	public SyncImageLoader(Context context) {
		super();
		this.mContext = context;
		runInOutherThread = new RunInOtherThread();
		runInOutherThread.start();
	}

	public interface OnImageLoadListener {
		public void onImageLoad(Bitmap bitmap);

		public void onError();
	}

	public void setLoadLimit(int startLoadLimit, int stopLoadLimit) {
		if (startLoadLimit > stopLoadLimit) {
			return;
		}
		mStartLoadLimit = startLoadLimit;
		mStopLoadLimit = stopLoadLimit;
	}

	public void restore() {
		mAllowLoad = true;
		firstLoad = true;
	}

	public void lock() {
		mAllowLoad = false;
		firstLoad = false;
	}

	public void unlock() {
		mAllowLoad = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	public void loadImage(String imageUrl, OnImageLoadListener listener) {
		final OnImageLoadListener mListener = listener;
		final String mImageUrl = imageUrl;

		Handler handerTest = runInOutherThread.getHandler();
		if (handerTest == null) {
			handerTest = runInOutherThread.getHandler();
		}
		if (handerTest != null) {
			handerTest.post(new Runnable() {
				@Override
				public void run() {
					if (!mAllowLoad) {
						synchronized (lock) {
							try {
								lock.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					if (mAllowLoad) {
						loadImage1(mImageUrl, mListener);
					}
				}
			});
		}
	}

	private void loadImage1(final String mImageUrl,
			final OnImageLoadListener mListener) {

		if (imageCache.containsKey(mImageUrl)) {
			SoftReference<Bitmap> softReference = imageCache.get(mImageUrl);
			final Bitmap bmp = softReference.get();
			if (bmp != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (mAllowLoad) {
							mListener.onImageLoad(bmp);
						}
					}
				});
				return;
			} else {
				imageCache.remove(mImageUrl);
			}
		}
		
		final Bitmap bitmap = decodeBitmap(mImageUrl, handler);
		if (bitmap != null) {
			imageCache.put(mImageUrl, new SoftReference<Bitmap>(bitmap));

			handler.post(new Runnable() {
				@Override
				public void run() {
					if (mAllowLoad) {
						mListener.onImageLoad(bitmap);
					}
				}
			});
		}
	}
	
	private Bitmap decodeBitmap(String imageUrl, Handler handler) {
    	Bitmap bitmap = null;
		try {
			URL url = new URL(imageUrl);
			InputStream is = (InputStream) url.getContent();
			
			BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = inSampleSize;
            bitmap = BitmapFactory.decodeStream(is, null, opts);
            
		} catch (OutOfMemoryError e) {
			inSampleSize++;
			decodeBitmap(imageUrl, handler);
			e.printStackTrace();
    	} catch (MalformedURLException e) {
			try {
				FileInputStream fin = new FileInputStream(imageUrl);
				BitmapFactory.Options opts = new BitmapFactory.Options();
	            opts.inSampleSize = inSampleSize;
	    		bitmap = BitmapFactory.decodeStream(fin, null, opts); 
	    		
			} catch (FileNotFoundException e1) {
				Message msg = new Message();
				msg.obj = null;
	            handler.sendMessage(msg);
				e1.printStackTrace();
			}
    		 
    	} catch (IOException e) {
			Message msg = new Message();
			msg.obj = null;
            handler.sendMessage(msg);
            
			e.printStackTrace();
		}
		
		return bitmap;
    }

//	public static Drawable loadImageFromUrl(String url) throws IOException {
//		// DebugUtil.debug(url);
//		if (url == null || url.length() == 0)
//			return null;
//		File file = new File(url);
//		Bitmap bitmap = null;
//		Drawable drawable = null;
//		if (file.exists()) {
//			FileInputStream fis = new FileInputStream(file);
//			try {
//				bitmap = BitmapFactory.decodeStream(fis);
//			} catch (OutOfMemoryError e) {
//				// TODO: handle exception
//				BitmapFactory.Options options = new BitmapFactory.Options();
//				options.inSampleSize = 4;
//				options.inPreferredConfig = Config.RGB_565;
//				try {
//					bitmap = BitmapFactory.decodeStream(fis, null, options);
//				} catch (OutOfMemoryError e2) {
//					// TODO: handle exception
//					return null;
//				}
//			} finally {
//				if (fis != null) {
//					fis.close();
//					System.gc();
//				}
//			}
//			if (bitmap != null) {
//				drawable = new BitmapDrawable(bitmap);
//			}
//			return drawable;
//		} else {
//			File f = new File(HTTPUrl.ImageCacheFloder + "/" + MD5.getMD5(url));
//			if (f.exists()) {
//				FileInputStream fis = new FileInputStream(f);
//
//				try {
//					bitmap = BitmapFactory.decodeStream(fis);
//				} catch (OutOfMemoryError err) {
//					// TODO: handle exception
//					BitmapFactory.Options opt = new BitmapFactory.Options();
//					opt.inPreferredConfig = Bitmap.Config.RGB_565;
//					opt.inSampleSize = 4;
//					try {
//						bitmap = BitmapFactory.decodeStream(fis, null, opt);
//					} catch (OutOfMemoryError e) {
//						// TODO: handle exception
//						return null;
//					}
//				} finally {
//					if (fis != null) {
//						fis.close();
//						System.gc();
//					}
//				}
//				if (bitmap != null) {
//					drawable = new BitmapDrawable(bitmap);
//				}
//				return drawable;
//			}
//			URL m = new URL(url);
//			InputStream i = (InputStream) m.getContent();
//			DataInputStream in = new DataInputStream(i);
//			FileOutputStream out = new FileOutputStream(f);
//			byte[] buffer = new byte[1024];
//			int byteread = 0;
//			while ((byteread = in.read(buffer)) != -1) {
//				out.write(buffer, 0, byteread);
//			}
//			Drawable d = Drawable.createFromStream(i, "src");
//			in.close();
//			out.close();
//			i.close();
//			return loadImageFromUrl(url);
//		}
//	}

	public void threadQuit() {
		// TODO Auto-generated method stub
		if (runInOutherThread != null) {
			runInOutherThread.quit();
		}

	}
}
