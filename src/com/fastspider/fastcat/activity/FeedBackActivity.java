package com.fastspider.fastcat.activity;

import java.lang.reflect.Field;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fastspider.fastcat.R;
import com.fastspider.fastcat.dialog.SweetAlertDialog;
import com.fastspider.fastcat.feedback.EmailSender;
import com.fastspider.fastcat.lib.NetWorkUtil;

public class FeedBackActivity extends Activity {
	EditText feedback_content_edit, feedback_contact_edit;
	Button submit_button;
	private String filepath;
	MenuItem item;


	@SuppressLint("SdCardPath")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(true);
//		}
//
//		SystemBarTintManager tintManager = new SystemBarTintManager(this);
//		tintManager.setStatusBarTintEnabled(true);
//		tintManager.setStatusBarTintResource(R.color.actionbar_color);
		ActionBar ab = getActionBar();
		setTitle("�����");
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		try {
			ViewConfiguration mconfig = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);

				menuKeyField.setBoolean(mconfig, false);
			}
		} catch (Exception ex) {
		}
		feedback_content_edit = (EditText) findViewById(R.id.feedback_content_edit);
		feedback_contact_edit = (EditText) findViewById(R.id.feedback_contact_edit);
		submit_button = (Button) findViewById(R.id.submit_button);

	}

	public void initData(final String contact, final String content) {
		new Thread() {
			@Override
			public void run() {
				EmailSender sender = new EmailSender();
				// ���÷�������ַ�Ͷ˿ڣ������ѵĵ�
				sender.setProperties("smtp.163.com", "25");// smtp.163.com
				// �ֱ����÷����ˣ��ʼ�������ı�����
				try {
					try {
						sender.addAttachment(filepath);
					} catch (Exception e) {
						Log.e("e:", e + "");
					}
					sender.setMessage("xxx@163.com", contact, content);//����������&����&����
					sender.setReceiver(new String[] { "xxxx@163.com" });// �ռ�������
					sender.sendEmail("smtp.163.com", "xxx@163.com",
							"**********");//�����������Լ�����
				} catch (AddressException e) {
					e.printStackTrace();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@SuppressWarnings("unused")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else { 

			// TODO Auto-generated method stub
			String content = feedback_content_edit.getText().toString()
					.trim();
			String contact = feedback_contact_edit.getText().toString()
					.trim();
			if (!content.equals("")) {

				if (NetWorkUtil.networkCanUse(getApplicationContext())) {
					initData(contact, content);
				  
					finish();
					Toast.makeText(getApplicationContext(),
							"��л��ı������..", 1).show();
				} else {
					if (!NetWorkUtil.networkCanUse(FeedBackActivity.this)) {
						new SweetAlertDialog(FeedBackActivity.this,
								SweetAlertDialog.ERROR_TYPE)
								.setTitleText("��������ʧ��...")
								.setContentText("����������������Ƿ���").show();
						 
					}
				}

			} else {
				new SweetAlertDialog(FeedBackActivity.this).setTitleText(
						"˵��ʲô��..").show();
			}
		
			
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.fb, menu);
		 

		return super.onCreateOptionsMenu(menu);
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
	
	public String getDeviceId() {
		String imei = "";
		Context ctx = getApplicationContext();
		if (ctx != null) {

			TelephonyManager telephonyManager = (TelephonyManager) ctx
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (telephonyManager != null)
				imei = telephonyManager.getDeviceId();

			if (TextUtils.isEmpty(imei))
				imei = "0";
		}
		return imei;

	}
}
