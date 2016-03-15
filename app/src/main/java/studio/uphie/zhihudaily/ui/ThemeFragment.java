package studio.uphie.zhihudaily.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import studio.uphie.zhihudaily.R;
import studio.uphie.zhihudaily.abs.AbsBaseAdapter;
import studio.uphie.zhihudaily.abs.AbsBaseFragment;
import studio.uphie.zhihudaily.http.Api;
import studio.uphie.zhihudaily.utils.ImageUtil;
import studio.uphie.zhihudaily.utils.JsonUtil;
import studio.uphie.zhihudaily.utils.TextToast;

/**
 * Created by Uphie on 2016/3/11 0011.
 * Email:uphie7@gmail.com
 */
public class ThemeFragment extends AbsBaseFragment {

    @Bind(R.id.lv_stories)
    ListView lv_stories;
    @Bind(R.id.layout_refresh)
    SwipeRefreshLayout layoutRefresh;

    SimpleDraweeView sd_theme;
    TextView tv_theme_des;
    TextView tv_theme_img_src;
    LinearLayout ll_editors;

    private int theme_id;
    private StoryAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home_content;
    }

    @Override
    public void init() {
        layoutRefresh.setColorSchemeColors(getResources().getColor(R.color.blue));
        layoutRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                get(Api.URL_THEME_CONTENT + theme_id);
            }
        });
        View header = View.inflate(getActivity(), R.layout.layout_theme_header, null);
        sd_theme = (SimpleDraweeView) header.findViewById(R.id.sd_theme_img);
        tv_theme_des = (TextView) header.findViewById(R.id.tv_theme_des);
        tv_theme_img_src = (TextView) header.findViewById(R.id.tv_theme_img_src);
        ll_editors= (LinearLayout) header.findViewById(R.id.ulayout);

        lv_stories.addHeaderView(header);

        adapter = new StoryAdapter(getActivity());
        lv_stories.setAdapter(adapter);

        theme_id = getArguments().getInt("theme_id");
        get(Api.URL_THEME_CONTENT + theme_id);
    }

    @Override
    public void onDataOK(String url, String data) {
        if (url.contains(Api.URL_THEME_CONTENT)) {
            layoutRefresh.setRefreshing(false);

            ThemeResult themeResult = JsonUtil.getEntity(data, ThemeResult.class);
            ImageUtil.displayImage(themeResult.background, sd_theme);
            tv_theme_des.setText(themeResult.description);
            if (!TextUtils.isEmpty(themeResult.image_source)) {
                tv_theme_img_src.setVisibility(View.VISIBLE);
                tv_theme_img_src.setText(themeResult.image_source);
            }
            adapter.updateAll(themeResult.stories);
// it does not work and is a bug
//            if (themeResult.editors != null) {
//                if (ll_editors.getChildCount()>1){
//                    ll_editors.removeViews(1,ll_editors.getChildCount()-1);
//                }
//                for (ThemeResult.Editor temp : themeResult.editors) {
//                    SimpleDraweeView view= (SimpleDraweeView) View.inflate(getActivity(),R.layout.list_item_recommender,null);
//                    ImageUtil.displayImage(temp.avatar,view);
//                    ll_editors.addView(view);
//                }
//            }
        }
    }

    @Override
    public void onNetworkUnavailable(String url) {
        if (url.equals(Api.URL_HOME_LATEST_STORIES)) {
            layoutRefresh.setRefreshing(false);
            TextToast.shortShow("网络不可用");
        }
    }

    class ThemeResult {
        boolean subscribed;
        List<Story> stories;
        List<Editor> editors;
        String image_source;
        String background;
        String description;
        String name;

        class Story {
            List<String> images;
            int type;
            int id;
            String title;
        }

        class Editor {
            String url;
            String bio;
            int id;
            String avatar;
            String name;
        }
    }

    class StoryAdapter extends AbsBaseAdapter<ThemeResult.Story> {

        private ArrayList<Integer> idList = new ArrayList<>();

        public StoryAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemLayoutID() {
            return R.layout.list_item_story;
        }

        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            ThemeResult.Story story = getItem(position);
            SimpleDraweeView sd_img = holder.findView(R.id.list_item_news_img);
            TextView tv_title = holder.findView(R.id.list_item_news_title);

            if (story.images != null && story.images.size() > 0) {
                ImageUtil.displayImage(story.images.get(0), sd_img);
            } else {
                holder.findView(R.id.list_item_news_block_img).setVisibility(View.GONE);
            }
            tv_title.setText(story.title);
            return convertView;
        }

        @Override
        public void getInfo(ThemeResult.Story story) {
            Intent intent = new Intent(getActivity(), StoryDetailActivity.class);
            intent.putExtra("index", data.indexOf(story));
            intent.putExtra("ids", idList);
            startActivity(intent);
        }

        @Override
        public void updateAll(List<ThemeResult.Story> elems) {
            super.updateAll(elems);
            idList.clear();
            for (ThemeResult.Story temp : elems) {
                idList.add(temp.id);
            }
        }
    }
}
