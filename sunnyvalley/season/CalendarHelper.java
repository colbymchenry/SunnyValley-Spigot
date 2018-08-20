package sunnyvalley.season;

import org.bukkit.World;

import static sunnyvalley.season.CalendarDate.DAYS_PER_SEASON;

public class CalendarHelper {
    public static final long TICKS_PER_DAY = 24000;
    public static final Season[] SEASONS;
    public static final Weekday[] DAYS;
    static {
        SEASONS = Season.class.getEnumConstants();
        DAYS = Weekday.class.getEnumConstants();
    }

    //Returns 0-29
    public static int getMinDay(int day) {
        return (int) (((double)(day) / 30D) * DAYS_PER_SEASON);
    }

    //Returns 0-29
    public static int getMaxDay(int day) {
        return (int) (((double)(day + 1) / 30D) * DAYS_PER_SEASON) - 1;
    }

    private static Weekday getWeekday(int days) {
        int modulus = days % 7;
        if (modulus < 0) modulus = 0;
        return DAYS[modulus];
    }

    public static Weekday getWeekday(long time) {
        return getWeekday(getElapsedDays(time));
    }

    private static int getYear(long totalTime) {
        return (int) Math.floor((double)getElapsedDays(totalTime) / 4 / DAYS_PER_SEASON);
    }

    public static Season getSeason(long totalTime) {
        return SEASONS[Math.max(0, (int)Math.floor((getElapsedDays(totalTime) / DAYS_PER_SEASON) % 4))];
    }

    private static int getDay(long totalTime) {
        return getElapsedDays(totalTime) % DAYS_PER_SEASON;
    }

    public static int getElapsedDays(long totalTime) {
        return (int) (totalTime / TICKS_PER_DAY);
    }

    private static int getTotalDays(int day, Season season, int year) {
        int season_days = DAYS_PER_SEASON * season.ordinal();
        int year_days = (year - 1) * (DAYS_PER_SEASON * 4);
        return day + season_days + year_days;
    }

    public static int getTotalDays(CalendarDate date) {
        int current_days = date.getDay();
        int season_days = DAYS_PER_SEASON * date.getSeason().ordinal();
        int year_days = (date.getYear() - 1) * (DAYS_PER_SEASON * 4);
        return current_days + season_days + year_days;
    }

    public static int getYearsPassed(CalendarDate birthday, CalendarDate date) {
        double current_total_days = getTotalDays(date);
        double birthday_total_days = getTotalDays(birthday);
        int one_year = DAYS_PER_SEASON * 4;

        int years_passed = (int) Math.floor(current_total_days / one_year);
        int birthday_years = (int) Math.floor(birthday_total_days / one_year);

        return Math.max(0, years_passed - birthday_years);
    }

    public static long getTime(int day, Season season, int year) {
        return (getTotalDays(day, season, year)) * TICKS_PER_DAY;
    }

    public static long getTime(World world) {
        return (world.getTime() + 6000) % TICKS_PER_DAY;
    }

    public static int getScaledTime(int time) {
        return (int) (((double)time / TICKS_PER_DAY) * 24000D);
    }

    public static boolean isBetween(World world, int open, int close) {
        long daytime = CalendarHelper.getTime(world); //0-23999 by default
        int scaledOpening = CalendarHelper.getScaledTime(open);
        int scaledClosing = CalendarHelper.getScaledTime(close);
        return daytime >= scaledOpening && daytime <= scaledClosing;
    }

    public static int getDays(CalendarDate then, CalendarDate now) {
        int thenDays = getTotalDays(then);
        int nowDays = getTotalDays(now);
        return (nowDays - thenDays);
    }

    public static String formatTime(int time) {
        int hour = time / 1000;
        int minute = (int) ((double) (time % 1000) / 20 * 1.2);
//        if (false) {
//            return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
//        } else {
            boolean pm = false;
            if (hour > 12) {
                hour = hour - 12;
                pm = true;
            }
            if (hour == 12)
                pm = true;
            if (hour == 0)
                hour = 12;

            return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + (pm ? "PM" : "AM");
//        }
    }
}