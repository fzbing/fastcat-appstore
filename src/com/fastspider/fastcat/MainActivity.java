package com.fastspider.fastcat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fastspider.fastcat.downfile.DownProgress;
import com.fastspider.fastcat.downfile.DownloadFileProgress;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends FragmentActivity {
    private DoubleClickExitHelper mDoubleClickExitHelper;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    RelativeLayout rl;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    public static FragmentManager fm;
    Boolean openOrClose = false;
    File sdcardDir;
    String path;
    File f;
    File[] fl;
    ProgressDialog m_progressDlg;
    Handler m_mainHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createSDCardDir();

        //左上角三角形按钮
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

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, drawerArrow, R.string.drawer_open, R.string.drawer_close) {

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

//
        // todo 解决依赖
        m_progressDlg = new ProgressDialog(MainActivity.this);
        m_progressDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
        m_progressDlg.setIndeterminate(false);
        new VersionAsyncTask().execute();
//

        clearCache();
    }

    // 更新任务
    private class VersionAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private String downlink;

        @Override
        protected Boolean doInBackground(Void... params) {
            // todo 依赖
            List<NameValuePair> vps = new ArrayList<NameValuePair>();
            vps.add(new BasicNameValuePair("action", "checkNewestVersion"));
            try {
                JSONObject json = Common.postServer(vps);
                // todo 依赖
                if (Integer.parseInt(json.get("version").toString()) != Common.getVerCode(MainActivity.this)) {
                    downlink = json.get("downlink").toString();
                    return true;
                }
            } catch (JSONException e) {
                Log.e("json", e.getMessage());
            } catch (Exception e) {
                // TODO: 15-10-28  java.lang.NullPointerException: println needs a message
                Log.e("json", e.getMessage());
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                // 更新新版本
                String str = "发现新版本，是否更新？";
                Dialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("软件更新").setMessage(str)
                        .setPositiveButton("下载更新",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_progressDlg.setTitle("正在下载");
                                        m_progressDlg.setMessage("请稍候...");
                                        m_progressDlg.show();
                                        (new FileDown()).downFile(downlink, Environment.getExternalStorageDirectory(), "aaa.apk");
                                    }
                                })
                        .setNegativeButton("暂不更新",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                }).create();
                dialog.show();
            }
            super.onPostExecute(result);
        }
    }

    private class FileDown extends DownloadFileProgress {

        // TODO: 15-10-28 依赖 m_progressDlg

        @Override
        public void onStart(long length) {
            // todo
            m_progressDlg.setMax((int) length);
        }

        @Override
        public void onUpdate(long count) {
            m_progressDlg.setProgress((int) count);
        }

        @Override
        public void onEnd() {
            m_mainHandler.post(new Runnable() {
                public void run() {
                    m_progressDlg.cancel();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "aaa.apk")),
                            "application/vnd.android.package-archive");
                    startActivity(intent);
                }
            });
        }
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
        FragmentTransaction ft = fm.beginTransaction();
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
                System.out.println("path ok,path:" + path);
            }
        } else {
            System.out.println("false");
            return;
        }
    }

    private String getVersionName() throws Exception {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        return packInfo.versionName;
    }
}
