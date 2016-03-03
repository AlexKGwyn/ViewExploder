package com.alexgwyn.exploderview;

import android.view.View;

import java.util.ArrayList;

public interface LayerBuilder {

    public ArrayList<Layer> buildLayers(View root);

}
