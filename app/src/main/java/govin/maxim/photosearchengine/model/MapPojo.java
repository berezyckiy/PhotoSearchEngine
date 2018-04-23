package govin.maxim.photosearchengine.model;

public class MapPojo {

    public MapPojo(String mTitle, Double mLatitude, Double mLongitude) {
        this.mTitle = mTitle;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
    }

    private String mTitle;
    private Double mLatitude;
    private Double mLongitude;

    public String getTitle() {
        return mTitle;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }
}
