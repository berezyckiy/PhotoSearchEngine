package govin.maxim.photosearchengine.model.api;

import android.support.annotation.NonNull;

import govin.maxim.photosearchengine.model.directions.DirectionResponse;
import govin.maxim.photosearchengine.model.distance_matrix.DistanceMatrix;
import retrofit2.Call;

public interface GoogleDistanceService {

    Call<DistanceMatrix> getDistanceMatrix(@NonNull String origins, @NonNull String destinations);

    Call<DirectionResponse> getDirections(@NonNull String origin, @NonNull String destination);
}
