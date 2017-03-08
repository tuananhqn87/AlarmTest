package org.anhtran.alarmtest;

/**
 * Created by anhtran on 2/26/17.
 */

public class Alarm {

    private int hour;
    private int minute;
    private int second;

    /**
     * This class manages alarm information
     * @param hour hour of alarm time
     * @param minute minute of alarm time
     * @param second second of alarm time
     */
    public Alarm (int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    //Return hour
    public int getHour() {
        return hour;
    }

    //Return minute
    public int getMinute() {
        return minute;
    }

    //Return second
    public int getSecond() {
        return second;
    }

    //Return AM or PM
    public String getAmPm() {
        if(getHour() > 12) {
            return "PM";
        }
        return "AM";
    }

    //Convert to time to string
    @Override
    public String toString() {
        String formattedHour = Integer.toString(getHour());
        String formattedMinute = Integer.toString(getMinute());
        String formattedSecond = Integer.toString(getSecond());
        if(getHour() > 12) {
            formattedHour = Integer.toString(getHour()%12);
        }

        if(getMinute() < 10) {
            formattedMinute = "0" + formattedMinute;
        }

        if(getSecond() < 10) {
            formattedSecond = "0" + formattedSecond;
        }
        return formattedHour + ":"
                + formattedMinute + ":"
                + formattedSecond + " "
                + getAmPm();
    }
}
