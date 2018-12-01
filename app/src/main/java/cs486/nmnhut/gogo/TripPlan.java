package cs486.nmnhut.gogo;

import java.util.ArrayList;

class TripPlan {
    String startDate;
    String endDate;
    ArrayList<TripActivity> activities;

    TripPlan() {

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
