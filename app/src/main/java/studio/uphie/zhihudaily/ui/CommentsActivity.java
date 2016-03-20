package studio.uphie.zhihudaily.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import studio.uphie.zhihudaily.R;
import studio.uphie.zhihudaily.abs.AbsBaseActivity;
import studio.uphie.zhihudaily.abs.AbsBaseAdapter;
import studio.uphie.zhihudaily.abs.AbsBaseOnItemClickListener;
import studio.uphie.zhihudaily.http.Api;
import studio.uphie.zhihudaily.utils.ImageUtil;
import studio.uphie.zhihudaily.utils.JsonUtil;
import studio.uphie.zhihudaily.utils.TimeUtil;
import studio.uphie.zhihudaily.widgets.UListView;

/**
 * Created by Uphie on 2016/3/13 0013.
 * Email:uphie7@gmail.com
 */
public class CommentsActivity extends AbsBaseActivity {

    @Bind(R.id.common_toolbar)
    Toolbar toolbar;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.lv_long_comments)
    UListView lv_longComments;
    @Bind(R.id.lv_short_comments)
    UListView lv_shortComments;
    @Bind(R.id.tv_long_comment)
    TextView tv_long_comment;
    @Bind(R.id.tv_short_comment)
    TextView tv_short_comment;

    private int story_id;
    private int short_comments_num;
    private int long_comments_num;
    private int comments_num;

    private CommentAdapter longCommentAdapter;
    private CommentAdapter shortCommentAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_comments;
    }

    @Override
    public void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back));

        longCommentAdapter = new CommentAdapter(this);
        shortCommentAdapter = new CommentAdapter(this);

        lv_longComments.setAdapter(longCommentAdapter);
        lv_shortComments.setAdapter(shortCommentAdapter);

        story_id = getIntent().getIntExtra("story_id", 0);
        comments_num = getIntent().getIntExtra("comments", 0);
        short_comments_num = getIntent().getIntExtra("short_comments", 0);
        long_comments_num = getIntent().getIntExtra("long_comments", 0);

        toolbar.setTitle(String.format(getResources().getString(R.string.comments_title), comments_num));
        tv_long_comment.setText(String.format(getString(R.string.long_comment), long_comments_num));
        tv_short_comment.setText(String.format(getString(R.string.short_comment), short_comments_num));

        if (story_id != 0) {
            get(String.format(Api.URL_STORY_LONG_COMMENTS, story_id));
        }
    }

    @Override
    public void onDataOK(String url, String data) {
        if (url.contains("long")) {
            CommentData commentData = JsonUtil.getEntity(data, CommentData.class);
            if (commentData != null) {
                longCommentAdapter.updateAll(commentData.comments);
            }
        }
        if (url.contains("short")) {
            CommentData commentData = JsonUtil.getEntity(data, CommentData.class);
            if (commentData != null) {
                shortCommentAdapter.updateAll(commentData.comments);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_write_comment:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick(R.id.tv_short_comment)
    public void onClick() {
        if (shortCommentAdapter.getCount() == 0) {
            tv_short_comment.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.comment_icon_expand), null);
            get(String.format(Api.URL_STORY_SHORT_COMMENTS, story_id));
            scrollView.scrollTo(0, (int) tv_short_comment.getY());
        } else {
            tv_short_comment.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.comment_icon_expand), null);
            shortCommentAdapter.updateAll(new ArrayList<Comment>());
            scrollView.scrollTo(0, 0);
        }
    }

    class CommentData {
        List<Comment> comments;
    }

    class Comment {
        boolean own;
        String author;
        String content;
        String avatar;
        long time;
        boolean voted;
        long id;
        //num of likes
        long likes;
        //may not exist
        ReplyTo reply_to;

        class ReplyTo {
            String content;
            int status;
            long id;
            String author;
        }
    }

    private class CommentAdapter extends AbsBaseAdapter<Comment> {

        public CommentAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemLayoutID() {
            return R.layout.list_item_comment;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public View getItemView(int position, View convertView, ViewHolder holder) {
            Comment comment = getItem(position);

            SimpleDraweeView sd_avatar = holder.findView(R.id.list_item_comment_user_avatar);
            TextView tv_nick = holder.findView(R.id.list_item_comment_user_nick);
            TextView tv_content = holder.findView(R.id.list_item_comment_content);
            TextView tv_reply_to = holder.findView(R.id.list_item_comment_reply_to);
            TextView tv_time = holder.findView(R.id.list_item_comment_time);
            TextView tv_vote = holder.findView(R.id.list_item_comment_vote);
            TextView tv_expand = holder.findView(R.id.list_item_comment_expand_content);

            ImageUtil.displayImage(comment.avatar, sd_avatar);
            tv_nick.setText(comment.author);
            tv_content.setText(comment.content);
            tv_time.setText(TimeUtil.getTime(comment.time));
            if (comment.voted) {
                tv_vote.setCompoundDrawablesRelativeWithIntrinsicBounds(getResources().getDrawable(R.drawable.comment_voted), null, null, null);
            } else {
                tv_vote.setCompoundDrawablesRelativeWithIntrinsicBounds(getResources().getDrawable(R.drawable.comment_vote), null, null, null);
            }
            String vote_num = comment.likes > 1000 ? new DecimalFormat("#.0").format(((double) comment.likes) / 1000) + "K" : comment.likes + "";
            tv_vote.setText(vote_num);

            if (comment.reply_to != null) {
                if (TextUtils.isEmpty(comment.reply_to.author)||TextUtils.isEmpty(comment.reply_to.content)){
                    tv_reply_to.setText(R.string.comment_deleted);
                    tv_reply_to.setTextColor(getColor(R.color.text_gray));
                    tv_reply_to.setBackgroundColor(getColor(R.color.background));
                }else {
                    SpannableString spannableString = new SpannableString("//" + comment.reply_to.author + "：" + comment.reply_to.content);
                    StyleSpan style = new StyleSpan(Typeface.BOLD);
                    spannableString.setSpan(style, 0, comment.reply_to.author.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ForegroundColorSpan foreground = new ForegroundColorSpan(Color.BLACK);
                    spannableString.setSpan(foreground, 0, comment.reply_to.author.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    foreground = new ForegroundColorSpan(getResources().getColor(R.color.text_gray));
                    spannableString.setSpan(foreground, comment.reply_to.author.length() + 3, spannableString.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    tv_reply_to.setText(spannableString);
                }

            }
            tv_vote.setOnClickListener(new ClickListener(comment));
            tv_expand.setOnClickListener(new ClickListener(comment));
            return convertView;
        }

        @Override
        public void getInfo(Comment data) {

        }

        class ClickListener extends AbsBaseOnItemClickListener<Comment> {

            public ClickListener(Comment data) {
                super(data);
            }

            @Override
            public void onClick(View view, Comment data) {
                switch (view.getId()) {
                    case R.id.list_item_comment_vote:
                        data.voted = !data.voted;
                        if (data.voted) {
                            data.likes++;
                        } else {
                            data.likes--;
                        }
                        notifyDataSetChanged();
                        break;
                    case R.id.list_item_comment_expand_content:

                        break;
                }
            }
        }
    }
}
