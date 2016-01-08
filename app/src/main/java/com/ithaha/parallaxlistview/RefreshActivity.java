package com.ithaha.parallaxlistview;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RefreshActivity extends Activity {

    private static String[] sSongList = new String[] {
            "Mozart's House", "Extraordinary", "Dust Clears", "Rather Be", "A+E", "Come Over",
            "Cologne", "Telephone Banking", "Up Again", "Heart On Fire", "New Eyes", "Birch",
            "Outro Movement III", "Rihanna", "UK Shanty", "Nightingale"
    };

    private ParallaxListViewWithRefresh mParallaxListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);

        mParallaxListView = (ParallaxListViewWithRefresh) findViewById(R.id.parallax_listview);
        mParallaxListView.setAdapter(new ListAdapter());

        LinearLayout headerView = (LinearLayout) this.getLayoutInflater().inflate(R.layout.header_view, null);
        mParallaxListView.setHeaderView(headerView);

        mParallaxListView.setOnMyRefreshListener(new ParallaxListViewWithRefresh.OnMyRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(RefreshActivity.this, "刷新...", Toast.LENGTH_SHORT).show();
            }
        });

//        mParallaxListView.setHeaderDrawable(getResources().getDrawable(R.mipmap.header));

    }

    private static class ViewHolder {
        private TextView textView;
    }

    private class ListAdapter extends BaseAdapter {

        @Override public int getCount() {
            return sSongList.length;
        }

        @Override public Object getItem(int i) {
            return sSongList[i];
        }

        @Override public long getItemId(int i) {
            return i;
        }

        @Override public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder vh;
            if (view == null) {
                LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.list_item, viewGroup, false);

                vh = new ViewHolder();
                vh.textView = (TextView) view.findViewById(R.id.list_item_text);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }

            vh.textView.setText(sSongList[i]);

            return view;
        }
    }
}
