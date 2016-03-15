package studio.uphie.zhihudaily.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Uphie on 2015/11/6.
 * Email: uphie7@gmail.com
 */
public class UListView extends ListView  {

    public UListView(Context context) {
        super(context);
    }

    public UListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
