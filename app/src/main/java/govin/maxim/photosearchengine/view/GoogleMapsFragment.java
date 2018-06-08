package govin.maxim.photosearchengine.view;

import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import govin.maxim.photosearchengine.R;
import govin.maxim.photosearchengine.base.MapContract;
import govin.maxim.photosearchengine.model.MapPojo;
import govin.maxim.photosearchengine.model.Photo;
import govin.maxim.photosearchengine.model.api.Service;
import govin.maxim.photosearchengine.model.clustering_marker.MarkerItem;
import govin.maxim.photosearchengine.model.directions.DirectionResponse;
import govin.maxim.photosearchengine.model.directions.Leg;
import govin.maxim.photosearchengine.model.directions.Route;
import govin.maxim.photosearchengine.model.directions.Step;
import govin.maxim.photosearchengine.model.distance_matrix.DistanceMatrix;
import govin.maxim.photosearchengine.presenter.MapPresenter;
import govin.maxim.photosearchengine.service.MyJobService;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

public class GoogleMapsFragment extends Fragment
        implements MapContract.View,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ClusterManager.OnClusterItemClickListener<MarkerItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<MarkerItem> {

    private static final String EMPTY_TITLE_REPLACEMENT = "No title";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private Marker mCurrentLocationMarker;
    private JobScheduler mJobScheduler;
    private BroadcastReceiver mReceiver;

    private MapPresenter mPresenter;
    private GoogleMap mMap;
    private List<MapPojo> mMapPojos = new ArrayList<>();
    private LatLng mCurrentLocation;
    private Polyline mPolyLineDirection;
    private ClusterManager<MarkerItem> mClusterManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new MapPresenter(new Service());
        mJobScheduler = (JobScheduler) getContext().getSystemService(JOB_SCHEDULER_SERVICE);
        mReceiver = new MyReceiver();
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

        final SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.main_branch_map);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        startJob();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver,
                new IntentFilter("job-service-response"));
    }

    private void startJob() {
        ComponentName jobService =
                new ComponentName(getContext().getPackageName(), MyJobService.class.getName());
        JobInfo jobInfo =
                new JobInfo.Builder(1, jobService).setPeriodic(900000000).build();
        mJobScheduler.schedule(jobInfo);
    }

    public void setCurrentlyPhotosList(List<Photo> photoList) {
        mMapPojos.clear();
        sortWithGeoOnly(photoList);
        showPhotoWithGeo();
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
                mMapPojos.add(mapPojo);
            }
        }
    }

    private void showPhotoWithGeo() {
        final StringBuilder destinations = new StringBuilder();
        boolean isSomeoneHasLatLng = false;
        for (MapPojo pojo : mMapPojos) {
            isSomeoneHasLatLng = true;
            destinations.append(pojo.getLatitude()).append(",").append(pojo.getLongitude()).append("|");
        }
        if (isSomeoneHasLatLng && mCurrentLocation != null) {
            final String origins = mCurrentLocation.latitude + "," + mCurrentLocation.longitude;
            mPresenter.getDistanceMatrix(origins, destinations.toString());
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        initClusterManager();
        initGoogleMap();
        enableMyLocation();
    }

    private void initClusterManager() {
        mClusterManager = new ClusterManager<>(getContext(), mMap);
        mClusterManager.setRenderer(new MyRender(getContext(), mMap, mClusterManager));
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
    }

    private void initGoogleMap() {
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        mMap.setInfoWindowAdapter(new CustomInfoAdapter());
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
    public void showDistanceMatrix(@NonNull DistanceMatrix response) {
        LatLngBounds.Builder mBoundsBuilder = new LatLngBounds.Builder();
        String snippet = "Empty";
        mClusterManager.clearItems();
        for (int i = 0; i < response.getDestinationAddresses().size(); i++) {
            if (response.getRows().get(0).getElements().get(i).getDistance() != null &&
                    response.getRows().get(0).getElements().get(i).getDuration() != null) {
                snippet = response.getRows().get(0).getElements().get(i).getDistance().getText() +
                        "\n" + response.getRows().get(0).getElements().get(i).getDuration().getText();
            }
            final LatLng latLng = new LatLng(mMapPojos.get(i).getLatitude(), mMapPojos.get(i).getLongitude());
            final String title = String.valueOf(simplifyEmptyTitle(mMapPojos.get(i).getTitle()));
            mBoundsBuilder.include(latLng);
            mClusterManager.addItem(new MarkerItem(latLng, title, snippet));

        }
        mClusterManager.cluster();

        if (mMapPojos.isEmpty()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 10));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentLocation));
        }
    }

    @Override
    public void showDirections(DirectionResponse response) {
        List<LatLng> latLngList = new ArrayList<>();
        for (Route route : response.getRoutes()) {
            for (Leg leg : route.getLegs()) {
                for (Step step : leg.getSteps()) {
                    latLngList.add(new LatLng(step.getStartLocation().getLat(), step.getStartLocation().getLng()));
                    latLngList.add(new LatLng(step.getEndLocation().getLat(), step.getEndLocation().getLng()));
                }
            }
        }
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(latLngList)
                .color(Color.BLUE)
                .geodesic(true);

        addDirectionOnMap(polylineOptions);
    }

    private void addDirectionOnMap(PolylineOptions polylineOptions) {
        if (mPolyLineDirection != null) {
            mPolyLineDirection.remove();
        }

        mPolyLineDirection = mMap.addPolyline(polylineOptions);
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();

        mJobScheduler.cancel(1);
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mPresenter.detachView();
    }

    @Override
    public boolean onClusterItemClick(MarkerItem markerItem) {
        if (mCurrentLocation != null) {
            final String origins = mCurrentLocation.latitude + "," + mCurrentLocation.longitude;
            mPresenter.getDirections(origins, markerItem.getPosition().latitude
                    + "," + markerItem.getPosition().longitude);
        }
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MarkerItem markerItem) {
        Snackbar.make(getView(), "Info window clicked", Snackbar.LENGTH_SHORT).show();
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

    private class MyRender extends DefaultClusterRenderer<MarkerItem> {

        public MyRender(Context context, GoogleMap map, ClusterManager<MarkerItem> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerItem item, MarkerOptions markerOptions) {
            super.onBeforeClusterItemRendered(item, markerOptions);
            markerOptions
                    .snippet(item.getSnippet())
                    .title(item.getTitle());
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mCurrentLocationMarker != null) {
                mCurrentLocationMarker.remove();
            }

            mCurrentLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(intent.getDoubleExtra("lat", 0),
                            intent.getDoubleExtra("lon", 0)))
                    .title("You")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        }
    }
}
