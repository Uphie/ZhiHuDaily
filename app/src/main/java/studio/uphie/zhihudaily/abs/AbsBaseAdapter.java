package studio.uphie.zhihudaily.abs;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsBaseAdapter<T> extends BaseAdapter {

    protected Context context;
    protected List<T> data;

    public AbsBaseAdapter(Context context, List<T> data) {
        this.context = context;
        this.data = data == null ? new ArrayList<T>() : data;
    }

    public AbsBaseAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<T>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        if (position >= data.size()) {
            return null;
        }
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, getItemLayoutID(), null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new Listener(getItem(position)));
        return getItemView(position, convertView, holder);
    }

    /**
     * 获得Item布局id
     *
     * @return
     * @author 史永飞
     */
    public abstract int getItemLayoutID();

    /**
     * 获得Item布局，子类实现，替换原来的getView方法
     *
     * @param position
     * @param convertView
     * @param holder
     * @return
     * @author 史永飞
     */
    public abstract View getItemView(int position, View convertView,
                                     ViewHolder holder);

    /**
     * 获取某一条数据
     *
     * @param data
     */
    public abstract void getInfo(T data);

    public class ViewHolder {
        private SparseArray<View> views = new SparseArray<View>();
        private View convertView;

        public ViewHolder(View convertView) {
            this.convertView = convertView;
        }

        public <T extends View> T findView(int resId) {
            View v = views.get(resId);
            if (v == null) {
                v = convertView.findViewById(resId);
                views.put(resId, v);
            }
            return (T) v;
        }
    }

    /**
     * 添加一组数据
     *
     * @param elems
     */
    public void add(List<T> elems) {
        data.addAll(elems);
        notifyDataSetChanged();
    }

    /**
     * 添加一条数据
     *
     * @param elem
     */
    public void add(T elem) {
        data.add(elem);
        notifyDataSetChanged();
    }

    /**
     * 删除一条数据
     *
     * @param elem
     */
    public void remove(T elem) {
        data.remove(elem);
        notifyDataSetChanged();
    }

    /**
     * 删除一组数据
     *
     * @param elems
     */
    public void remove(List<T> elems) {
        data.remove(elems);
        notifyDataSetChanged();
    }

    public void update(int index, T elem) {
        data.remove(index);
        data.add(index, elem);
        notifyDataSetChanged();
    }

    /**
     * 更新全部数据
     *
     * @param elems 新的数据
     */
    public void updateAll(List<T> elems) {
        data.clear();
        if (elems != null) {
            data.addAll(elems);
        }
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return data;
    }

    private class Listener extends AbsBaseOnItemClickListener<T> {

        public Listener(T data) {
            super(data);
        }

        @Override
        public void onClick(View view, T data) {
            getInfo(data);
        }

    }

}
