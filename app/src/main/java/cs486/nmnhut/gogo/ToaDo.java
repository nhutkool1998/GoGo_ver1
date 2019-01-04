package cs486.nmnhut.gogo;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class ToaDo {
    double lat;
    double lng;

    ToaDo() {
        lat = 0;
        lng = 0;
    }

    ToaDo(LatLng latLng) {
        lat = latLng.latitude;
        lng = latLng.longitude;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public double Distance(ToaDo t) {
        float[] res = new float[1];
        Location.distanceBetween(t.lat, t.lng, this.lat, this.lng, res);
        return res[0];
    }

    public LatLng getLatLng() {
        return new LatLng(this.lat, this.lng);
    }

    @Override
    public boolean equals(Object obj) {
        ToaDo t = (ToaDo) obj;
        return t.lat == this.lat && t.lng == this.lng;
    }
}
