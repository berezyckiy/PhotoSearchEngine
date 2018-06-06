package govin.maxim.photosearchengine.model;

public class MapPojo {

    public MapPojo(String mTitle, Double mLatitude, Double mLongitude, String url) {
        this.mTitle = mTitle;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mUrl = url;
    }

    private String mTitle;
    private Double mLatitude;
    private Double mLongitude;
    private String mUrl;

    public String getTitle() {
        return mTitle;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public String getUrl() {
        return mUrl;
    }
}
