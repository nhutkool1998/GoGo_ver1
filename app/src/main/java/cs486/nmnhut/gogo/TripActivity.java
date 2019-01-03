package cs486.nmnhut.gogo;

class TripActivity {


    String startDate;
    String endDate;
    String place;
    ToaDo toaDo;
    boolean alarm;

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
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
