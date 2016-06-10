package com.acmezon.acmezon_dash.image_url;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.acmezon.acmezon_dash.R;

public class LazyImageLoadAdapter extends BaseAdapter implements OnClickListener{

    private Activity activity;
    private String[] names;
    private String[] imageUrls;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    public Activity mainActivity;

    public LazyImageLoadAdapter(Activity a, String[] names, String[] imageUrls, Activity mainActivity) {
        activity = a;
        this.names = names;
        this.imageUrls = imageUrls;
        if (names.length != imageUrls.length) {
            throw new IllegalArgumentException("Names and Images array not same dimension");
        }
        this.mainActivity = mainActivity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @Override
    public Row getItem(int pos) {
        return new Row(names[pos], imageUrls[pos]);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    public static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView == null) {

            vi = inflater.inflate(R.layout.product_row, null);

            holder = new ViewHolder();

            holder.text = (TextView) vi.findViewById(R.id.product_name);
            holder.image = (ImageView) vi.findViewById(R.id.product_image);

            vi.setTag(holder);

        } else {
            holder = (ViewHolder) vi.getTag();
        }

        Row item = getItem(pos);
        holder.text.setText(item.getName());
        ImageView image = holder.image;

        imageLoader.displayImage(item.getUrl(), image);

        return vi;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }
}