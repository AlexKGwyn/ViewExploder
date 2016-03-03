package com.alexgwyn.viewexploder;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A useful view to make sure our transformed touch events match up to the actual view position on screen
 */
public class DrawView extends View {

    private Queue<PointF> mPointFQueue = new LinkedList<>();

    private Paint mPaint;
    private Paint mBackgroundPaint;

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.GRAY);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPointFQueue.size() < 40) {
            mPointFQueue.add(new PointF(event.getX(), event.getY()));

        } else {
            PointF pointF = mPointFQueue.poll();
            pointF.set(event.getX(), event.getY());
            mPointFQueue.add(pointF);
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBackgroundPaint);
        for (PointF point : mPointFQueue) {
            canvas.drawCircle(point.x, point.y, 10, mPaint);
        }
    }
}
