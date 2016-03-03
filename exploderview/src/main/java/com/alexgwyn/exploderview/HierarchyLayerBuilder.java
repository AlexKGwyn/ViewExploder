package com.alexgwyn.exploderview;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class HierarchyLayerBuilder implements LayerBuilder {
    @Override
    public ArrayList<Layer> buildLayers(View root) {
        return buildLayers(root, 0);
    }

    public ArrayList<Layer> buildLayers(View root, int level) {
        ArrayList<Layer> ret = new ArrayList<Layer>();
        ret.add(new Layer(root, level));
        if (root instanceof ViewGroup && root.getVisibility() == View.VISIBLE) {
            for (int i = 0; i < ((ViewGroup) root).getChildCount(); i++) {
                View child = ((ViewGroup) root).getChildAt(i);
                ret.addAll(buildLayers(child, level + 1));
            }
        }
        return ret;
    }
}
