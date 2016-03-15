package studio.uphie.zhihudaily.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import studio.uphie.zhihudaily.R;
import studio.uphie.zhihudaily.abs.AbsBaseActivity;
import studio.uphie.zhihudaily.http.Api;
import studio.uphie.zhihudaily.utils.TextToast;

/**
 * Created by Uphie on 2016/3/3 0003.
 * Email:uphie7@gmail.com
 */
public class StoryDetailActivity extends AbsBaseActivity implements ViewPager.OnPageChangeListener {

    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.menu_item_back)
    TextView menu_back;
    @Bind(R.id.menu_item_share)
    TextView menu_share;
    @Bind(R.id.menu_item_collect)
    TextView menu_collect;
    @Bind(R.id.menu_item_comment)
    TextView menu_comment;
    @Bind(R.id.menu_item_comment_num)
    TextView menu_comment_num;
    @Bind(R.id.menu_item_praise)
    TextView menu_praise;
    @Bind(R.id.menu_item_praise_num)
    TextView menu_praise_num;
    @Bind(R.id.bar)
    RelativeLayout bar;

    private ArrayList<Integer> idList;
    private ArrayList<Integer> readIdList=new ArrayList<>();
    private  int vote_status;
    private int praised;
    private int comments;
    private int long_comments;
    private int short_comments;

    private int current_story_id;

    @Override
    public int getLayoutId() {
        return R.layout.activity_story_detail;
    }

    @Override
    public void init() {

        idList = getIntent().getIntegerArrayListExtra("ids");
        int index = getIntent().getIntExtra("index", 0);

        current_story_id=idList.get(index);
        readIdList.add(current_story_id);

        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(new DetailAdapter(getSupportFragmentManager(), idList));
        viewPager.setCurrentItem(index);

        get(Api.URL_STORY_DETAIL_EXTRA + idList.get(index));
    }

    @Override
    public void onDataOK(String url, String data) {
        if (url.contains(Api.URL_STORY_DETAIL_EXTRA)) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                boolean hasCollected = jsonObject.optBoolean("favorite");
                comments = jsonObject.getInt("comments");
                long_comments = jsonObject.optInt("long_comments");
                short_comments = jsonObject.optInt("short_comments");
                int normal_comments = jsonObject.optInt("normal_comments");
                int post_reasons = jsonObject.optInt("post_reasons");
                vote_status = jsonObject.optInt("vote_status");
                praised = jsonObject.optInt("popularity");

                menu_collect.setBackgroundResource(hasCollected ? R.drawable.collected : R.drawable.collect);
                String com_num = comments > 1000 ? new DecimalFormat("#.0").format(((float) comments) / 1000) + "K" : comments + "";
                menu_comment_num.setText(com_num);
                menu_praise.setBackgroundResource(vote_status == 0 ? R.drawable.praise : R.drawable.praised);
                String pra_num = praised > 1000 ? new DecimalFormat("#.0").format(((float) praised) / 1000) + "K" : praised + "";
                menu_praise_num.setText(pra_num);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        current_story_id=idList.get(position);
        get(Api.URL_STORY_DETAIL_EXTRA + current_story_id);
        int story_id=idList.get(position);
        if (!readIdList.contains(story_id)){
            readIdList.add(story_id);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @OnClick({R.id.menu_item_back, R.id.menu_item_share, R.id.menu_item_collect, R.id.menu_item_comment, R.id.menu_item_praise})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_item_back:
                finish();
                break;
            case R.id.menu_item_share:
                TextToast.longShow("暂未开启此功能");
                break;
            case R.id.menu_item_collect:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.menu_item_comment:
                Intent intent=new Intent(this,CommentsActivity.class);
                intent.putExtra("story_id",current_story_id);
                intent.putExtra("short_comments",short_comments);
                intent.putExtra("long_comments",long_comments);
                intent.putExtra("comments",comments);
                startActivity(intent);
                break;
            case R.id.menu_item_praise:
            case R.id.menu_item_praise_num:
                if (vote_status==0){
                    menu_praise.setBackgroundResource(R.drawable.praised);
                    vote_status=1;
                    praised++;
                }else {
                    menu_praise.setBackgroundResource(R.drawable.praise);
                    vote_status=0;
                    praised--;
                }
                String pra_num = praised > 1000 ? new DecimalFormat("#.0").format(((float) praised) / 1000) + "K" : praised + "";
                menu_praise_num.setText(pra_num);
                TextToast.longShow("注：不能真正的赞/取消赞");
                break;
        }
    }

    @Override
    public void finish() {
        Intent intent=new Intent();
        intent.putExtra("readIds",readIdList);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    class DetailAdapter extends FragmentPagerAdapter {

        private ArrayList<Integer> idList;

        public DetailAdapter(FragmentManager fm, ArrayList<Integer> data) {
            super(fm);
            this.idList = data;
        }

        @Override
        public Fragment getItem(int position) {
            StoryDetailFragment fragment = new StoryDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("news_id", idList.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return idList.size();
        }
    }

}
