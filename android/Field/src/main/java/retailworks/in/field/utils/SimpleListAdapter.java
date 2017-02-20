package retailworks.in.field.utils;

/**
 * Created by Neiv on 10/17/2015.
 */


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import retailworks.in.field.R;

public class SimpleListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    String[] Heading;
    String[] SubText;
    int[] imgs;
    Context context;

    public SimpleListAdapter(Context ct, String[] headings, String[] values) {

        this.context = ct;
        this.Heading = headings;
        this.SubText = values;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public SimpleListAdapter(Context ct, String[] headings, int[] values) {

        this.context = ct;
        this.Heading = headings;
        this.imgs = values;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int idx, View rootView, ViewGroup paramViewGroup) {

        Holder hold = new Holder();

        if(rootView == null)
            rootView = inflater.inflate(R.layout.row_general, null);

        hold.headView = ((TextView) rootView.findViewById(R.id.GenHeading));
        if(Heading != null) {
            hold.headView.setText(this.Heading[idx]);
            hold.headView.setVisibility(View.VISIBLE);
            rootView.setTag(Heading[idx]);
        } else
            hold.headView.setVisibility(View.GONE);

        hold.valsView = ((TextView) rootView.findViewById(R.id.GenValue));
        if(SubText != null) {
            hold.valsView.setText(this.SubText[idx]);
            hold.valsView.setVisibility(View.VISIBLE);
            hold.headView.setPadding(0,20,0,0);
        }else {
            hold.valsView.setVisibility(View.GONE);
            hold.headView.setTextColor(Color.BLACK);
        }

        hold.img = ((ImageView) rootView.findViewById(R.id.GenImg));
        if(imgs != null) {
            hold.img.setImageResource(this.imgs[idx]);
            hold.img.setVisibility(View.VISIBLE);
        }else
            hold.img.setVisibility(View.GONE);

        return rootView;
    }


    public int getCount() {return this.Heading.length;}

    public Object getItem(int paramInt) {return Integer.valueOf(paramInt);}

    public long getItemId(int paramInt) {return paramInt;}

    public class Holder
    {
        TextView headView;
        ImageView img;
        TextView valsView;

        public Holder() {}
    }
}
