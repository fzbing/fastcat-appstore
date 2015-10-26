package com.fastspider.fastcat.activity;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.fastspider.fastcat.MainActivity;
import com.fastspider.fastcat.R;
import com.fastspider.fastcat.cache.ACache;
import com.fastspider.fastcat.lib.RoundedImageView;

public class SplashActivity extends Activity {
	TextView logo_text,logo_name;
	RelativeLayout splash_id;
	ACache mCache;
	Random random;
	RoundedImageView iv_main_left_head;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		random = new Random();
		int avatarColor = Color.argb(255, random.nextInt(256),
				random.nextInt(256), random.nextInt(256));
		iv_main_left_head = (RoundedImageView) findViewById(R.id.iv_main_left_head);
		
		logo_name = (TextView) findViewById(R.id.logo_name);
		mCache = ACache.get(this);
		
		splash_id = (RelativeLayout) findViewById(R.id.splash_id);
//		splash_id.setBackgroundColor(avatarColor);
		String avatar = mCache.getAsString("avatar");
		String name = mCache.getAsString("name");
		try {
			if (avatar.equals("")) {
				logo_name.setText("ָ��");
				iv_main_left_head.setImageResource(R.drawable.ic_launcher);
				
			} else {
				Ion.with(SplashActivity.this).load(avatar).asBitmap()
						.setCallback(new FutureCallback<Bitmap>() {

							@Override
							public void onCompleted(Exception e, Bitmap bitmap) {
								// ivHead.setImageBitmap(bitmap);
								iv_main_left_head.setImageBitmap(bitmap);
							}
						});

			}

			if (!name.equals("")) {
				logo_name.setText(name+",��ӭ����");
			}  
		} catch (Exception e) {
			// TODO: handle exception
		}
		logo_text = (TextView) findViewById(R.id.logo_text);
		try {
			String str = getVersionName();
			logo_text.setText("��ǰ�汾�ţ�"+str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		//����չʾ������
		AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
		aa.setDuration(3000);
		splash_id.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0) {
				Intent it = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(it);
				finish();
			}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationStart(Animation animation) {}
			
		});
	}
	 private String getVersionName() throws Exception
	   {
	           // ��ȡpackagemanager��ʵ��
	           PackageManager packageManager = getPackageManager();
	           // getPackageName()���㵱ǰ��İ���0����ǻ�ȡ�汾��Ϣ
	           PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
	           String version = packInfo.versionName;
	           return version;
	   }
	 
}
