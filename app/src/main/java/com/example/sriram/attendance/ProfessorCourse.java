package com.example.sriram.attendance;

import java.util.HashMap;

/**
 * Created by SRIRAM on 16-11-2016.
 */

public class ProfessorCourse {
    public HashMap<String, String> getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(HashMap<String, String> attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    private HashMap<String, String> attendanceStatus;
}
