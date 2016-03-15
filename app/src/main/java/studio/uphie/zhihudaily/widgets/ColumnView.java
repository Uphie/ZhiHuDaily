package studio.uphie.zhihudaily.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import studio.uphie.zhihudaily.R;
import studio.uphie.zhihudaily.ui.MainActivity;
import studio.uphie.zhihudaily.utils.SysUtil;

/**
 * Created by Uphie on 2016/2/26 0026.
 * Email:uphie7@gmail.com
 */
public class ColumnView extends TextView {

    private Context context;
    private boolean subscribed;
    private OnColumnHandleListener onColumnHandleListener;

    public ColumnView(Context context) {
        super(context);
        init(context);
    }

    public ColumnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context ctx) {
        this.context = ctx;
        Drawable drawable;
        if (isSubscribed()) {
            drawable = context.getResources().getDrawable(R.drawable.menu_arrow);
        } else {
            drawable = context.getResources().getDrawable(R.drawable.menu_follow);
        }
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        int padding= SysUtil.dp2px(context,15);
        int paddingRight= SysUtil.dp2px(context,40);
        setPadding(padding,padding,paddingRight,padding);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isSubscribed()) {
                //view column
                if (onColumnHandleListener != null) {
                    onColumnHandleListener.onViewColumn();
                }
            } else {
                if (event.getX() > getWidth() - getPaddingRight() - getCompoundDrawables()[2].getIntrinsicWidth()) {
                    //follow column
                    if (onColumnHandleListener != null) {
                        if (onColumnHandleListener.onAddColumn()) {
                            Drawable drawable = context.getResources().getDrawable(R.drawable.menu_arrow);
                            setCompoundDrawables(null, null, drawable, null);
                            setSubscribed(true);
                        }
                    }
                }else {
                    if (onColumnHandleListener != null) {
                        onColumnHandleListener.onViewColumn();
                    }
                }
            }
        }
        return true;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
        Drawable drawable;
        if (isSubscribed()) {
            drawable= context.getResources().getDrawable(R.drawable.menu_arrow);
        } else {
            drawable = context.getResources().getDrawable(R.drawable.menu_follow);
        }
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
    }

    public void setOnColumnHandleListener(OnColumnHandleListener listener) {
        this.onColumnHandleListener = listener;
    }

    public interface OnColumnHandleListener {
        /**
         * add column
         *
         * @return whether column is added
         */
        boolean onAddColumn();

        /**
         * view column
         */
        void onViewColumn();
    }
}
