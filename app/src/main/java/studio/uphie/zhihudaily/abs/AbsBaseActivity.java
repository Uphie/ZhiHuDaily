package studio.uphie.zhihudaily.abs;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import studio.uphie.zhihudaily.http.HttpClient;
import studio.uphie.zhihudaily.interfaces.DownloadListener;
import studio.uphie.zhihudaily.interfaces.IInit;
import studio.uphie.zhihudaily.utils.NetworkUtil;


/**
 * Created by Uphie on 2016/2/26 0026.
 * Email:uphie7@gmail.com
 */
public abstract class AbsBaseActivity extends AppCompatActivity implements IInit {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = getLayoutId();
        if (layout == 0) {
            throw new IllegalStateException("Please specify root layout resource id for " + getClass().getSimpleName());
        } else {
            setContentView(layout);
            ButterKnife.bind(this);
            init();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                progressDialog=new ProgressDialog(this,android.R.style.Theme_Material_Light_Dialog_Alert);
            }else {
                progressDialog=new ProgressDialog(this,ProgressDialog.THEME_HOLO_LIGHT);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDataOK(String url, String data) {

    }

    @Override
    public void onNetworkUnavailable(String url) {

    }

    public void showProgress(){
        if (progressDialog!=null&&!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    public void closeProgress(){
        if (progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
    /**
     * GET请求
     *
     * @param url
     */
    public void get(final String url) {
        if (!NetworkUtil.getInstance().checkNetworkAvailable()) {
            onNetworkUnavailable(url);
            return;
        }
        HttpClient.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (statusCode == 200) {
                    onDataOK(url, responseString);
                }
            }
        });
    }

    public void downloadImage(String url, final String file, final DownloadListener listener) {
        listener.onStart();
        //using https may cause exceptions,convert to http
        if (url.startsWith("https")){
            url=url.replace("https","http");
        }
        HttpClient.downloadFile(url, new BinaryHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                        if (statusCode == 200 && binaryData != null && binaryData.length > 0) {
                            boolean success = saveImg(binaryData, file);
                            if (success) {
                                listener.onFinish();
                            } else {
                                listener.onFail();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                        listener.onFail();
                    }
                }

        );
    }

    private boolean saveImg(byte[] binaryData, String savePath) {
        Bitmap bmp = BitmapFactory.decodeByteArray(binaryData, 0,
                binaryData.length);
        File file = new File(savePath);
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        try {
            if (file.createNewFile()) {
                OutputStream stream = new FileOutputStream(file);
                bmp.compress(format, quality, stream);
                stream.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
