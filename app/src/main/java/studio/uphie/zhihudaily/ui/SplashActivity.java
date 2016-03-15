package studio.uphie.zhihudaily.ui;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import studio.uphie.zhihudaily.R;
import studio.uphie.zhihudaily.abs.AbsBaseActivity;
import studio.uphie.zhihudaily.http.Api;
import studio.uphie.zhihudaily.utils.ImageUtil;
import studio.uphie.zhihudaily.utils.SysUtil;

/**
 * Created by Uphie on 2016/2/26 0026.
 * Email:uphie7@gmail.com
 */
public class SplashActivity extends AbsBaseActivity {

    @Bind(R.id.sd_splash)
    SimpleDraweeView sd_splash;
    @Bind(R.id.tv_author)
    TextView tv_author;

    private Animation animation;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void init() {
        animation= AnimationUtils.loadAnimation(this,R.anim.ani_splash);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        String dimen;
        int width = SysUtil.getScreenWidth(this);
        if (width >= 900) {
            dimen = "1080*1776";
        } else if (width >= 600 && width < 900) {
            dimen = "720*1184";
        } else if (width >= 400 && width < 600) {
            dimen = "480*728";
        } else {
            dimen = "320*432";
        }
        get(Api.URL_SPLASH_IMG + dimen);
    }

    @Override
    public void onDataOK(String url, String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String img = jsonObject.optString("img");
            String text = jsonObject.optString("text");

            ImageUtil.displayImage(img, sd_splash);
            tv_author.setText(text);

            sd_splash.startAnimation(animation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetworkUnavailable(String url) {
        ImageUtil.showDrawableImage(R.drawable.splash,sd_splash);
        sd_splash.startAnimation(animation);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //闪屏页禁掉返回键
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
