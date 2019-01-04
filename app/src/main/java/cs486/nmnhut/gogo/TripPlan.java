package cs486.nmnhut.gogo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

class TripPlan {
    String startDate;
    String endDate;
    ArrayList<TripActivity> activities;

    TripPlan() {
        activities = new ArrayList<>();
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String s = sdf.format(calendar1.getTime());
        startDate = s;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public ArrayList<TripActivity> getActivities() {
        return activities;
    }
}
