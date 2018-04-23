package govin.maxim.photosearchengine.base;

public abstract class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private static final String STATUS_OK = "ok";

    private V mView;

    @Override
    public void attachView(V view) {
        mView = view;
    }

    @Override
    public void detachView() {
        mView = null;
    }

    @Override
    public V getView() {
        return mView;
    }

    public static String getStatusOk() {
        return STATUS_OK;
    }
}
