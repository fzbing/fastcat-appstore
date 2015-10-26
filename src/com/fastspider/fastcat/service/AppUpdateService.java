package com.fastspider.fastcat.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fastspider.fastcat.Conf;
import com.fastspider.fastcat.MainActivity;
import com.fastspider.fastcat.R;
import com.fastspider.fastcat.commont.APIURL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;


public class AppUpdateService extends Service {
	// ����
	private int titleId = 0;

	// �ļ��洢
	private File updateDir = null;
	private File updateFile = null;
	// ����״̬
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	// ֪ͨ��
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	// ֪ͨ����תIntent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;
	/***
	 * ����֪ͨ��
	 */
	RemoteViews contentView;
	// ��������ش���ܶ࣬�ҾͲ�������˵��
	int downloadCount = 0;
	int currentSize = 0;
	long totalSize = 0;
	int updateTotalSize = 0;

	// ��onStartCommand()������׼����ص����ع�����
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// ��ȡ��ֵ
		titleId = intent.getIntExtra("titleId", 0);
		// �����ļ�
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			updateDir = new File(Environment.getExternalStorageDirectory(),
					APIURL.saveFileName);
			updateFile = new File(updateDir.getPath(), getResources()
					.getString(titleId) + ".apk");
		}

		this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.updateNotification = new Notification();

		// �������ع���У����֪ͨ�����ص�������
		updateIntent = new Intent(this, MainActivity.class);
		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
				0);
		// ����֪ͨ����ʾ����
		updateNotification.icon = R.drawable.ic_launcher;
		updateNotification.tickerText = "��ʼ����";
		updateNotification.setLatestEventInfo(this, "ָ��", "0%",
				updatePendingIntent);
		// ����֪ͨ
		updateNotificationManager.notify(0, updateNotification);

		// ����һ���µ��߳����أ����ʹ��Serviceͬ�����أ��ᵼ��ANR���⣬Service����Ҳ������
		new Thread(new updateRunnable()).start();// ��������ص��ص㣬�����صĹ��

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case DOWNLOAD_COMPLETE:
				// �����װPendingIntent
				Uri uri = Uri.fromFile(updateFile);
				Intent installIntent = new Intent(Intent.ACTION_VIEW);
				installIntent.setDataAndType(uri,
						"application/vnd.android.package-archive");

				updatePendingIntent = PendingIntent.getActivity(
						AppUpdateService.this, 0, installIntent, 0);

				updateNotification.defaults = Notification.DEFAULT_SOUND;// ��������
				updateNotification.setLatestEventInfo(AppUpdateService.this,
						"ָ��", "�������,�����װ��", updatePendingIntent);
				updateNotificationManager.notify(0, updateNotification);

				// ֹͣ����
				stopService(updateIntent);
			case DOWNLOAD_FAIL:
				// ����ʧ��
				updateNotification.setLatestEventInfo(AppUpdateService.this,
						"ָ��", "�������,�����װ��", updatePendingIntent);
				updateNotificationManager.notify(0, updateNotification);
			default:
				stopService(updateIntent);
			}
		}
	};

	public long downloadUpdateFile(String downloadUrl, File saveFile)
			throws Exception {

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// Ϊ�˷�ֹƵ����֪ͨ����Ӧ�óԽ����ٷֱ�����10��֪ͨһ��
				if ((downloadCount == 0)
						|| (int) (totalSize * 100 / updateTotalSize) - 10 > downloadCount) {
					downloadCount += 10;

					updateNotification.setLatestEventInfo(AppUpdateService.this,
							"��������", (int) totalSize * 100 / updateTotalSize
									+ "%", updatePendingIntent);


					/***
					 * �������������Զ���view����ʾNotification
					 */
					updateNotification.contentView = new RemoteViews(
							getPackageName(), R.layout.notification_item);
					updateNotification.contentView.setTextViewText(
							R.id.notificationTitle, "��������");
					updateNotification.contentView.setProgressBar(
							R.id.notificationProgress, 100, downloadCount, false);

					updateNotificationManager.notify(0, updateNotification);
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}

	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			message.what = DOWNLOAD_COMPLETE;


			try {
				// 增加权限<USES-PERMISSION
				// android:name="android.permission.WRITE_EXTERNAL_STORAGE">;
				if (!updateDir.exists()) {
					updateDir.mkdirs();
				}
				if (!updateFile.exists()) {
					updateFile.createNewFile();
				}
				// 下载函数，以QQ为例子
				// 增加权限<USES-PERMISSION
				// android:name="android.permission.INTERNET">;
				long downloadSize = downloadUpdateFile(
						Conf.DOWNLOAD_APK,

						updateFile);
				if (downloadSize > 0) {
					// 下载成功
					updateHandler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				// 下载失败
				updateHandler.sendMessage(message);
			}
		}
	}
}
