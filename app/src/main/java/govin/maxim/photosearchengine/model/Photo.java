package govin.maxim.photosearchengine.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("secret")
    @Expose
    private String secret;
    @SerializedName("server")
    @Expose
    private String server;
    @SerializedName("farm")
    @Expose
    private Integer farm;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("ispublic")
    @Expose
    private Integer ispublic;
    @SerializedName("isfriend")
    @Expose
    private Integer isfriend;
    @SerializedName("isfamily")
    @Expose
    private Integer isfamily;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("accuracy")
    @Expose
    private Integer accuracy;
    @SerializedName("context")
    @Expose
    private Integer context;
    @SerializedName("url_n")
    @Expose
    private String urlN;
    @SerializedName("url_c")
    @Expose
    private String urlC;
    @SerializedName("height_n")
    @Expose
    private String heightN;
    @SerializedName("width_n")
    @Expose
    private String widthN;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("woeid")
    @Expose
    private String woeid;
    @SerializedName("geo_is_family")
    @Expose
    private Integer geoIsFamily;
    @SerializedName("geo_is_friend")
    @Expose
    private Integer geoIsFriend;
    @SerializedName("geo_is_contact")
    @Expose
    private Integer geoIsContact;
    @SerializedName("geo_is_public")
    @Expose
    private Integer geoIsPublic;

    public String getUrlC() {
        return urlC;
    }

    public void setUrlC(String urlC) {
        this.urlC = urlC;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Integer getFarm() {
        return farm;
    }

    public void setFarm(Integer farm) {
        this.farm = farm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIspublic() {
        return ispublic;
    }

    public void setIspublic(Integer ispublic) {
        this.ispublic = ispublic;
    }

    public Integer getIsfriend() {
        return isfriend;
    }

    public void setIsfriend(Integer isfriend) {
        this.isfriend = isfriend;
    }

    public Integer getIsfamily() {
        return isfamily;
    }

    public void setIsfamily(Integer isfamily) {
        this.isfamily = isfamily;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public Integer getContext() {
        return context;
    }

    public void setContext(Integer context) {
        this.context = context;
    }

    public String getUrlN() {
        return urlN;
    }

    public void setUrlN(String urlN) {
        this.urlN = urlN;
    }

    public String getHeightN() {
        return heightN;
    }

    public void setHeightN(String heightN) {
        this.heightN = heightN;
    }

    public String getWidthN() {
        return widthN;
    }

    public void setWidthN(String widthN) {
        this.widthN = widthN;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getWoeid() {
        return woeid;
    }

    public void setWoeid(String woeid) {
        this.woeid = woeid;
    }

    public Integer getGeoIsFamily() {
        return geoIsFamily;
    }

    public void setGeoIsFamily(Integer geoIsFamily) {
        this.geoIsFamily = geoIsFamily;
    }

    public Integer getGeoIsFriend() {
        return geoIsFriend;
    }

    public void setGeoIsFriend(Integer geoIsFriend) {
        this.geoIsFriend = geoIsFriend;
    }

    public Integer getGeoIsContact() {
        return geoIsContact;
    }

    public void setGeoIsContact(Integer geoIsContact) {
        this.geoIsContact = geoIsContact;
    }

    public Integer getGeoIsPublic() {
        return geoIsPublic;
    }

    public void setGeoIsPublic(Integer geoIsPublic) {
        this.geoIsPublic = geoIsPublic;
    }
}
