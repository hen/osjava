package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

public class TimeOfDay {

    private int hours;
    private int minutes;

    public TimeOfDay(Date d) {
        this.hours = d.getHours();
        this.minutes = d.getMinutes();
    }

    public TimeOfDay(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public int asMinutes() {
        return this.hours * 60 + this.minutes;
    }

    public double asHours() {
        return this.hours + this.minutes/60;
    }

    public String toString() {
        return this.hours+":"+this.minutes;
    }

}
