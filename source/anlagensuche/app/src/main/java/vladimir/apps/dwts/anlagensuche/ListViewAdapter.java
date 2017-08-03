package vladimir.apps.dwts.anlagensuche;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * WEA Suche
 *
 * @author
 *      Vladimir (jelezarov.vladimir@gmail.com)
 */

class ListViewAdapter extends BaseAdapter {
    // Declare Variables
    private Context mContext;
    private LayoutInflater inflater;
    private List<dataWEA> WEAlist = null;
    private ArrayList<dataWEA> arraylist;

    ListViewAdapter(Context context, List<dataWEA> WEAlist) {
        mContext = context;
        this.WEAlist = WEAlist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<dataWEA>();
        this.arraylist.addAll(WEAlist);
    }
    private class ViewHolder {
        TextView WEA;
    }
    @Override
    public int getCount() {
        return WEAlist.size();
    }
    @Override
    public dataWEA getItem(int position) {
        return WEAlist.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.mylistlayout, null);
            // Locate the TextViews in listview_item.xml
            holder.WEA = (TextView) view.findViewById(R.id.text1);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.WEA.setText(WEAlist.get(position).getDesc());

        // Listen for ListView Item Click
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String br = WEAlist.get(position).getBreit();
                String la = WEAlist.get(position).getLang();
                if (br.length()==0 || la.length() == 0) return;
                Intent intent = new Intent(mContext, viewSingle.class);
                intent.putExtra("breit",br);
                intent.putExtra("lang",la);
                intent.putExtra("desc",(WEAlist.get(position).getDesc()));
                mContext.startActivity(intent);
            }
        });

        return view;
    }
    // Filter Class
    void filter(String charText) {
        charText = charText.toLowerCase();
        WEAlist.clear();
        if (charText.length() == 0) {
            WEAlist.addAll(arraylist);
        }
        else
        {
            for (dataWEA wp : arraylist)
            {
                if (wp.getDesc().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    WEAlist.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
