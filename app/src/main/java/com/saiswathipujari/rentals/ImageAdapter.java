package com.saiswathipujari.rentals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by saiswathipujari on 9/17/15.
 */
public class ImageAdapter extends ArrayAdapter<MyListItem> {

    /**
     * applying ViewHolder pattern to speed up ListView, smoother and faster
     * item loading by caching view in A ViewHolder object
     */
    private static class ViewHolder {
        ImageView imgIcon;
        TextView price;
        TextView desc;
        TextView locality;
        TextView listing_id;

    }

    public ImageAdapter(Context context, ArrayList<MyListItem> items) {
        super(context, 0, items);
    }

    @Override public View getView(int position, View convertView,
                                  ViewGroup parent) {
        // view lookup cache stored in tag
        ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the
        // item view
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.info_window, parent, false);

            viewHolder.price = (TextView) convertView.findViewById(R.id.aptPriceBeds);
            viewHolder.desc = (TextView) convertView.findViewById(R.id.apt_desc);
            viewHolder.locality = (TextView) convertView.findViewById(R.id.tv_snippet);
            viewHolder.imgIcon = (ImageView) convertView.findViewById(R.id.imageView1);
            viewHolder.listing_id = (TextView) convertView.findViewById(R.id.listing_id);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Get the data item for this position
        MyListItem item = getItem(position);
        // set description text

        viewHolder.price.setText(item.getPrice());
        viewHolder.desc.setText(item.getDesc());
        viewHolder.locality.setText(item.getLocality());
        viewHolder.listing_id.setText(item.getListing_id());
        // set image icon

        byte[] img= item.getImg();

        Bitmap b1=BitmapFactory.decodeByteArray(img, 0, img.length);

        viewHolder.imgIcon.setImageBitmap(b1);

        // Return the completed view to render on screen
        return convertView;
    }
}
