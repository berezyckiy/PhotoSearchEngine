package govin.maxim.photosearchengine.base;


import govin.maxim.photosearchengine.model.directions.DirectionResponse;
import govin.maxim.photosearchengine.model.distance_matrix.DistanceMatrix;

public interface MapContract {

    interface View extends MvpView {

        void showDistanceMatrix(DistanceMatrix response);

        void showDirections(DirectionResponse response);
    }

    interface Presenter extends MvpPresenter<View> {

        void getDistanceMatrix(String origins, String destinations);

        void getDirections(String origin, String destination);
    }
}
