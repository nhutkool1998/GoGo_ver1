package cs486.nmnhut.gogo;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route {

    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
    public List<Integer> waypointOrder;
}
