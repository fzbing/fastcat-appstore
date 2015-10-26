package com.fastspider.fastcat.activity;

import java.io.File;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.fastspider.fastcat.Conf;
import com.fastspider.fastcat.R;
import com.fastspider.fastcat.adapter.SettingAdapter;
import com.fastspider.fastcat.cache.ACache;
import com.fastspider.fastcat.dialog.SweetAlertDialog;
import com.fastspider.fastcat.lib.NetWorkUtil;
import com.fastspider.fastcat.lib.toast.Crouton;
import com.fastspider.fastcat.lib.toast.Style;
import com.fastspider.fastcat.service.AppUpdateService;

public class SettingActivity extends Activity implements OnPreferenceChangeListener{
	ListView lv;
	SettingAdapter adapter;
	String[] items = { "�����", "������","�͸�����","QQȺ��271436525", "����", "ע���¼" };
	int vc;// ��ȡ��ǰ�汾��
	File sdcardDir;
	String path;
	File f;
	File[] fl;
	ACache mCache;
	private boolean isExit = true;// trueΪ��¼״̬
	private CountDownTimer timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_setting);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(true);
//		}
//
//		SystemBarTintManager tintManager = new SystemBarTintManager(this);
//		tintManager.setStatusBarTintEnabled(true);
//		tintManager.setStatusBarTintResource(R.color.actionbar_color);
		mCache = ACache.get(this);
		vc = getVersionCode(this); 
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		ab.setTitle("����");
		lv = (ListView) findViewById(R.id.lv);
		adapter = new SettingAdapter(items, this);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent it = new Intent();
				switch (arg2) {
				case 0:
					it.setClass(getApplicationContext(), FeedBackActivity.class);
					startActivity(it);
					break;
				case 1:
					chekedVersionCode();
					break;
				case 2:
					Intent i = getIntent(SettingActivity.this,"com.fastspider.fastcat");
					boolean b = judge(SettingActivity.this, i);
					if (b == false) {
						startActivity(i);
					}
					break;
				case 3:
					ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
					clip.setText("271436525"); // ����
					showCustomToast(getString(R.string.back_exit_qq),
							R.id.ll_setting_id);
					break;
				case 4:

					sdcardDir = Environment.getExternalStorageDirectory();
					path = sdcardDir.getPath() + "/zhidu";
					f = new File(path);
					fl = f.listFiles();
					Log.e("fl.length==", fl.length + "");
					if (fl.length == 0) {
						showCustomToast(getString(R.string.back_exit_no),
								R.id.ll_setting_id);
					} else {
						new SweetAlertDialog(SettingActivity.this,
								SweetAlertDialog.CUSTOM_IMAGE_TYPE)
								.setTitleText("����")
								.setContentText("�Ƿ����棿")
								.setCancelText("�ݲ����")
								.setConfirmText("����")
								.showCancelButton(true)
								.setCancelClickListener(
										new SweetAlertDialog.OnSweetClickListener() {
											@Override
											public void onClick(
													SweetAlertDialog sDialog) {
												sDialog.dismiss();
											}
										})
								.setConfirmClickListener(
										new SweetAlertDialog.OnSweetClickListener() {
											@Override
											public void onClick(
													SweetAlertDialog sDialog) {

												//
												for (int i = 0; i < fl.length; i++) {
													if (fl[i].toString()
															.endsWith(".mp3")
															|| fl[i].toString()
																	.endsWith(
																			".MP3")||fl[i].toString()
																			.endsWith(".jpg")||fl[i].toString()
																			.endsWith(".JPG")) {
														fl[i].delete();
													}
												}
												showCustomToast(getString(R.string.back_exit_success),
														R.id.ll_setting_id);
												sDialog.dismiss();
											}
										}).show();
					}

					break;
				case 5:

					new SweetAlertDialog(SettingActivity.this,
							SweetAlertDialog.CUSTOM_IMAGE_TYPE)
							.setTitleText("ע���¼")
							.setContentText("�Ƿ�ע���¼��")
							.setCancelText("�ݲ�ע��")
							.setConfirmText("ע���¼")
							.showCancelButton(true)
							.setCancelClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												SweetAlertDialog sDialog) {
											sDialog.dismiss();
										}
									})
							.setConfirmClickListener(
									new SweetAlertDialog.OnSweetClickListener() {
										@Override
										public void onClick(
												SweetAlertDialog sDialog) {

											mCache.clear();
											isExit = false;

											sDialog.dismiss();
										}
									}).show();

					break;
				case 6:
					 
					break;
				default:
					break;
				}
			}
		});
	}
	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}
	  /**
     * ��ʾShortToast
     */
    public void showCustomToast(String pMsg, int view_position) {
	// ToastUtil.createCustomToast(this, pMsg, Toast.LENGTH_SHORT).show();
	 Crouton.makeText(this, pMsg, Style.CONFIRM, view_position).show();
//	Crouton.makeText(this, pMsg, Style.ALERT, view_position).show();
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (isExit == false) {
				// �����ʹ��Intent����
				Intent intent = new Intent();
				// �ѷ�����ݴ���Intent
				intent.putExtra("result", "exit");
				// ���÷������
				SettingActivity.this.setResult(RESULT_OK, intent);
				// �ر�Activity
				SettingActivity.this.finish();
//				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				// �����ʹ��Intent����
				Intent intent = new Intent();
				// �ѷ�����ݴ���Intent
				intent.putExtra("date", "�û�û���˳�---��¼״̬");
				// ���÷������
				SettingActivity.this.setResult(RESULT_OK, intent);
				// �ر�Activity
				SettingActivity.this.finish();
//				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
			// finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * ��ȡ�汾��
	 * 
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// ��ȡ����汾��
			versionCode = context.getPackageManager().getPackageInfo(
					"com.fastspider.fastcat", 1).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	public void chekedVersionCode() {
		if (!NetWorkUtil.networkCanUse(this)) {
			new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
					.setTitleText("��������ʧ��...").setContentText("����������������Ƿ���")
					.show();
			return;
		}
		Ion.with(this, Conf.VERSION_CODE).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						if (e != null) {
							return;
						}
						String code = result.get("code").getAsString();
						int jsonCode = Integer.parseInt(code);
						// �ȽϿ�Դ�й�ص�code��ǰ�汾code�Ƿ�һ��
						if (jsonCode == vc) {
							Log.e("11111", "�汾����ͬ��������" + "jsonCode:" + jsonCode);
							new SweetAlertDialog(SettingActivity.this,
									SweetAlertDialog.SUCCESS_TYPE)
									.setTitleText("��ǰ�汾��������")
									.setContentText("Version:" + getAppInfo())
									.show();

						} else if (jsonCode > vc) {
							new SweetAlertDialog(SettingActivity.this,
									SweetAlertDialog.WARNING_TYPE)
									.setTitleText("�汾���")
									.setContentText("�����°汾���Ƿ���£�")
									.setCancelText("�ݲ�����")
									.setConfirmText("���ϸ���")
									.showCancelButton(true)
									.setCancelClickListener(
											new SweetAlertDialog.OnSweetClickListener() {
												@Override
												public void onClick(
														SweetAlertDialog sDialog) {
													sDialog.dismiss();
												}
											})
									.setConfirmClickListener(
											new SweetAlertDialog.OnSweetClickListener() {
												@Override
												public void onClick(
														SweetAlertDialog sDialog) {
													Intent updateIntent = new Intent(
															SettingActivity.this,
															AppUpdateService.class);
													updateIntent.putExtra(
															"titleId",
															R.string.app_name);
													startService(updateIntent);
													sDialog.dismiss();

												}
											}).show();
						}

					}
				});
	}

	private String getAppInfo() {
		try {
			String pkName = this.getPackageName();
			String versionName = this.getPackageManager().getPackageInfo(
					pkName, 0).versionName;
			return  versionName;
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // ���/����/���η��ؼ�
			if (isExit == false) {
				// �����ʹ��Intent����
				Intent intent = new Intent();
				// �ѷ�����ݴ���Intent
				intent.putExtra("result", "exit");
				// ���÷������
				SettingActivity.this.setResult(RESULT_OK, intent);
				// �ر�Activity
				SettingActivity.this.finish();
//				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			} else {
				// �����ʹ��Intent����
				Intent intent = new Intent();
				// �ѷ�����ݴ���Intent
				intent.putExtra("date", "�û�û���˳�---��¼״̬");
				// ���÷������
				SettingActivity.this.setResult(RESULT_OK, intent);
				// �ر�Activity
				SettingActivity.this.finish();
//				overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public static Intent getIntent(Context paramContext,String packageName) {
		StringBuilder localStringBuilder = new StringBuilder()
				.append("market://details?id=");
		String str = /* paramContext.getPackageName(); */packageName;
		localStringBuilder.append(str);
		Uri localUri = Uri.parse(localStringBuilder.toString());
		return new Intent("android.intent.action.VIEW", localUri);
	}

	// ֱ����ת���ж��Ƿ�����г�Ӧ��
	public static void start(Context paramContext, String paramString) {
		Uri localUri = Uri.parse(paramString);
		Intent localIntent = new Intent("android.intent.action.VIEW", localUri);
		localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		paramContext.startActivity(localIntent);
	}

	public static boolean judge(Context paramContext, Intent paramIntent) {
		List<ResolveInfo> localList = paramContext.getPackageManager()
				.queryIntentActivities(paramIntent,
						PackageManager.GET_INTENT_FILTERS);
		if ((localList != null) && (localList.size() > 0)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (Boolean.parseBoolean(newValue.toString())) {
			
		 
		}
	 
	
		return true;
	}
}
