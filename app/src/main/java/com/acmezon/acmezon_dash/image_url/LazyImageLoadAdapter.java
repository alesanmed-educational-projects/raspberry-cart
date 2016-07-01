package com.acmezon.acmezon_dash.image_url;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.acmezon.acmezon_dash.CartPreviewActivity;
import com.acmezon.acmezon_dash.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LazyImageLoadAdapter extends BaseAdapter implements DialogInterface.OnClickListener {

    private Activity activity;
    private static LayoutInflater inflater = null;
    private List<JSONObject> products;
    public ImageLoader imageLoader;
    public Activity mainActivity;
    public Boolean valid;

    public LazyImageLoadAdapter(Activity a, List<JSONObject> products, Activity mainActivity) {
        activity = a;
        this.products = products;
        this.valid = isValid(products);
        this.mainActivity = mainActivity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    /*private view holder class*/
    private class ViewHolder {
        TextView name;
        ImageView image;
        TextView quantity;
        Button add,sub;
    }

    public JSONArray getProducts() {
        return new JSONArray(products);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public JSONObject getItem(int pos) {
        return products.get(pos);
    }

    public JSONObject remove(int pos) {
        return products.remove(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;

        if(convertView == null) {

            vi = inflater.inflate(R.layout.product_row, parent, false);

            holder = new ViewHolder();
            holder.name = (TextView) vi.findViewById(R.id.product_name);
            holder.image = (ImageView) vi.findViewById(R.id.product_image);
            holder.quantity = (TextView) vi.findViewById(R.id.product_quantity);
            holder.add = (Button) vi.findViewById(R.id.btn_add);
            holder.sub = (Button) vi.findViewById(R.id.btn_substract);

            if(activity.getClass().equals(CartPreviewActivity.class)) {
                holder.add.setVisibility(View.INVISIBLE);
                holder.sub.setVisibility(View.INVISIBLE);
            }

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        final JSONObject product = getItem(pos);
        assert product != null;
        int quantity;

        try {
            quantity = product.getInt("quantity");
            holder.name.setText(product.getString("name"));
            holder.quantity.setText(String.valueOf(quantity));
            holder.add.setTag(pos);
            holder.sub.setTag(pos);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ImageView image = holder.image;
        try {
            if (product.getBoolean("available")) {
                imageLoader.displayImage(activity.getApplicationContext().getString(
                        R.string.domain)
                        .concat("/img/")
                        .concat(product.getString("image")), image);
            } else {
                image.setImageResource(R.mipmap.not_available);
                holder.add.setEnabled(false);
                holder.sub.setEnabled(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Adds 1 to the counter
                try {
                    int quantity = product.getInt("quantity");
                    product.put("quantity", quantity + 1);
                    holder.quantity.setText(String.valueOf(product.getInt("quantity")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Adds 1 to the counter
                try {
                    int quantity = product.getInt("quantity");
                    if (quantity>1) {
                        product.put("quantity", quantity - 1);
                        holder.quantity.setText(String.valueOf(product.getInt("quantity")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        holder.name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        mainActivity)
                        .setTitle(mainActivity.getResources().getString(R.string.delete_title))
                        .setMessage(mainActivity.getResources().getString(R.string.delete_subtitle) + " " +
                        holder.name.getText() + "?")
                        .setPositiveButton(mainActivity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remove(pos);
                        notifyDataSetChanged();
                        dialog.dismiss();
                        LazyImageLoadAdapter.this.valid = isValid(products);
                    }
                })
                        .setNegativeButton(mainActivity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
                return false;
            }
        });
        if(!activity.getClass().equals(CartPreviewActivity.class)) {
            holder.name.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            mainActivity);
                    alert.setTitle(mainActivity.getResources().getString(R.string.delete_title));
                    alert.setMessage(mainActivity.getResources().getString(R.string.delete_subtitle) + " " +
                            holder.name.getText() + "?");
                    alert.setPositiveButton(mainActivity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            remove(pos);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton(mainActivity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                    return false;
                }
            });


            holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(
                            mainActivity)
                            .setIcon(R.drawable.warning)
                            .setTitle(mainActivity.getResources().getString(R.string.delete_title))
                            .setMessage(mainActivity.getResources().getString(R.string.delete_subtitle) + " " +
                                    holder.name.getText() + "?")
                            .setPositiveButton(mainActivity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    remove(pos);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                    LazyImageLoadAdapter.this.valid = isValid(products);
                                }
                            }).setNegativeButton(mainActivity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alert.show();
                    return false;
                }
            });
        }
        return vi;
    }

    private static Boolean isValid(List<JSONObject> products) {
        Boolean result = true;
        for (JSONObject p : products) {
            try {
                if (!p.getBoolean("available")) {
                    result = false;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}