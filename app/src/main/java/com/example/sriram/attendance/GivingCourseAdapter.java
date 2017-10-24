package com.example.sriram.attendance;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SRIRAM on 16-11-2016.
 */

public class GivingCourseAdapter extends RecyclerView.Adapter<GivingCourseAdapter.ViewHolder> {

    private Context context;
    private List<Course> courses;

    public GivingCourseAdapter(Context context, List<Course> courses) {
        this.context = context;
        this.courses = courses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.givingcourse, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.courseNameTV.setText(course.getName());
        holder.courseCodeTV.setText(course.getCode());
        holder.studentCountTV.setText(course.getStudentCount().toString());
        holder.courseTimeTV.setText(course.getDay() + " " + course.getTime());

    }

    @Override
    public int getItemCount() {
        return courses.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView courseNameTV = null;
        public TextView courseCodeTV = null;
        public TextView studentCountTV = null;
        public TextView courseTimeTV = null;
        public ImageView reportTV = null;
        public ImageView attendanceTV = null;
        Context context = null;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            courseNameTV = (TextView) itemView.findViewById(R.id.coursename);
            courseCodeTV = (TextView) itemView.findViewById(R.id.coursecode);
            studentCountTV = (TextView) itemView.findViewById(R.id.studentcount);
            courseTimeTV = (TextView) itemView.findViewById(R.id.coursetime);
            reportTV = (ImageView) itemView.findViewById(R.id.report);
            reportTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToReportActivity(getAdapterPosition());
                }
            });

            attendanceTV = (ImageView) itemView.findViewById(R.id.attendance);
            attendanceTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToTakeAttendanceActivity(getAdapterPosition());
                }
            });
        }

        public void moveToTakeAttendanceActivity(int position) {
            ((ProfessorMainActivity) context).moveToTakeAttendanceActivity(position);

        }
        public void moveToReportActivity(int position) {
            ((ProfessorMainActivity) context).moveToReportActivity(position);

        }

    }
}
