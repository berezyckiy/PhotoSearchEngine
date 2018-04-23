package govin.maxim.photosearchengine.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import govin.maxim.photosearchengine.R;
import govin.maxim.photosearchengine.model.MapPojo;
import govin.maxim.photosearchengine.model.Photo;

public class GoogleMapsFragment extends Fragment {

    private static final String[] PERMISSIONS_CONSTANT = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final String EMPTY_TITLE_REPLACEMENT = "No title";


    private GoogleMap mMap;
    private OnGoogleMapsFragmentListener mListener;
    private View mRootView;
    private View mMapView;
    private OnGoogleMapReadyCallback mGoogleMapCallback;
    private List<MapPojo> mMapPojos = new ArrayList<>();

    public interface OnGoogleMapsFragmentListener {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_google_maps, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        requestPermissions(PERMISSIONS_CONSTANT, 1);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGoogleMapsFragmentListener) {
            mListener = (OnGoogleMapsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGoogleMapsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initView() {
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.main_branch_map);
        mMapView = mMapFragment.getView();
        mGoogleMapCallback = new OnGoogleMapReadyCallback();
        mMapFragment.getMapAsync(mGoogleMapCallback);
    }

    public void setCurrentlyPhotosList(List<Photo> photoList) {
        sortWithGeoOnly(photoList);
        showPhotoWithGeo(mMapPojos);
    }

    public void clearMarkers() {
        mMap.clear();
        mMapPojos.clear();
    }

    private void sortWithGeoOnly(List<Photo> photoList) {
        for (Photo photo : photoList) {
            if (photo.getLatitude() != 0 && photo.getLongitude() != 0) {
                MapPojo mapPojo = new MapPojo(photo.getTitle(), photo.getLatitude(), photo.getLongitude());
                if (!mMapPojos.contains(mapPojo)) {
                    mMapPojos.add(mapPojo);
                }
            }
        }
    }

    private void showPhotoWithGeo(List<MapPojo> mapPojos) {
        for (MapPojo pojo : mapPojos) {
            mGoogleMapCallback.addMarker(pojo);
        }
    }

    private class OnGoogleMapReadyCallback implements OnMapReadyCallback {
        private double longitude;
        private double latitude;

        private MapPojo mMapPojo;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            if (!isPermissionGranted()) {
                return;
            }

            View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            locationButton.setLayoutParams(getParamsBottomLeft(locationButton));

            putMarkerOnMap();
        }

        public void addMarker(MapPojo pojo) {
            mMapPojo = pojo;
            onMapReady(mMap);
        }

        private void putMarkerOnMap() {
            if (mMapPojo != null) {
                LatLng latLng = new LatLng(mMapPojo.getLatitude(), mMapPojo.getLongitude());
                String title = String.valueOf(simplifyEmptyTitle(mMapPojo.getTitle()));
                mMap.addMarker(new MarkerOptions().position(latLng).title(title)).showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }

        private RelativeLayout.LayoutParams getParamsBottomLeft(View view) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();

            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
            params.addRule(RelativeLayout.ALIGN_END, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

            params.setMargins(30, 0, 0, 100);
            return params;
        }

        private String simplifyEmptyTitle(String target) {
            if (target.equals("")) {
                return EMPTY_TITLE_REPLACEMENT;
            } else {
                return target;
            }
        }

        private boolean isPermissionGranted() {
            if (!(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                return true;
            } else {
                return false;
            }
        }

    }
}
