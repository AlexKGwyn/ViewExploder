package com.alexgwyn.viewexploder;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import com.alexgwyn.exploderview.ElevationLayerBuilder;
import com.alexgwyn.exploderview.ExploderView;

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private ExploderView mExploderView;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;

    private float mElevationStart;
    private float mElevationEnd;

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //A nice elevation animation to show when the view is scrolling
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                View child = recyclerView.getChildAt(0);
                if (child != null && recyclerView.getChildAdapterPosition(child) == 0) {
                    float interpol = (Math.max(-child.getTop(), 0) / (float) child.getHeight());
                    float elevation = mElevationStart + (mElevationEnd - mElevationStart) * interpol;
                    mToolbar.setElevation(elevation);

                } else {
                    mToolbar.setElevation(mElevationEnd);

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mExploderView = (ExploderView) findViewById(R.id.exploderView);
        mExploderView.setLayerBuilder(new ElevationLayerBuilder());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Exploder View Sample");
        mToolbar.inflateMenu(R.menu.main);
        mToolbar.setOnMenuItemClickListener(this);

        mElevationEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        mElevationStart = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new SampleAdapter());
        mRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    @Override
    public void onBackPressed() {
        if (mExploderView.isExploded()) {
            mExploderView.explode(false);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_explode) {
            mExploderView.explode(true);
            mExploderView.setInteractive(false);

            return true;
        }
        if (item.getItemId() == R.id.action_explode_interactive) {
            mExploderView.explode(true);
            mExploderView.setInteractive(true);
            return true;
        }
        return false;
    }
}
