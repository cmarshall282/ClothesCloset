package com.clothescloset.signin;

public class Volunteer {
    protected String name;
    protected int id;

    protected long startTime;
    protected long endTime;

    protected int sessionSeconds;
    protected VolunteerTime sessionTime;
    protected int totalSeconds;
    protected VolunteerTime totalTime;

    protected boolean signedIn = false;

    protected Volunteer(int id, String name, int totalSeconds) {
        this.id = id;
        this.name = name;
        this.totalSeconds = totalSeconds;
    }
}