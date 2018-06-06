package govin.maxim.photosearchengine.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import govin.maxim.photosearchengine.R;
import govin.maxim.photosearchengine.base.MapContract;
import govin.maxim.photosearchengine.model.MapPojo;
import govin.maxim.photosearchengine.model.Photo;
import govin.maxim.photosearchengine.model.api.Service;
import govin.maxim.photosearchengine.model.distance_matrix.DistanceMatrix;
import govin.maxim.photosearchengine.presenter.MapPresenter;

public class GoogleMapsFragment extends Fragment
        implements MapContract.View,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener {

    private static final String EMPTY_TITLE_REPLACEMENT = "No title";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private MapPresenter mPresenter;
    private GoogleMap mMap;
    private List<MapPojo> mMapPojos = new ArrayList<>();
    private LatLng mCurrentLocation;
    private List<MarkerOptions> mMarkersList;
    private LatLngBounds.Builder mBoundsBuilder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new MapPresenter(new Service());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mPresenter.attachView(this);

        return inflater.inflate(R.layout.fragment_google_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.main_branch_map);
        mMapFragment.getMapAsync(this);
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
                MapPojo mapPojo = new MapPojo(photo.getTitle(), photo.getLatitude(),
                        photo.getLongitude(), photo.getUrlN());
                if (!mMapPojos.contains(mapPojo)) {
                    mMapPojos.add(mapPojo);
                }
            }
        }
    }

    private void showPhotoWithGeo(List<MapPojo> mapPojos) {
        mMarkersList = new ArrayList<>();
        mBoundsBuilder = new LatLngBounds.Builder();
        String destinations = "";
        boolean isSomeoneHasLatLng = false;
        for (MapPojo pojo : mapPojos) {
            final LatLng latLng = new LatLng(pojo.getLatitude(), pojo.getLongitude());
            final String title = String.valueOf(simplifyEmptyTitle(pojo.getTitle()));
            mBoundsBuilder.include(latLng);
            isSomeoneHasLatLng = true;
            destinations += pojo.getLatitude() + "," + pojo.getLongitude() + "|";
            mMarkersList.add(new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );
        }
        if (isSomeoneHasLatLng && mCurrentLocation != null) {
            final String origins = mCurrentLocation.latitude + "," + mCurrentLocation.longitude;
            mPresenter.getDistanceMatrix(origins, destinations);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        }
    }

    public boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
                                       String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else if (mMap != null) {
            final LocationManager locationManager = (LocationManager)
                    getContext().getSystemService(Context.LOCATION_SERVICE);
            final Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(new Criteria(), false));
            mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.setMyLocationEnabled(true);
        }
    }

    public void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(new CustomInfoAdapter());

        enableMyLocation();
    }

    private String simplifyEmptyTitle(String target) {
        if (target.isEmpty()) {
            return EMPTY_TITLE_REPLACEMENT;
        } else {
            return target;
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Snackbar.make(getView(), "My location button clicked", Snackbar.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Snackbar.make(getView(), "My location clicked", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Snackbar.make(getView(), "Info window clicked", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void showDistanceMatrix(@NonNull DistanceMatrix response) {
        String snippet = "Empty";
        for (int i = 0; i < response.getDestinationAddresses().size(); i++) {
            if (response.getRows().get(0).getElements().get(i).getDistance() != null &&
                    response.getRows().get(0).getElements().get(i).getDuration() != null) {
                snippet = response.getRows().get(0).getElements().get(i).getDistance().getText() +
                        "\n" + response.getRows().get(0).getElements().get(i).getDuration().getText();
                mMarkersList.get(i).snippet(snippet);
            }
            mMarkersList.get(i).snippet(snippet);
            mMap.addMarker(mMarkersList.get(i));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 0));
    }

    @Override
    public void showMessage(String message) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mPresenter.detachView();
    }

    private class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {

        private View mWindow;

        CustomInfoAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.layout_custom_info_window_map, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            ((TextView) mWindow.findViewById(R.id.title)).setText(marker.getTitle());
            ((TextView) mWindow.findViewById(R.id.snippet)).setText(marker.getSnippet());
            return mWindow;
        }
    }
}
