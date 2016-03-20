package studio.uphie.zhihudaily.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import studio.uphie.zhihudaily.R;
import studio.uphie.zhihudaily.abs.AbsBaseFragment;
import studio.uphie.zhihudaily.abs.AbsBaseOnItemClickListener;
import studio.uphie.zhihudaily.http.Api;
import studio.uphie.zhihudaily.interfaces.IInit;
import studio.uphie.zhihudaily.interfaces.OnSetTitleListener;
import studio.uphie.zhihudaily.utils.ImageUtil;
import studio.uphie.zhihudaily.utils.JsonUtil;
import studio.uphie.zhihudaily.utils.TextToast;

/**
 * Created by Uphie on 2016/2/26 0026.
 * Email:uphie7@gmail.com
 */
public class HomeFragment extends AbsBaseFragment implements IInit, ViewPager.OnPageChangeListener, AbsListView.OnScrollListener {

    @Bind(R.id.lv_stories)
    ListView lv_stories;
    @Bind(R.id.layout_refresh)
    SwipeRefreshLayout layoutRefresh;

    LinearLayout group_pagerDots;

    private BannerAdapter bannerAdapter;
    private NewsAdapter newsAdapter;
    private ViewPager viewPager;

    private boolean hasScrolledBottom;
    private String lastDate;


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
                get(Api.URL_HOME_LATEST_STORIES);
            }
        });

        View header = View.inflate(getActivity(), R.layout.layout_home_banner, null);
        viewPager = (ViewPager) header.findViewById(R.id.viewPager);
        group_pagerDots = (LinearLayout) header.findViewById(R.id.group_dots);

        bannerAdapter = new BannerAdapter();
        viewPager.setAdapter(bannerAdapter);
        viewPager.addOnPageChangeListener(this);

        lv_stories.addHeaderView(header);
        newsAdapter = new NewsAdapter(getActivity());
        lv_stories.setAdapter(newsAdapter);
        lv_stories.setOnScrollListener(this);

        get(Api.URL_HOME_LATEST_STORIES);

        playHandler.sendEmptyMessageDelayed(0, 5000);
    }

    @Override
    public void onDataOK(String url, String data) {
        if (url.contains(Api.URL_HOME_LATEST_STORIES)) {
            layoutRefresh.setRefreshing(false);
            Latest latest = JsonUtil.getEntity(data, Latest.class);
            if (latest != null) {
                if (latest.top_stories != null) {
                    group_pagerDots.removeAllViews();
                    for (int i = 0; i < latest.top_stories.size(); i++) {
                        ImageView dot = new ImageView(getActivity());
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(5, 0, 5, 0);
                        dot.setLayoutParams(layoutParams);
                        group_pagerDots.addView(dot);
                    }
                    bannerAdapter.updateAll(latest.top_stories);
                    onPageSelected(0);
                }

                if (latest.stories != null) {
                    List<News> list = new ArrayList<>();
                    News news = new News();
                    news.type = NewsAdapter.TYPE_LABEL;
                    news.date = latest.date;

                    lastDate = latest.date;

                    list.add(news);
                    for (Story story : latest.stories) {
                        News temp = new News();
                        temp.type = NewsAdapter.TYPE_NEWS;
                        temp.date = latest.date;
                        temp.story = story;
                        list.add(temp);
                    }
                    newsAdapter.updateAll(list);
                }
            }
        } else if (url.contains(Api.URL_HOME_LAST_STORIES)) {
            Last last = JsonUtil.getEntity(data, Last.class);
            if (last.stories != null) {
                List<News> list = new ArrayList<>();
                News news = new News();
                news.type = NewsAdapter.TYPE_LABEL;
                news.date = last.date;

                lastDate = last.date;

                list.add(news);
                for (Story story : last.stories) {
                    News temp = new News();
                    temp.type = NewsAdapter.TYPE_NEWS;
                    temp.date = last.date;
                    temp.story = story;
                    list.add(temp);
                }
                newsAdapter.addAll(list);
            }
        }
    }

    @Override
    public void onNetworkUnavailable(String url) {
        if (url.equals(Api.URL_HOME_LATEST_STORIES)) {
            layoutRefresh.setRefreshing(false);
            TextToast.shortShow("网络不可用");
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < group_pagerDots.getChildCount(); i++) {
            if (i == position) {
                group_pagerDots.getChildAt(i).setBackgroundResource(R.drawable.dot_solid);
            } else {
                group_pagerDots.getChildAt(i).setBackgroundResource(R.drawable.dot_empty);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (hasScrolledBottom) {
            get(Api.URL_HOME_LAST_STORIES + lastDate);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int total = totalItemCount - 1;
        int last = view.getLastVisiblePosition();
        //whether listview reaches bottom
        hasScrolledBottom = last == total;
        if (total > 0) {
            News news = newsAdapter.getItem(firstVisibleItem);
            if (news.type == NewsAdapter.TYPE_LABEL) {
                ((OnSetTitleListener) (getActivity())).onSetTitle(getNewsLabel(news.date));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //remove message ,otherwise it will cause exception for fragment has been destroyed, viewpager and adapter are null.
        playHandler.removeMessages(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            List<Integer> readStoryIds = data.getIntegerArrayListExtra("readIds");
            for (int id : readStoryIds) {
                for (News news : newsAdapter.getData()) {
                    if (news.type == NewsAdapter.TYPE_NEWS && news.story.id == id) {
                        news.hasRead = true;
                    }
                }
            }
            newsAdapter.notifyDataSetChanged();
        }
    }

    class Latest {
        String date;
        List<TopStory> top_stories;
        List<Story> stories;
    }

    class Last {
        String date;
        List<Story> stories;
    }

    class News {
        int type;
        boolean hasRead;
        String date;
        Story story;
    }

    class TopStory {
        String image;
        int type;
        int id;
        String ga_prefix;
        String title;
    }

    class Story {
        //may not exist
        List<String> images;
        int type;
        int id;
        //may not exist
        boolean multipic;
        String ga_prefix;
        String title;
        //may not exist
        Theme theme;

        class Theme {
            int id;
            String name;
            boolean subscribed;
        }
    }

    class BannerAdapter extends android.support.v4.view.PagerAdapter {

        private List<TopStory> list = new ArrayList<>();
        private List<View> banners = new ArrayList<>();
        private ArrayList<Integer> idList = new ArrayList<>();

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TopStory topStory = list.get(position);

            View view = View.inflate(getActivity(), R.layout.list_item_banner, null);

            container.addView(view);
            banners.add(view);

            SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.list_item_banner_img);
            draweeView.setImageURI(Uri.parse(topStory.image));
            TextView tv_title = (TextView) view.findViewById(R.id.list_item_banner_title);
            tv_title.setText(topStory.title);

            view.setOnClickListener(new Listener(topStory));

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(banners.get(position));
        }

        public void updateAll(List<TopStory> data) {
            data = data == null ? new ArrayList<TopStory>() : data;
            list.clear();
            list.addAll(data);
            for (TopStory topStory : list) {
                idList.add(topStory.id);
            }
            notifyDataSetChanged();
        }

        private class Listener extends AbsBaseOnItemClickListener<TopStory> {
            public Listener(TopStory data) {
                super(data);
            }

            @Override
            public void onClick(View view, TopStory data) {
                Intent intent = new Intent(getActivity(), StoryDetailActivity.class);
                intent.putExtra("ids", idList);
                intent.putExtra("index", idList.indexOf(data.id));
                startActivity(intent);
            }
        }
    }

    private class NewsAdapter extends BaseAdapter {

        static final int TYPE_LABEL = 0;
        static final int TYPE_NEWS = 1;

        private LayoutInflater layoutInflater;
        private List<News> list = new ArrayList<>();
        private ArrayList<Integer> idList = new ArrayList<>();

        NewsAdapter(Context ctx) {
            this.layoutInflater = LayoutInflater.from(ctx);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public News getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).type;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            News news = getItem(position);

            LabelHolder labelHolder;
            NewsHolder newsHolder;

            if (convertView == null) {
                switch (type) {
                    case TYPE_LABEL:
                        convertView = layoutInflater.inflate(R.layout.list_item_story_label, null);
                        labelHolder = new LabelHolder(convertView);
                        labelHolder.tv_label.setText(getNewsLabel(news.date));
                        convertView.setTag(labelHolder);
                        break;
                    case TYPE_NEWS:
                        convertView = layoutInflater.inflate(R.layout.list_item_story, null);
                        newsHolder = new NewsHolder(convertView);
                        newsHolder.tv_title.setText(news.story.title);
                        if (news.story.images!=null&&news.story.images.size()>0){
                            ImageUtil.displayImage(news.story.images.get(0), newsHolder.sd_img);
                        }else {
                            newsHolder.sd_img.setVisibility(View.GONE);
                        }
                        //whether has read
                        if (news.hasRead) {
                            newsHolder.tv_title.setTextColor(getResources().getColor(R.color.text_gray));
                        }
                        //whether has multi pictures
                        if (news.story.multipic) {
                            newsHolder.iv_multipic.setVisibility(View.VISIBLE);
                        } else {
                            newsHolder.iv_multipic.setVisibility(View.GONE);
                        }
                        //whether has subscribed
                        if (news.story.theme != null && news.story.theme.subscribed) {
                            newsHolder.tv_src.setText(news.story.theme.name);
                        }
                        convertView.setOnClickListener(new Listener(position));
                        convertView.setTag(newsHolder);
                        break;
                }
            } else {
                switch (type) {
                    case TYPE_LABEL:
                        labelHolder = (LabelHolder) convertView.getTag();
                        labelHolder.tv_label.setText(getNewsLabel(news.date));
                        break;
                    case TYPE_NEWS:
                        newsHolder = (NewsHolder) convertView.getTag();
                        newsHolder.tv_title.setText(news.story.title);
                        //whether has read
                        if (news.hasRead) {
                            newsHolder.tv_title.setTextColor(getResources().getColor(R.color.text_gray));
                        }
                        ImageUtil.displayImage(news.story.images.get(0), newsHolder.sd_img);
                        //whether has multi pictures
                        if (news.story.multipic) {
                            newsHolder.iv_multipic.setVisibility(View.VISIBLE);
                        } else {
                            newsHolder.iv_multipic.setVisibility(View.GONE);
                        }
                        //whether has subscribed
                        if (news.story.theme != null && news.story.theme.subscribed) {
                            newsHolder.tv_src.setText(news.story.theme.name);
                        }
                        convertView.setOnClickListener(new Listener(position));
                        break;
                }
            }
            return convertView;
        }

        public void updateAll(List<News> data) {
            data = data == null ? new ArrayList<News>() : data;
            this.list.clear();
            this.list.addAll(data);
            this.idList.clear();
            for (News news : data) {
                if (news.type == TYPE_NEWS) {
                    idList.add(news.story.id);
                }
            }
            notifyDataSetChanged();
        }

        public void addAll(List<News> data) {
            data = data == null ? new ArrayList<News>() : data;
            this.list.addAll(data);
            for (News news : data) {
                if (news.type == TYPE_NEWS) {
                    idList.add(news.story.id);
                }
            }
            notifyDataSetChanged();
        }

        public List<News> getData() {
            return list;
        }

        class LabelHolder {
            TextView tv_label;

            LabelHolder(View view) {
                tv_label = (TextView) view.findViewById(R.id.list_item_news_label);
            }
        }

        class NewsHolder {
            TextView tv_title;
            TextView tv_src;
            ImageView iv_multipic;
            SimpleDraweeView sd_img;

            NewsHolder(View view) {
                tv_title = (TextView) view.findViewById(R.id.list_item_news_title);
                tv_src = (TextView) view.findViewById(R.id.list_item_news_src);
                sd_img = (SimpleDraweeView) view.findViewById(R.id.list_item_news_img);
                iv_multipic = (ImageView) view.findViewById(R.id.list_item_news_multipic);
            }
        }

        class Listener extends AbsBaseOnItemClickListener<Integer> {

            public Listener(int index) {
                super(index);
            }

            @Override
            public void onClick(View view, Integer index) {
                Intent intent = new Intent(getActivity(), StoryDetailActivity.class);
                intent.putExtra("index", idList.indexOf(getItem(index).story.id));
                intent.putExtra("ids", idList);
                startActivityForResult(intent, 0);
            }
        }

    }


    private Handler playHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (bannerAdapter.getCount() > 1) {
                    //if banners count is greater than 1,play
                    int index = viewPager.getCurrentItem();
                    index++;
                    if (index >= bannerAdapter.getCount()) {
                        index = 0;
                    }
                    viewPager.setCurrentItem(index, true);
                    sendEmptyMessageDelayed(0, 5000);
                }
            }
        }
    };

    private String getNewsLabel(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String today = format.format(new Date());
        if (date.equals(today)) {
            return "今日热闻";
        } else {
            SimpleDateFormat format2 = new SimpleDateFormat("MM月dd日", Locale.getDefault());
            try {
                Date then = format.parse(date);
                String result = format2.format(then);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(then);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                switch (dayOfWeek) {
                    case Calendar.SUNDAY:
                        result += " 星期日";
                        break;
                    case Calendar.MONDAY:
                        result += " 星期一";
                        break;
                    case Calendar.TUESDAY:
                        result += " 星期二";
                        break;
                    case Calendar.WEDNESDAY:
                        result += " 星期三";
                        break;
                    case Calendar.THURSDAY:
                        result += " 星期四";
                        break;
                    case Calendar.FRIDAY:
                        result += " 星期五";
                        break;
                    case Calendar.SATURDAY:
                        result += " 星期六";
                        break;
                }
                return result;
            } catch (ParseException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

}


