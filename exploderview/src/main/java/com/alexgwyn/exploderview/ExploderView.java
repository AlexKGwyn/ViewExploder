package com.alexgwyn.exploderview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Arrays;


public class ExploderView extends FrameLayout implements ViewTreeObserver.OnPreDrawListener {

    private final Camera mCamera = new Camera();
    private final Matrix mMatrix = new Matrix();
    private final Matrix mCameraMatrix = new Matrix();

    private final int[] mViewLocation = new int[2];
    private final int[] mRootLocation = new int[2];

    private boolean mExploded = false;

    private float mRotationX = 45;
    private float mRotationY = 0;
    private float mZoom = .6f;
    private float mLayerSpacing = .4f;

    private float[] mTouch = new float[2];
    private float[] mTransformedTouch = new float[2];
    private Rect mViewRect = new Rect();

    private LayerBuilder mLayerBuilder = new HierarchyLayerBuilder();
    private boolean mDrawing = false;
    private boolean mInteractive = false;


    private GestureDetector mGestureDetector;
    private GestureDetector.SimpleOnGestureListener mSimpleGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float rotx = -180 * (distanceX / getWidth());
            float roty = 180 * (distanceY / getHeight());
            mRotationX = Math.min(80, Math.max(-80, mRotationX + rotx));
            mRotationY = Math.min(80, Math.max(-80, mRotationY + roty));
            return true;
        }
    };

    private ScaleGestureDetector mScaleGestureDetector;
    private ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mZoom = Math.min(2f, Math.max(.2f, mZoom * detector.getScaleFactor()));
            return true;
        }
    };

    private ArrayList<Layer> mViewLayers;

    public ExploderView(Context context) {
        super(context);
        init();
    }

    public ExploderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExploderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExploderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mGestureDetector = new GestureDetector(getContext(), mSimpleGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), mScaleGestureListener);
    }


    public void setLayerBuilder(LayerBuilder layerBuilder) {
        mLayerBuilder = layerBuilder;
    }

    public boolean isExploded() {
        return mExploded;
    }

    public void explode(boolean explode) {
        if (mExploded != explode) {
            mExploded = explode;
            setWillNotDraw(!explode);
            invalidate();
        }
    }

    public boolean isInteractive() {
        return mInteractive;
    }

    public void setInteractive(boolean interactive) {
        mInteractive = interactive;
    }

    public void setLayerSpacing(float layerSpacing) {
        mLayerSpacing = layerSpacing;
        invalidate();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mExploded) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mExploded) {
            if (mInteractive) {
                onInteractiveTouch(event);
            } else {
                mScaleGestureDetector.onTouchEvent(event);
                if (!mScaleGestureDetector.isInProgress()) {
                    mGestureDetector.onTouchEvent(event);
                }
                invalidate();
            }
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void onInteractiveTouch(MotionEvent event) {
        //TODO order is not yet properly handled and the lowest layer will always take the touch event.
        mTouch[0] = event.getX();
        mTouch[1] = event.getY();

        if (mViewLayers != null && !mViewLayers.isEmpty()) {
            for (int i = 0; i < mViewLayers.size(); i++) {
                Layer layer = mViewLayers.get(i);
                if (layer.getInverseLayerMatrix() != null) {
                    layer.getInverseLayerMatrix().mapPoints(mTransformedTouch, mTouch);
                    if (mTransformedTouch[0] >= 0 && mTransformedTouch[0] <= getWidth() && mTransformedTouch[1] >= 0 && mTransformedTouch[1] <= getHeight() && layer.getView().isEnabled()) {
                        layer.getView().getDrawingRect(mViewRect);
                        mViewRect.offset(layer.getViewPosition()[0], layer.getViewPosition()[1]);
                        if (mViewRect.contains((int) mTransformedTouch[0], (int) mTransformedTouch[1])) {
                            event.setLocation(mTransformedTouch[0] - layer.getViewPosition()[0], mTransformedTouch[1] - layer.getViewPosition()[1]);
                            if (layer.getView().onTouchEvent(event)) {
                                Log.d("Exploder", "Layer took touch event " + layer.getView().toString());
                                invalidate();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }


    private Matrix calculateLayerMatrix(float layer) {
        mMatrix.reset();
        mMatrix.preTranslate(-getMeasuredWidth() / 2, -getMeasuredHeight() / 2);
        mCamera.save();

        float layerRadius = layer * mLayerSpacing;

        float x = (float) (Math.sin(Math.toRadians(mRotationX)) * layerRadius);
        float y = (float) (Math.sin(Math.toRadians(mRotationY)) * Math.cos(Math.toRadians(mRotationX)) * layerRadius);
        float z = -50f;
        mCamera.setLocation(-x * mZoom, -y * mZoom, z);

//        TODO use these to calculate the draw order to allow for going behind the view hierarchy!
//        float angleY = (float) Math.toDegrees(Math.asin((y * zoom) / z));
//        float angleX = (float) Math.toDegrees(Math.asin((x * zoom) / z));

        mCamera.rotateX(mRotationY);
        mCamera.rotateY(mRotationX);

        mCamera.getMatrix(mCameraMatrix);

        mCamera.restore();

        mMatrix.postConcat(mCameraMatrix);
        mMatrix.postTranslate(getMeasuredWidth() / 2, getMeasuredHeight() / 2);

        return mMatrix;
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        child.getViewTreeObserver().addOnPreDrawListener(this);

    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        child.getViewTreeObserver().removeOnPreDrawListener(this);
    }

    @Override
    public void draw(Canvas canvas) {
        mDrawing = true;

        if (!mExploded || isInEditMode()) {
            //Act normal
            super.draw(canvas);
        } else {
            if (getBackground() != null) {
                getBackground().draw(canvas);
            }

            getLocationInWindow(mRootLocation);

            ArrayList<View> hiddenViews = new ArrayList<>(10);
            mViewLayers = mLayerBuilder.buildLayers(this);
            for (Layer layer : mViewLayers) {
                View view = layer.getView();
                if (view == this) {
                    continue;
                }

                if (view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    for (int i = 0, count = viewGroup.getChildCount(); i < count; i++) {
                        View child = viewGroup.getChildAt(i);
                        if (child.getVisibility() == VISIBLE) {
                            hiddenViews.add(child);
                            child.setVisibility(INVISIBLE);
                        }
                    }
                }

                Matrix layerMatrix = new Matrix(calculateLayerMatrix(layer.getLevel()));
                layerMatrix.postScale(mZoom, mZoom, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
                view.getLocationInWindow(mViewLocation);

                Matrix invertedLayerMatrix = new Matrix();
                layerMatrix.invert(invertedLayerMatrix);
                layer.setInverseLayerMatrix(new Matrix(invertedLayerMatrix));

                layer.setViewPosition(Arrays.copyOf(mViewLocation, 2));
                canvas.save();
                canvas.concat(layerMatrix);
                canvas.translate(mViewLocation[0] - mRootLocation[0], mViewLocation[1] - mRootLocation[1]);
                view.draw(canvas);
                canvas.restore();

                for (int i = 0, hiddenViewsSize = hiddenViews.size(); i < hiddenViewsSize; i++) {
                    View hidden = hiddenViews.get(i);
                    hidden.setVisibility(View.VISIBLE);
                }
            }
        }
        mDrawing = false;
    }


    @Override
    public boolean onPreDraw() {
        //If a child is gonna update we need to update
        //There is probably a better way to do this.
        if (!mDrawing) {
            invalidate();
        }
        return true;
    }
}
