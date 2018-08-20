package sunnyvalley.season;

public enum Weekday {

    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;

    public boolean isWeekend() {
        return this == SATURDAY || this == SUNDAY;
    }

}
