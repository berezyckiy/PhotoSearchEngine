package govin.maxim.photosearchengine.presenter;

import android.support.annotation.NonNull;

import java.util.Objects;

import govin.maxim.photosearchengine.base.BasePresenter;
import govin.maxim.photosearchengine.base.MapContract;
import govin.maxim.photosearchengine.model.api.Service;
import govin.maxim.photosearchengine.model.distance_matrix.DistanceMatrix;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapPresenter extends BasePresenter<MapContract.View>
        implements MapContract.Presenter,
        Callback<DistanceMatrix> {

    private Service mService;

    public MapPresenter(@NonNull Service service) {
        mService = service;
    }

    @Override
    public void getDistanceMatrix(String origins, String destinations) {
        mService.getDistanceMatrix(origins, destinations).enqueue(this);
    }

    @Override
    public void onResponse(Call<DistanceMatrix> call, Response<DistanceMatrix> response) {
        getView().showDistanceMatrix(Objects.requireNonNull(response.body()));
    }

    @Override
    public void onFailure(Call<DistanceMatrix> call, Throwable t) {
        //smth wrong
    }


    @Override
    public void onDestroy() {
        mService = null;
    }
}
