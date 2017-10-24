package com.example.sriram.attendance;

import java.util.HashMap;

/**
 * Created by SRIRAM on 17-11-2016.
 */

public class StudentUtils {

    public static HashMap<Integer, String> days = new HashMap<Integer, String>();

    static {
        days.put(0, "U");
        days.put(1, "M");
        days.put(2, "T");
        days.put(3, "W");
        days.put(4, "R");
        days.put(5, "F");
        days.put(6, "S");
    }
}
