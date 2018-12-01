package cs486.nmnhut.gogo;

class TripActivity {


    String startDate;
    String endDate;
    String place;
    boolean Alarm;

    public boolean isAlarm() {
        return Alarm;
    }

    public void setAlarm(boolean alarm) {
        Alarm = alarm;
    }

    TripActivity() {

    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getPlace() {
        return place;
    }

}
