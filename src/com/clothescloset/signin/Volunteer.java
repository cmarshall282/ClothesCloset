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

    protected void signIn() {
        startTime = System.currentTimeMillis();
        signedIn = true;

        System.out.println(name + " has signed in.");
        System.out.println();
    }

    protected void signOut() {
        endTime = System.currentTimeMillis();
        sessionSeconds = (int) (endTime - startTime) / 1000;
        totalSeconds += sessionSeconds;
        sessionTime = new VolunteerTime(sessionSeconds);

        signedIn = false;

        System.out.println(name + " has signed out.");
        System.out.println();
    }
}
