package govin.maxim.photosearchengine.model.clustering_marker;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerItem implements ClusterItem {

    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;

    public MarkerItem(LatLng position, String title, String snippet) {
        mPosition = position;
        mTitle = title;
        mSnippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSnippet() {
        return mSnippet;
    }
}
