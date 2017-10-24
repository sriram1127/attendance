package com.example.sriram.attendance;

import java.util.HashMap;

/**
 * Created by SRIRAM on 09-11-2016.
 */
public class StudentCourse {
    public HashMap<String, Boolean> getAttendance() {
        return attendance;
    }

    public void setAttendance(HashMap<String, Boolean> attendance) {
        this.attendance = attendance;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    private HashMap<String, Boolean> attendance;

    private double attendancePercentage;
}
