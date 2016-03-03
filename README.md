<img align="left" src="/images/icon.png?raw=true">
# ViewExploder

View Exploder is a library that lets you explode apart arbitrary view hierarchies in 3D to understand how views interact in the z axis, or just for a cool visualization.

The library is also useful to see how views are drawn to the screen and can be used to easily identify drawing problems such as overdraw.

![Alt text](/images/exploder2.gif?raw=true "View Exploder In Action")

# Usage
To use the View Exploder simply add it to your view heirarchy as a root view to your layout.

```XML
<com.alexgwyn.exploderview.ExploderView
      android:id="@+id/exploderView"
      android:layout_width="match_parent"
      android:layout_height="match_parent">
      
        <!-- Your Layout -->
        
</com.alexgwyn.exploderview.ExploderView>
```
In your onCreate find a reference and then set either an `ElevationLayerBuilder` or `HeirarchyLayerBuilder` to the view. By default a `HeirarchyLayerBuilder` is used.
A `HeirarchyLayerBuilder` builds the layer based on the nesting of the views in your layout, while the `ElevationLayerBuilder` builds layers based on a views elevation.

```Java
mExploderView = (ExploderView) findViewById(R.id.exploderView);
mExploderView.setLayerBuilder(new ElevationLayerBuilder());
```
  
Next call `setInteractive(true)` if you want to be able to interact with the views in the exploded layout

```Java
mExploderView.setInteractive(false);
```

Finally call `explode()` to see your views in 3D!

```Java
mExploderView.explode(true);
```

  



