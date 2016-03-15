package studio.uphie.zhihudaily.ui;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import studio.uphie.zhihudaily.R;
import studio.uphie.zhihudaily.abs.AbsBaseActivity;
import studio.uphie.zhihudaily.utils.ImageUtil;

/**
 * Created by Uphie on 2016/3/4 0004.
 * Email:uphie7@gmail.com
 */
public class PreferenceActivity extends AbsBaseActivity {

    @Bind(R.id.common_toolbar)
    Toolbar toolbar;
    @Bind(R.id.cb_auto_offline_dl)
    CheckBox cb_autoOfflineDl;
    @Bind(R.id.cb_non_img)
    CheckBox cb_nonImg;
    @Bind(R.id.cb_big_font)
    CheckBox cb_bigFont;
    @Bind(R.id.cb_push_msg)
    CheckBox cb_pushMsg;
    @Bind(R.id.cb_comment_share)
    CheckBox cb_commentShare;
    @Bind(R.id.tv_clear_cache)
    TextView tv_clearCache;
    @Bind(R.id.tv_feedback)
    TextView tv_feedback;

    @Override
    public int getLayoutId() {
        return R.layout.activity_preference;
    }

    @Override
    public void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));
    }

    @OnClick({R.id.tv_clear_cache, R.id.tv_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_clear_cache:
                ImageUtil.clearFrescoDiscCache();
                break;
            case R.id.tv_feedback:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
