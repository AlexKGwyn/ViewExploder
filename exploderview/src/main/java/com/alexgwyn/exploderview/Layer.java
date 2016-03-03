package com.alexgwyn.exploderview;

import android.graphics.Matrix;
import android.view.View;

public class Layer implements Comparable<Layer> {
    private View mView;
    private float mLevel;
    private Matrix mInverseLayerMatrix;
    private int[] mViewPosition;


    public Layer(View view, float level) {
        mView = view;
        mLevel = level;
    }

    public Matrix getInverseLayerMatrix() {
        return mInverseLayerMatrix;
    }

    public void setInverseLayerMatrix(Matrix inverseLayerMatrix) {
        mInverseLayerMatrix = inverseLayerMatrix;
    }

    public int[] getViewPosition() {
        return mViewPosition;
    }

    public void setViewPosition(int[] viewPosition) {
        mViewPosition = viewPosition;
    }

    public View getView() {
        return mView;
    }

    public float getLevel() {
        return mLevel;
    }

    @Override
    public int compareTo(Layer another) {
        return (int) (mLevel - another.getLevel());
    }
}
