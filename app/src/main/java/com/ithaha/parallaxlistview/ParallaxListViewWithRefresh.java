package com.ithaha.parallaxlistview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 *
 * Created by Administrator on 2015/12/28.
 */
public class ParallaxListViewWithRefresh extends FrameLayout implements AbsListView.OnScrollListener {

    private ScrollView mScrollView;
    private LinearLayout mBackgroundLayout;
    // 头部的View
    private LinearLayout mHeaderView;
    // 头部的高
    private int mHeaderHeight;

    private ListView mListView;
    protected View mTransparentHeader;

    private float mParallaxFactor = 2;

    private BaseAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;

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

        mScrollView = new ScrollView(context);
        mScrollView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeaderHeight));
        mScrollView.setVerticalScrollBarEnabled(false);
        addView(mScrollView);

        mBackgroundLayout = new LinearLayout(context);
        mBackgroundLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mBackgroundLayout.setOrientation(LinearLayout.VERTICAL);
        mScrollView.addView(mBackgroundLayout);

        mHeaderView = new LinearLayout(context);
        mHeaderView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, mHeaderHeight));
        mBackgroundLayout.addView(mHeaderView);

        mListView = new ListView(context);
        mListView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mListView.setOnScrollListener(this);
        addView(mListView);

        mTransparentHeader = new View(context);
        mTransparentHeader.setBackgroundColor(Color.TRANSPARENT);
        mTransparentHeader.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, mHeaderHeight));

        if (attrs != null) {
            TypedArray a = context
                    .obtainStyledAttributes(attrs, R.styleable.ParallaxListView, 0, 0);
            Resources res = getResources();
            if (a != null && res != null) {
                try {
                    int dividerColor = a.getColor(
                            R.styleable.ParallaxListView_divider_color, Color.WHITE);
                    mListView.setDivider(new ColorDrawable(dividerColor));

                    int dividerHeight = a.getDimensionPixelSize(
                            R.styleable.ParallaxListView_divider_height, 1);
                    mListView.setDividerHeight(dividerHeight);

                    mHeaderHeight = a.getDimensionPixelSize(
                            R.styleable.ParallaxListView_header_height, mHeaderHeight);

                    mParallaxFactor = a.getFloat(
                            R.styleable.ParallaxListView_parallax_factor, 2);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    a.recycle();
                }
            }
        }
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        mListView.setAdapter(new ActualAdapter());
    }

    public void setHeaderView(View view) {
        mHeaderView.addView(view);
    }

    @Override public void onScrollStateChanged(AbsListView absListView, int i) {
    }


    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        View firstChild = absListView.getChildAt(0);
        if (firstChild != null) {
            // When HeaderView is visible
            if (firstChild == mTransparentHeader) {
                // Top position of this view relative to its parent.
                int scrollY = -firstChild.getTop();

                if(scrollY == 0) {
                    if(swipeRefresh == null) {
                        swipeRefresh = (SwipeRefreshLayout) getParent();
                    }
                    swipeRefresh.setEnabled(true);
                } else {
                    if(swipeRefresh == null) {
                        swipeRefresh = (SwipeRefreshLayout) getParent();
                    }
                    swipeRefresh.setEnabled(false);
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

                if(swipeRefresh == null) {
                    swipeRefresh = (SwipeRefreshLayout) getParent();
                }
                swipeRefresh.setEnabled(false);
            }
        }
    }


    private class ActualAdapter extends BaseAdapter {

        @Override public int getCount() {
            return adapter.getCount() + 1;
        }

        @Override public Object getItem(int pos) {
            return pos == 0 ? null : adapter.getItem(pos - 1);
        }

        @Override public long getItemId(int pos) {
            return pos == 0 ? 0 : adapter.getItemId(pos - 1);
        }

        @Override public View getView(int pos, View view, ViewGroup viewGroup) {
            if (pos == 0) {
                return mTransparentHeader;
            }

            if (view == mTransparentHeader) {
                view = null;
            }

            return adapter.getView(pos - 1, view, viewGroup);
        }
    }
}