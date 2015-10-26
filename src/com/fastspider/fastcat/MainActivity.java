package com.fastspider.fastcat;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.umeng.analytics.MobclickAgent;
import com.fastspider.fastcat.activity.FeedBackActivity;
import com.fastspider.fastcat.activity.SettingActivity;
import com.fastspider.fastcat.cache.ACache;
import com.fastspider.fastcat.commont.DoubleClickExitHelper;
import com.fastspider.fastcat.dialog.SweetAlertDialog;
import com.fastspider.fastcat.fragment.AppTuiFragment;
import com.fastspider.fastcat.fragment.EveryDayEnglishFragment;
import com.fastspider.fastcat.fragment.HomeFragment;
import com.fastspider.fastcat.fragment.OtherFragment;
import com.fastspider.fastcat.lib.ActionBarDrawerToggle;
import com.fastspider.fastcat.lib.DrawerArrowDrawable;
import com.fastspider.fastcat.lib.RoundedImageView;
import com.fastspider.fastcat.lib.StringUtil;
import com.fastspider.fastcat.lib.toast.Crouton;
import com.fastspider.fastcat.lib.toast.Style;
import com.fastspider.fastcat.lib.weibo.User;
import com.fastspider.fastcat.lib.weibo.UsersAPI;
import com.fastspider.fastcat.service.AppUpdateService;

public class MainActivity extends FragmentActivity {
	private DoubleClickExitHelper mDoubleClickExitHelper;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	RelativeLayout rl;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerArrowDrawable drawerArrow;
	public static FragmentManager fm;
	Boolean openOrClose = false;
	ACache mCache;
	RoundedImageView iv_main_left_head;
	RelativeLayout toprl;
	File sdcardDir;
	String path;
	File f;
	File[] fl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

//		mCache = ACache.get(this);
//		mDoubleClickExitHelper = new DoubleClickExitHelper(this);
//		toprl = (RelativeLayout) findViewById(R.id.toprl);
//		animll_id = (LinearLayout) findViewById(R.id.animll_id);
//		login_tv = (ImageView) findViewById(R.id.login_tv);
//		user_name = (TextView) findViewById(R.id.user_name);
		iv_main_left_head = (RoundedImageView) findViewById(R.id.iv_main_left_head);


		createSDCardDir();

		MobclickAgent.updateOnlineConfig(this);

		ActionBar ab = getActionBar();

		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		init();
		fm = this.getSupportFragmentManager();
		rl = (RelativeLayout) findViewById(R.id.rl);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.navdrawer);

		drawerArrow = new DrawerArrowDrawable(this) {
			@Override
			public boolean isLayoutRtl() {
				return false;
			}
		};
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				drawerArrow, R.string.drawer_open, R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				invalidateOptionsMenu();
				openOrClose = false;
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				invalidateOptionsMenu();
				openOrClose = true;
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();

		clearCache();
	}
	@TargetApi(19)

	private void clearCache() {
		sdcardDir = Environment.getExternalStorageDirectory();
		path = sdcardDir.getPath() + "/zhidu";
		f = new File(path);
		fl = f.listFiles();
		Log.e("fl.length==", fl.length + "");
		if (fl.length == 0) {

		} else {

			for (int i = 0; i < fl.length; i++) {
				if (fl[i].toString().endsWith(".mp3")
						|| fl[i].toString().endsWith(".MP3")) {
					fl[i].delete();
				}
			}
		}
	}
	  /**
     * 显示ShortToast
     */
    public void showCustomToast(String pMsg, int view_position) {
	 Crouton.makeText(this, pMsg, Style.CONFIRM, view_position).show();
    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private void init() {
		fm = getSupportFragmentManager();
		// 只當容器，主要內容已Fragment呈現
		initFragment(new EveryDayEnglishFragment());
	}


	// 初始化Fragment(FragmentActivity中呼叫)
	public void initFragment(Fragment f) {
		changeFragment(f, true);
	}

	private void changeFragment(Fragment f, boolean init) {
		FragmentTransaction ft = fm.beginTransaction().setCustomAnimations(
				R.anim.umeng_fb_slide_in_from_left,
				R.anim.umeng_fb_slide_out_from_left,
				R.anim.umeng_fb_slide_in_from_right,
				R.anim.umeng_fb_slide_out_from_right);
		;
		ft.replace(R.id.fragment_layout, f);
		if (!init)
			ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {

			if (openOrClose == false) {
				showCustomToast(getString(R.string.back_exit_tips),
						R.id.fragment_layout);
				return mDoubleClickExitHelper.onKeyDown(keyCode, event);
			} else {
				mDrawerLayout.closeDrawers();
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void createSDCardDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + "/zhidu";
			File path1 = new File(path);
			if (!path1.exists()) {
				// 若不存在，创建目录，可以在应用启动的时候创建
				path1.mkdirs();
				System.out.println("paht ok,path:" + path);
			}
		} else {
			System.out.println("false");
			return;
		}

	}

}
