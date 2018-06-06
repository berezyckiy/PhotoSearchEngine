package govin.maxim.photosearchengine.base;


import govin.maxim.photosearchengine.model.distance_matrix.DistanceMatrix;

public interface MapContract {

    interface View extends MvpView {

        void showDistanceMatrix(DistanceMatrix response);
    }

    interface Presenter extends MvpPresenter<View> {

        void getDistanceMatrix(String origins, String destinations);
    }
}
