package com.fastspider.fastcat.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
    TextView logo_text, logo_name;
    RelativeLayout splash_id;
    ACache mCache;
    RoundedImageView iv_main_left_head;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        iv_main_left_head = (RoundedImageView) findViewById(R.id.iv_main_left_head);

        logo_name = (TextView) findViewById(R.id.logo_name);
        mCache = ACache.get(this);

        splash_id = (RelativeLayout) findViewById(R.id.splash_id);
        String avatar = mCache.getAsString("avatar");
        String name = mCache.getAsString("name");
        try {
            if (avatar.equals("")) {
                logo_name.setText("快喵");
                iv_main_left_head.setImageResource(R.drawable.ic_launcher);

            } else {
                Ion.with(SplashActivity.this).load(avatar).asBitmap().setCallback(new FutureCallback<Bitmap>() {

                    @Override
                    public void onCompleted(Exception e, Bitmap bitmap) {
                        // ivHead.setImageBitmap(bitmap);
                        iv_main_left_head.setImageBitmap(bitmap);
                    }
                });

            }

            if (!name.equals("")) {
                logo_name.setText(name);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        logo_text = (TextView) findViewById(R.id.logo_text);


//        自动更新
        try {

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(3000);
        splash_id.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                Intent it = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(it);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });
    }
}
