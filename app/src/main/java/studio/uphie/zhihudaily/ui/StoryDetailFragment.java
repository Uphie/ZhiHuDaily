package studio.uphie.zhihudaily.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.Bind;
import studio.uphie.zhihudaily.R;
import studio.uphie.zhihudaily.abs.AbsBaseFragment;
import studio.uphie.zhihudaily.http.Api;
import studio.uphie.zhihudaily.utils.ImageUtil;
import studio.uphie.zhihudaily.utils.JsonUtil;
import studio.uphie.zhihudaily.widgets.UWebView;

/**
 * Created by Uphie on 2016/3/8 0008.
 * Email:uphie7@gmail.com
 */
public class StoryDetailFragment extends AbsBaseFragment{

    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.block_story_img)
    FrameLayout block_story_img;
    @Bind(R.id.block_recommenders)
    LinearLayout block_recommenders;
    @Bind(R.id.sd_news_img)
    SimpleDraweeView sd_newsImg;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.tv_img_source)
    TextView tv_img_source;
    @Bind(R.id.wv_news)
    UWebView webView;

    private int news_id;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_story;
    }

    @Override
    public void init() {
        news_id = getArguments().getInt("news_id");
        get(Api.URL_STORY_DETAIL + news_id);
    }

    @Override
    public void onDataOK(String url, String data) {
        if (url.equals(Api.URL_STORY_DETAIL + news_id)) {
            News news = JsonUtil.getEntity(data, News.class);
            if (!TextUtils.isEmpty(news.image)) {
                ImageUtil.displayImage(news.image, sd_newsImg);
            } else {
                block_story_img.setVisibility(View.GONE);
            }
            tv_title.setText(news.title);
            tv_img_source.setText(news.image_source);
            if (news.recommenders == null) {
                block_recommenders.setVisibility(View.GONE);
            } else {
                block_recommenders.removeViews(1, block_recommenders.getChildCount()-1);
                for (News.Recommender rec : news.recommenders) {
                    SimpleDraweeView avatar = (SimpleDraweeView) View.inflate(getActivity(), R.layout.list_item_recommender, null);
                    ImageUtil.displayImage(rec.avatar, avatar);
                    block_recommenders.addView(avatar);
                }
            }

            //build a html content and load it with webview
            String css = "";
            for (String css_url : news.css) {
                css += "<link rel=\"stylesheet\" href=" + css_url + ">\n";
            }
            String js = "";
            for (String js_url : news.js) {
                js += "<script src=" + js_url + "/>\n";
            }
            String body = news.body.replaceAll("<div class=\"img-place-holder\"></div>", "");

            StringBuilder builder = new StringBuilder();
            builder.append("<html>\n")
                    .append("<head>\n")
                    .append(css).append(js)
                    .append("</head>\n")
                    .append("<body>")
                    .append(body)
                    .append("</body>\n")
                    .append("</html>");
            webView.loadData(builder.toString(), "text/html;charset=UTF-8", "UTF-8");

        }
    }


    class News {
        String body;
        String image_source;
        String title;
        //may not exist
        String image;
        String share_url;
        List<String> js;
        String ga_prefix;
        int type;
        int id;
        List<String> css;
        //may not exist
        List<Recommender> recommenders;

        class Recommender {
            String avatar;
        }
    }

}
