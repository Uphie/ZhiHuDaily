package studio.uphie.zhihudaily.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import studio.uphie.zhihudaily.R;
import studio.uphie.zhihudaily.abs.AbsBaseActivity;
import studio.uphie.zhihudaily.utils.TextToast;

/**
 * Created by Uphie on 2016/3/13 0013.
 * Email:uphie7@gmail.com
 */
public class LoginActivity extends AbsBaseActivity {

    @Bind(R.id.common_toolbar)
    Toolbar toolbar;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));
        toolbar.setTitle(R.string.login);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.block_sina_weibo_login, R.id.block_tencent_weibo_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.block_sina_weibo_login:
                TextToast.shortShow("暂无新浪微博登录功能");
                break;
            case R.id.block_tencent_weibo_login:
                TextToast.shortShow("暂无腾讯微博登录功能");
                break;
        }
    }
}
