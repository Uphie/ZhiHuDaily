package studio.uphie.zhihudaily.http;

/**
 * Created by Uphie on 2016/2/26 0026.
 * Email:uphie7@gmail.com
 */
public class Api {
    /**
     * splash image,add resolution after url please,for example:“http://news-at.zhihu.com/api/4/start-image/1080*1776”
     */
    public static final String URL_SPLASH_IMG = "http://news-at.zhihu.com/api/4/start-image/";
    /**
     * themes, which can be subscribed
     */
    public static final String URL_THEMES = "http://news-at.zhihu.com/api/4/themes";
    /**
     * theme content,add theme id after url please,for example:http://news-at.zhihu.com/api/4/theme/11
     */
    public static final String URL_THEME_CONTENT = "http://news-at.zhihu.com/api/4/theme/";
    /**
     * latest articles in home page
     */
    public static final String URL_HOME_LATEST_STORIES = "http://news-at.zhihu.com/api/4/stories/latest";
    /**
     * last news before the specified date,add news id after url please,for example:http://news.at.zhihu.com/api/4/news/before/20160306
     */
    public static final String URL_HOME_LAST_STORIES = "http://news-at.zhihu.com/api/4/stories/before/";

    /**
     * news detail, add news id after url please, for example:http://news-at.zhihu.com/api/4/news/3892357
     */
    public static final String URL_STORY_DETAIL = "http://news-at.zhihu.com/api/4/story/";
    /**
     * extra story info,add story id after url please,for example:http://news-at.zhihu.com/api/4/story-extra/7983932
     */
    public static final String URL_STORY_DETAIL_EXTRA = "http://news-at.zhihu.com/api/4/story-extra/";
    /**
     * long comments, replace "%1$d" with story id please.
     */
    public static final String URL_STORY_LONG_COMMENTS = "http://news-at.zhihu.com/api/4/story/%1$d/long-comments";
    /**
     * short comments, replace "%1$d" with story id please.
     */
    public static final String URL_STORY_SHORT_COMMENTS = "http://news-at.zhihu.com/api/4/story/%1$d/short-comments";
}
