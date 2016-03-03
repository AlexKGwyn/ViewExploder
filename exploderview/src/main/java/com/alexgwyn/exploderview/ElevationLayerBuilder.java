package com.alexgwyn.exploderview;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;


public class ElevationLayerBuilder implements LayerBuilder {

    @Override
    public ArrayList<Layer> buildLayers(View root) {
        ArrayList<Layer> layers = buildLayers(root, 0);
        Collections.sort(layers);
        return layers;
    }

    public ArrayList<Layer> buildLayers(View root, float currentElevation) {
        ArrayList<Layer> ret = new ArrayList<Layer>();
        float elevation = ViewCompat.getElevation(root);
        Layer layer = new Layer(root, currentElevation + elevation);
        ret.add(layer);
        if (root instanceof ViewGroup && root.getVisibility() == View.VISIBLE) {
            for (int i = 0; i < ((ViewGroup) root).getChildCount(); i++) {
                View child = ((ViewGroup) root).getChildAt(i);
                ret.addAll(buildLayers(child, elevation + currentElevation));
            }
        }
        return ret;
    }
}
