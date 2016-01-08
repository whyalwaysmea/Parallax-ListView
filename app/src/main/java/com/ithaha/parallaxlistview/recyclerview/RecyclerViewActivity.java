package com.ithaha.parallaxlistview.recyclerview;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ithaha.parallaxlistview.R;
import com.poliveira.parallaxrecycleradapter.ParallaxRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/8.
 */
public class RecyclerViewActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recyclerview);

		final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

		RecyclerView myRecycler = (RecyclerView) findViewById(R.id.myRecycler);
		LinearLayoutManager manager = new LinearLayoutManager(this);
		manager.setOrientation(LinearLayoutManager.VERTICAL);
		myRecycler.setLayoutManager(manager);
		myRecycler.setHasFixedSize(true);

		final List<String> content = new ArrayList<>();
		for (int i = 0; i < 30; i++)
			content.add(getListString(i));


		ParallaxRecyclerAdapter<String> stringAdapter = new ParallaxRecyclerAdapter<>(content);
		stringAdapter.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter.RecyclerAdapterMethods() {
			@Override
			public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
				((TextView) viewHolder.itemView).setText(content.get(i));
			}

			@Override
			public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
				return new SimpleViewHolder(getLayoutInflater().inflate(android.R.layout.simple_list_item_1, viewGroup, false));
			}

			@Override
			public int getItemCount() {
				return content.size();
			}


		});

		stringAdapter.setParallaxHeader(getLayoutInflater().inflate(R.layout.my_header, myRecycler, false), myRecycler);
		stringAdapter.setOnParallaxScroll(new ParallaxRecyclerAdapter.OnParallaxScroll() {
			@Override
			public void onParallaxScroll(float v, float v1, View view) {
				Drawable c = mToolbar.getBackground();
				c.setAlpha(Math.round(v * 255));
				mToolbar.setBackground(c);
			}
		});
		myRecycler.setAdapter(stringAdapter);
	}

	static class SimpleViewHolder extends RecyclerView.ViewHolder {

		public SimpleViewHolder(View itemView) {
			super(itemView);
		}


	}

	public String getListString(int position) {
		return position + " - android";
	}

}
