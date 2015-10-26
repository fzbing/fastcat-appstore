package com.fastspider.fastcat.cache;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * �첽�̼߳���ͼƬ������ ʹ��˵���� BitmapManager bmpManager; bmpManager = new
 * BitmapManager(BitmapFactory.decodeResource(context.getResources(),
 * R.drawable.loading)); bmpManager.loadBitmap(imageURL, imageView);
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-6-25
 */
@SuppressLint("HandlerLeak")
public class BitmapManager {

	private static HashMap<String, SoftReference<Bitmap>> cache;
	private static ExecutorService pool;
	private static Map<ImageView, String> imageViews;
	private Bitmap defaultBmp;

	static {
		cache = new HashMap<String, SoftReference<Bitmap>>();
		pool = Executors.newFixedThreadPool(20); // �̶��̳߳�
		imageViews = Collections
				.synchronizedMap(new WeakHashMap<ImageView, String>());
	}

	public BitmapManager() {
	}

	public BitmapManager(Bitmap def) {
		this.defaultBmp = def;
	}

	/**
	 * ����Ĭ��ͼƬ
	 * 
	 * @param bmp
	 */
	public void setDefaultBmp(Bitmap bmp) {
		defaultBmp = bmp;
	}

	/**
	 * ����ͼƬ
	 * 
	 * @param url
	 * @param imageView
	 */
	public void loadBitmap(String url, ImageView imageView) {
		loadBitmap(url, imageView, this.defaultBmp, 0, 0);
	}

	/**
	 * ����ͼƬ-�����ü���ʧ�ܺ���ʾ��Ĭ��ͼƬ
	 * 
	 * @param url
	 * @param imageView
	 * @param defaultBmp
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp) {
		loadBitmap(url, imageView, defaultBmp, 0, 0);
	}

	/**
	 * ����ͼƬ-��ָ����ʾͼƬ�ĸ߿�
	 * 
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void loadBitmap(String url, ImageView imageView, Bitmap defaultBmp,
			int width, int height) {
		imageViews.put(imageView, url);
		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap != null) {
			// ��ʾ����ͼƬ
			imageView.setImageBitmap(bitmap);
		} else {
			// ����SD���е�ͼƬ����
			String filename = FileUtils.getFileName(url);
			String filepath = imageView.getContext().getFilesDir()
					+ File.separator + filename;
			File file = new File(filepath);
			if (file.exists()) {
				// ��ʾSD���е�ͼƬ����
				Bitmap bmp = ImageUtils.getBitmap(imageView.getContext(),
						filename);
				imageView.setImageBitmap(bmp);
			} else {
				// �̼߳�������ͼƬ
				imageView.setImageBitmap(defaultBmp);
				queueJob(url, imageView, width, height);
			}
		}
	}

	/**
	 * �ӻ����л�ȡͼƬ
	 * 
	 * @param url
	 */
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = null;
		if (cache.containsKey(url)) {
			bitmap = cache.get(url).get();
		}
		return bitmap;
	}

	/**
	 * �������м���ͼƬ
	 * 
	 * @param url
	 * @param imageView
	 * @param width
	 * @param height
	 */
	public void queueJob(final String url, final ImageView imageView,
			final int width, final int height) {
		/* Create handler in UI thread. */
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				String tag = imageViews.get(imageView);
				if (tag != null && tag.equals(url)) {
					if (msg.obj != null) {
						imageView.setImageBitmap((Bitmap) msg.obj);
						try {
							// ��SD����д��ͼƬ����
							ImageUtils.saveImage(imageView.getContext(),
									FileUtils.getFileName(url),
									(Bitmap) msg.obj);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};

//		pool.execute(new Runnable() {
//			public void run() {
//				Message message = Message.obtain();
//				message.obj = downloadBitmap(url, width, height);
//				handler.sendMessage(message);
//			}
//		});
	}

	/**
	 * ����ͼƬ-��ָ����ʾͼƬ�ĸ߿�
	 * 
	 * @param url
	 * @param width
	 * @param height
	 */
//	private Bitmap downloadBitmap(String url, int width, int height) {
//		Bitmap bitmap = null;
//		try {
//			// http����ͼƬ
//			bitmap = ApiClient.getNetBitmap(url);
//			if (width > 0 && height > 0) {
//				// ָ����ʾͼƬ�ĸ߿�
//				bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
//			}
//			// ���뻺��
//			cache.put(url, new SoftReference<Bitmap>(bitmap));
//		} catch (AppException e) {
//			e.printStackTrace();
//		}
//		return bitmap;
//	}
}