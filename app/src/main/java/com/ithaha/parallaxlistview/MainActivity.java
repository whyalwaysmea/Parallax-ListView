package com.ithaha.parallaxlistview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ithaha.parallaxlistview.recyclerview.RecyclerViewActivity;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button commonParallax = (Button) findViewById(R.id.common_parallax);
		Button refreshParallax = (Button) findViewById(R.id.refresh_parallax);
		Button recyclerView = (Button) findViewById(R.id.recycler_view);


		commonParallax.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, CommonActivity.class));
			}
		});

		refreshParallax.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, RefreshActivity.class));
			}
		});

		recyclerView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, RecyclerViewActivity.class));
			}
		});
	}
}
