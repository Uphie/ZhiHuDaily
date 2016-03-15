package studio.uphie.zhihudaily.ui;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.FileNotFoundException;

import butterknife.Bind;
import studio.uphie.zhihudaily.R;
import studio.uphie.zhihudaily.abs.AbsBaseActivity;
import studio.uphie.zhihudaily.interfaces.DownloadListener;
import studio.uphie.zhihudaily.utils.FileManager;
import studio.uphie.zhihudaily.utils.ImageUtil;
import studio.uphie.zhihudaily.utils.TextToast;

/**
 * Created by Uphie on 2016/3/8 0008.
 * Email:uphie7@gmail.com
 */
public class ImgActivity extends AbsBaseActivity {

    @Bind(R.id.common_toolbar)
    Toolbar toolbar;
    @Bind(R.id.sd_img_detail)
    SimpleDraweeView sd_imgDetail;

    private String url;

    @Override
    public int getLayoutId() {
        return R.layout.activity_img_detail;
    }

    @Override
    public void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));

        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(new RoundProgressDrawable())
                .build();
        sd_imgDetail.setHierarchy(hierarchy);

        url = getIntent().getStringExtra("url");
        ImageUtil.displayImage(url, sd_imgDetail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_img, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_save_img:
                final String fileName = System.currentTimeMillis() + ".jpg";
                final String filePath = FileManager.getImgDownloadDir();
                final String file = filePath + "/" + fileName;
                downloadImage(url, file, new DownloadListener() {
                    @Override
                    public void onStart() {
                        showProgress();
                    }

                    @Override
                    public void onFinish() {
                        closeProgress();
                        TextToast.shortShow("已保存");
                        // 其次把文件插入到系统图库,但是小米手机不行
                        try {
                           MediaStore.Images.Media.insertImage(ImgActivity.this.getContentResolver(),
                                    file, fileName, null);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail() {
                        closeProgress();
                        TextToast.shortShow("保存失败");
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class RoundProgressDrawable extends ProgressBarDrawable{
        @Override
        protected boolean onLevelChange(int level) {
            return super.onLevelChange(level);
        }
    }

}
