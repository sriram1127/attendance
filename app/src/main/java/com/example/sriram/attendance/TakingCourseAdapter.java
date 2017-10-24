package com.example.sriram.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SRIRAM on 08-11-2016.
 */
public class TakingCourseAdapter extends ArrayAdapter<Course> {


    private Context context;
    private int resource;
    private List<Course> courses;

    public TakingCourseAdapter(Context context, int resource, List<Course> courses) {
        super(context, resource, courses);
        this.context = context;
        this.resource = resource;
        this.courses = courses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, parent, false);
        }
        Course course = courses.get(position);

        TextView coursenameTV = (TextView) convertView.findViewById(R.id.coursename);
        coursenameTV.setText(course.getName());

        TextView coursecodeTV = (TextView) convertView.findViewById(R.id.coursecode);
        coursecodeTV.setText(course.getCode());

        TextView professornameTV = (TextView) convertView.findViewById(R.id.professorname);
        professornameTV.setText(course.getProfessorName());

        TextView coursetimeTV = (TextView) convertView.findViewById(R.id.coursetime);
        coursetimeTV.setText(course.getDay() + " " + course.getTime());

        return convertView;
    }


}
