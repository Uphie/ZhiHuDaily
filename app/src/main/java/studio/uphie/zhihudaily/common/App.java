package studio.uphie.zhihudaily.common;

import android.app.Application;
import android.widget.TextView;

import studio.uphie.zhihudaily.http.HttpClient;
import studio.uphie.zhihudaily.utils.FileManager;
import studio.uphie.zhihudaily.utils.ImageUtil;
import studio.uphie.zhihudaily.utils.NetworkUtil;
import studio.uphie.zhihudaily.utils.TextToast;

/**
 * Created by Uphie on 2016/2/26 0026.
 * Email:uphie7@gmail.com
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HttpClient.init(this);
        ImageUtil.init(this, FileManager.getHomeDir());
        TextToast.init(this);
        NetworkUtil.init(this);
        CrashHandler.getInstance().init(this);
    }
}
