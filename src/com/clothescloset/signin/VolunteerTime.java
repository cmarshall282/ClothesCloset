package com.clothescloset.signin;

public class VolunteerTime {
    private int hours;
    private int minutes;
    private int seconds;

    protected VolunteerTime(int totalTime) {
        //get hours
        hours = totalTime/3600;
        totalTime -= hours * 3600;

        //get minutes
        minutes = totalTime/60;
        totalTime -= minutes * 60;

        //get seconds
        seconds = totalTime;
    }

    protected String format() {
        return hours + " hours " + minutes + " minutes " + seconds + " seconds";
    }
}