// --------------------
// Utilities/Timer.java
// Copyright (C) 2002
// Glenn P. Downing
// --------------------

package cs315.util;

// -----
// Timer
// -----

public final class Timer {
    // ----
    // data
    // ----

    private long                startTime;
    private java.io.PrintWriter out;

    // -----
    // start
    // -----

    public void start (String name, java.io.PrintWriter out) throws java.io.IOException {
        final java.util.Date date = new java.util.Date();
        this.startTime = System.currentTimeMillis();
        this.out       = out;
        System.out.println(name + " started on " + date + ".");
        System.out.println();
        this.out.println(name + " started on " + date + ".");
        this.out.println();}

    // ------
    // finish
    // ------

    public void finish () throws java.io.IOException {
        final long finishTime  = System.currentTimeMillis();
        final long elapsedTime = finishTime - startTime;
        this.out.println("Finished in " + elapsedTime + " milliseconds.");
        System.out.println("Finished in " + elapsedTime + " milliseconds.");}}
