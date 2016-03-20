package studio.uphie.zhihudaily.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import studio.uphie.zhihudaily.R;
import studio.uphie.zhihudaily.abs.AbsBaseActivity;
import studio.uphie.zhihudaily.http.Api;
import studio.uphie.zhihudaily.interfaces.OnSetTitleListener;
import studio.uphie.zhihudaily.utils.JsonUtil;
import studio.uphie.zhihudaily.utils.TextToast;
import studio.uphie.zhihudaily.widgets.ColumnView;

/**
 * Created by Uphie on 2016/2/26 0026.
 * Email:uphie7@gmail.com
 */
public class MainActivity extends AbsBaseActivity implements OnSetTitleListener {

    @Bind(R.id.common_toolbar)
    Toolbar toolbar;
    @Bind(R.id.lv_columns)
    ListView lv_columns;
    @Bind(R.id.home_drawer)
    DrawerLayout drawer_home;

    TextView tv_home;

    private ColumnAdapter columnAdapter;
    private boolean isTheme = false;
    private boolean isSubscribed = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {
        setSupportActionBar(toolbar);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_fragment, new HomeFragment());
        transaction.commit();

        getSupportActionBar().setTitle(getString(R.string.home));

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer_home, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawer_home.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        columnAdapter = new ColumnAdapter();
        lv_columns.setAdapter(columnAdapter);

        View header = View.inflate(this, R.layout.layout_meau_header, null);
        header.findViewById(R.id.sd_avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                drawer_home.closeDrawers();
            }
        });
        header.findViewById(R.id.tv_nickname).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                drawer_home.closeDrawers();
            }
        });
        header.findViewById(R.id.tv_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                drawer_home.closeDrawers();
            }
        });
        header.findViewById(R.id.tv_offline_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextToast.longShow("暂未开启此功能");
            }
        });
        tv_home = (TextView) header.findViewById(R.id.tv_home);
        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_fragment, new HomeFragment());
                transaction.commit();

                tv_home.setBackgroundColor(getResources().getColor(R.color.selected_gray));
                drawer_home.closeDrawers();
                getSupportActionBar().setTitle(getString(R.string.home));
            }
        });
        lv_columns.addHeaderView(header);

        //encrypt analytic info
        AnalyticsConfig.enableEncrypt(true);
        //forbidden check config,  while it does not work!
        UmengUpdateAgent.setUpdateCheckConfig(false);
        //check for update
        UmengUpdateAgent.update(this);
        //forbidden delta update
        UmengUpdateAgent.setDeltaUpdate(false);
        //disable popup
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int status, UpdateResponse updateResponse) {
                switch (status) {
                    case UpdateStatus.Yes:
                        showUpdateDialog(updateResponse);
                        break;
                    case UpdateStatus.No:
                        break;
                    case UpdateStatus.NoneWifi:
                        break;
                    case UpdateStatus.Timeout:
                        break;
                }
            }
        });
        get(Api.URL_THEMES);
    }

    @Override
    public void onDataOK(String url, String data) {
        if (url.equals(Api.URL_THEMES)) {
            ThemeData themeData = JsonUtil.getEntity(data, ThemeData.class);
            if (themeData != null) {
                List<UserTheme> themeList = new ArrayList<>();
                for (ThemeData.Theme theme : themeData.subscribed) {
                    themeList.add(new UserTheme(true, theme));
                }
                for (ThemeData.Theme theme : themeData.others) {
                    themeList.add(new UserTheme(false, theme));
                }
                columnAdapter.updateAll(themeList);
            }
        }
    }

    /**
     * inflate menu on the top right
     *
     * @param menu menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isTheme) {
            menu.clear();
            menu.add(0, R.id.menu_subscribe, 10, "");
            if (isSubscribed) {
                menu.findItem(R.id.menu_subscribe).setIcon(getResources().getDrawable(R.drawable.theme_add));
            } else {
                menu.findItem(R.id.menu_subscribe).setIcon(getResources().getDrawable(R.drawable.theme_remove));
            }
            return true;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_msg:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.menu_night_mode:
                TextToast.longShow("暂未开启此功能");
                break;
            case R.id.menu_preferences:
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
            case R.id.menu_subscribe:
                isSubscribed = !isSubscribed;
                invalidateOptionsMenu();
                TextToast.longShow("注：该操作并不能真正订阅");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawer_home.isDrawerOpen(Gravity.LEFT)) {
                drawer_home.closeDrawers();

                return true;
            }else {
                if (isTheme){
                    //return from theme to home
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_fragment, new HomeFragment());
                    transaction.commit();
                    isTheme=false;

                    for(UserTheme userTheme:columnAdapter.list){
                        userTheme.selected=false;
                    }
                    columnAdapter.notifyDataSetChanged();
                    tv_home.setBackgroundColor(getResources().getColor(R.color.selected_gray));
                    invalidateOptionsMenu();

                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSetTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private void showUpdateDialog(final UpdateResponse updateResponse) {
        View view = View.inflate(this, R.layout.dialog_update, null);
        TextView content = (TextView) view.findViewById(R.id.dialog_update_content);
        TextView cancel = (TextView) view.findViewById(R.id.dialog_update_cancel);
        TextView ok = (TextView) view.findViewById(R.id.dialog_update_ok);

        content.setText(String.format(getResources().getString(R.string.label_update_content), updateResponse.version, updateResponse.updateLog));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //开始下载
                File file = UmengUpdateAgent.downloadedFile(MainActivity.this, updateResponse);
                if (file == null) {
                    //若未下载，下载
                    UmengUpdateAgent.startDownload(MainActivity.this, updateResponse);
                } else {
                    // 已经下载，直接安装
                    UmengUpdateAgent.startInstall(MainActivity.this, file);
                }
            }
        });

        dialog.show();
    }

    class ColumnAdapter extends BaseAdapter {
        private List<UserTheme> list = new ArrayList<>();

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list == null ? null : list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserTheme userTheme = list.get(position);
            ColumnView columnView;
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.list_item_column, null);
                columnView = (ColumnView) convertView;
                convertView.setTag(columnView);
            } else {
                columnView = (ColumnView) convertView.getTag();
            }

            if (userTheme.selected) {
                columnView.setBackgroundColor(getResources().getColor(R.color.selected_gray));
            } else {
                columnView.setBackgroundColor(getResources().getColor(android.R.color.white));
            }
            columnView.setSubscribed(userTheme.subscribed);
            columnView.setText(userTheme.theme.name);
            columnView.setOnColumnHandleListener(new HandleColumnListener(userTheme));
            return convertView;
        }

        public void updateAll(List<UserTheme> data) {
            list.clear();
            list.addAll(data);
            notifyDataSetChanged();
        }

        private class HandleColumnListener implements ColumnView.OnColumnHandleListener {
            private UserTheme userTheme;

            public HandleColumnListener(UserTheme userTheme) {
                this.userTheme = userTheme;
            }

            @Override
            public boolean onAddColumn() {
                userTheme.subscribed = true;
                notifyDataSetChanged();
                TextToast.shortShow("注：不能真正的订阅");
                return true;
            }

            @Override
            public void onViewColumn() {
                ThemeFragment themeFragment = new ThemeFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("theme_id", userTheme.theme.id);
                themeFragment.setArguments(bundle);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content_fragment, themeFragment);
                transaction.commit();

                getSupportActionBar().setTitle(userTheme.theme.name);
                drawer_home.closeDrawers();
                tv_home.setBackgroundColor(getResources().getColor(android.R.color.white));

                isSubscribed = userTheme.subscribed;
                isTheme = true;
                userTheme.selected = true;
                for (UserTheme temp : list) {
                    temp.selected = temp == userTheme;
                }
                invalidateOptionsMenu();
                notifyDataSetChanged();
            }
        }

        @Override
        public void notifyDataSetChanged() {
            Collections.sort(list, comparator);
            super.notifyDataSetChanged();
        }
    }

    Comparator<UserTheme> comparator = new Comparator<UserTheme>() {
        @Override
        public int compare(UserTheme lhs, UserTheme rhs) {
            if (lhs.subscribed && !rhs.subscribed){
                return -1;
            }
            if (!lhs.subscribed&&rhs.subscribed){
                return 1;
            }
            return  0;
        }
    };

    @Override
    public void finish() {
        MobclickAgent.onKillProcess(this);
        super.finish();
    }

    class ThemeData {
        int limit;
        List<Theme> subscribed;
        List<Theme> others;

        class Theme {
            int id;
            int color;
            String thumbnail;
            String description;
            String name;
        }
    }

    public class UserTheme {
        boolean selected = false;
        boolean subscribed;
        ThemeData.Theme theme;

        UserTheme(boolean subscribed, ThemeData.Theme theme) {
            this.subscribed = subscribed;
            this.theme = theme;
        }
    }
}
