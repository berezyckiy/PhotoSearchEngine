package govin.maxim.photosearchengine.presenter;

import android.support.annotation.NonNull;

import java.util.Objects;

import govin.maxim.photosearchengine.base.BasePresenter;
import govin.maxim.photosearchengine.base.MapContract;
import govin.maxim.photosearchengine.model.api.Service;
import govin.maxim.photosearchengine.model.directions.DirectionResponse;
import govin.maxim.photosearchengine.model.distance_matrix.DistanceMatrix;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapPresenter extends BasePresenter<MapContract.View>
        implements MapContract.Presenter {

    private Service mService;
    private DistanceCallback mDistanceCallback;
    private DirectionsCallback mDirectionsCallback;

    public MapPresenter(@NonNull Service service) {
        mService = service;
        mDistanceCallback = new DistanceCallback();
        mDirectionsCallback = new DirectionsCallback();
    }

    @Override
    public void getDistanceMatrix(String origins, String destinations) {
        mService.getDistanceMatrix(origins, destinations).enqueue(mDistanceCallback);
    }

    @Override
    public void getDirections(String origin, String destination) {
        mService.getDirections(origin, destination).enqueue(mDirectionsCallback);
    }

    @Override
    public void onDestroy() {
        mService = null;
    }

    private class DistanceCallback implements Callback<DistanceMatrix> {

        @Override
        public void onResponse(Call<DistanceMatrix> call, Response<DistanceMatrix> response) {
            getView().showDistanceMatrix(response.body());
        }

        @Override
        public void onFailure(Call<DistanceMatrix> call, Throwable t) {
        }
    }

    private class DirectionsCallback implements Callback<DirectionResponse> {

        @Override
        public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse> response) {
            if (response.body() != null && !response.body().getStatus().equals("ZERO_RESULTS")) {
                getView().showDirections(response.body());
            } else {
                getView().showMessage("No direction");
            }
        }

        @Override
        public void onFailure(Call<DirectionResponse> call, Throwable t) {
        }
    }
}
