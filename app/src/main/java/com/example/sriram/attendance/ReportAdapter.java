package com.example.sriram.attendance;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SRIRAM on 17-11-2016.
 */

public class ReportAdapter extends ArrayAdapter<StudentAttendance> {

    private Context context;
    private int resource;
    private List<StudentAttendance> studentAttendances;

    public ReportAdapter(Context context, int resource, List<StudentAttendance> studentAttendances) {
        super(context, resource, studentAttendances);
        this.context = context;
        this.resource = resource;
        this.studentAttendances = studentAttendances;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
        }
        StudentAttendance studentAttendance = studentAttendances.get(position);

        TextView studentNameTV = (TextView) convertView.findViewById(R.id.studentName);
        studentNameTV.setText(studentAttendance.getStudentName());

        TextView idTV = (TextView) convertView.findViewById(R.id.studentID);
        idTV.setText(studentAttendance.getStudentId());

        TextView attendancePercentageTV = (TextView) convertView.findViewById(R.id.attendance);
        attendancePercentageTV.setText(studentAttendance.getAttendancePercentage() + "");

        if (studentAttendance.isPresent()) {
            convertView.setBackgroundColor(Color.GREEN);
        } else {
            convertView.setBackgroundColor(Color.RED);
        }

        return convertView;
    }


}
