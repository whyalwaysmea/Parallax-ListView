package com.ithaha.parallaxlistview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * 头部会跟随缩小的ListView，并且带有下拉刷新
 * 下拉刷新使用的是 SwipeRefreshLayout
 * Created by Administrator on 2015/12/28.
 */
public class ParallaxListViewWithRefresh extends FrameLayout implements AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

	private ScrollView mScrollView;
	// 头部的View
	private LinearLayout mBackgroundLayout;

	private ImageView mHeaderImage;

	// 头部View的高度
	private int mHeaderHeight;
	// ListView
	private ListView mListView;
	// ListView的Apapter
	private BaseAdapter adapter;
	// 空白的View
	protected View mTransparentHeader;
	// 缩小的比例
	private float mParallaxFactor = 2;
	// 下拉刷新
	private SwipeRefreshLayout swipeRefresh;
	private int dividerHeight;
	private int dividerColor;

	public ParallaxListViewWithRefresh(Context context) {
		super(context);
		init(context, null);
	}

	public ParallaxListViewWithRefresh(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ParallaxListViewWithRefresh(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		if (dm.heightPixels > dm.widthPixels) {
			mHeaderHeight = dm.widthPixels;
		} else {
			mHeaderHeight = (int) (dm.heightPixels / 2f);
		}

		// 获取自定义属性中值
		if (attrs != null) {
			TypedArray a = context
					.obtainStyledAttributes(attrs, R.styleable.ParallaxListView);
			if (a != null) {
				try {
					dividerColor = a.getColor(
							R.styleable.ParallaxListView_dividerColor, Color.WHITE);

					dividerHeight = a.getDimensionPixelSize(
							R.styleable.ParallaxListView_dividerHeight, 1);

					mHeaderHeight = a.getDimensionPixelSize(
							R.styleable.ParallaxListView_headerHeight, mHeaderHeight);

					mParallaxFactor = a.getFloat(
							R.styleable.ParallaxListView_parallaxFactor, 2);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					a.recycle();
				}
			}
		}


		mScrollView = new ScrollView(context);
		mScrollView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeaderHeight));
		mScrollView.setVerticalScrollBarEnabled(false);
		addView(mScrollView);

		mBackgroundLayout = new LinearLayout(context);
		mBackgroundLayout.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		mBackgroundLayout.setOrientation(LinearLayout.VERTICAL);
		mScrollView.addView(mBackgroundLayout);

		mHeaderImage = new ImageView(context);
		mHeaderImage.setLayoutParams(new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, mHeaderHeight));
		mHeaderImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
		mBackgroundLayout.addView(mHeaderImage);

		mListView = new ListView(context);
		mListView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mListView.setOnScrollListener(this);
		// ListView属性设置
		mListView.setDivider(new ColorDrawable(dividerColor));
		mListView.setDividerHeight(dividerHeight);
		addView(mListView);

		mTransparentHeader = new View(context);
		mTransparentHeader.setBackgroundColor(Color.TRANSPARENT);
		mTransparentHeader.setLayoutParams(new AbsListView.LayoutParams(
				AbsListView.LayoutParams.MATCH_PARENT, mHeaderHeight));

	}

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
		mListView.setAdapter(new ActualAdapter());
	}

	public void setHeaderView(View view) {
		mBackgroundLayout.removeAllViews();
		mBackgroundLayout.addView(view);
	}

	public void setHeaderDrawable(Drawable drawable) {
		mHeaderImage.setImageDrawable(drawable);
	}

	@Override
	public void onScrollStateChanged(AbsListView absListView, int i) {
	}


	// 滑动的处理
	@Override
	public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		View firstChild = absListView.getChildAt(0);
		if (firstChild != null) {
			// When HeaderView is visible
			if (firstChild == mTransparentHeader) {
				// Top position of this view relative to its parent.
				int scrollY = -firstChild.getTop();

				if (scrollY == 0) {
					if (swipeRefresh == null) {
						swipeRefresh = (SwipeRefreshLayout) getParent();
					}
					swipeRefresh.setEnabled(true);
				} else {
					if (swipeRefresh == null) {
						swipeRefresh = (SwipeRefreshLayout) getParent();
					}
					swipeRefresh.setEnabled(false);
				}
				if (swipeRefresh != null) {
					swipeRefresh.setOnRefreshListener(this);
				}

				// The amount that the view is scaled in y around the pivot point, as a proportion of the view's unscaled height.
				if (mScrollView.getScrollY() != scrollY) {
					// move mScrollView
					mScrollView.scrollTo(0, (int) (scrollY / mParallaxFactor));

					// change mScrollView height
					ViewGroup.LayoutParams lp = mScrollView.getLayoutParams();
					lp.height = mHeaderHeight - scrollY;
					mScrollView.setLayoutParams(lp);
				}
			} else {
				mScrollView.scrollTo(0, mHeaderHeight);

				ViewGroup.LayoutParams lp = mScrollView.getLayoutParams();
				lp.height = 0;
				mScrollView.setLayoutParams(lp);

				if (swipeRefresh == null) {
					swipeRefresh = (SwipeRefreshLayout) getParent();
				}
				swipeRefresh.setEnabled(false);
			}
		}
	}

	@Override
	public void onRefresh() {
		if (onMyRefreshListener != null) {
			onMyRefreshListener.onRefresh();
		}
	}

	// 下拉刷新的回调接口
	interface OnMyRefreshListener {
		void onRefresh();
	}

	private OnMyRefreshListener onMyRefreshListener;

	public OnMyRefreshListener getOnMyRefreshListener() {
		return onMyRefreshListener;
	}

	public void setOnMyRefreshListener(OnMyRefreshListener onMyRefreshListener) {
		this.onMyRefreshListener = onMyRefreshListener;
	}

	private class ActualAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return adapter.getCount() + 1;
		}

		@Override
		public Object getItem(int pos) {
			return pos == 0 ? null : adapter.getItem(pos - 1);
		}

		@Override
		public long getItemId(int pos) {
			return pos == 0 ? 0 : adapter.getItemId(pos - 1);
		}

		@Override
		public View getView(int pos, View view, ViewGroup viewGroup) {
			if (pos == 0) {
				return mTransparentHeader;
			}

			if (view == mTransparentHeader) {
				view = null;
			}

			return adapter.getView(pos - 1, view, viewGroup);
		}
	}

	public ListView getListView() {
		return mListView;
	}


}