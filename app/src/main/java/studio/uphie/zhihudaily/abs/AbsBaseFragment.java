package studio.uphie.zhihudaily.abs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.TextHttpResponseHandler;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import studio.uphie.zhihudaily.http.HttpClient;
import studio.uphie.zhihudaily.interfaces.IInit;
import studio.uphie.zhihudaily.utils.NetworkUtil;

/**
 * Created by Uphie on 2016/2/29 0029.
 * Email:uphie7@gmail.com
 */
public abstract class AbsBaseFragment extends Fragment implements IInit {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        if (layoutId == 0) {
            throw new IllegalStateException("Please specify root layout resource id for " + getClass().getSimpleName());
        } else {
            View parentView = inflater.inflate(layoutId, null);
            ButterKnife.bind(this, parentView);
            init();
            return parentView;
        }
    }

    @Override
    public void onNetworkUnavailable(String url) {

    }
    /**
     * GET请求
     *
     * @param url
     */
    public void get(final String url) {
        if (!NetworkUtil.getInstance().checkNetworkAvailable()){
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

    @Override
    public void onDataOK(String url, String data) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
