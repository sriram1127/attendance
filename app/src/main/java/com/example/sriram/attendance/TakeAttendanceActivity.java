package com.example.sriram.attendance;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TakeAttendanceActivity extends AppCompatActivity {

    private TextView nameTV;
    private TextView codeTV;
    private TextView dateTV;
    private TextView dayTV;
    private ImageView imgIV;
    private TextView msg;
    private TextView studentsPresentTV;
    private LinearLayout ll;
    private DatabaseReference mDatabase;
    private int count = 0;
    private Course course = null;
    private String date = null;
    private String crn = null;
    private String status = null;

    private Button scanBtn = null;
    private String contents = null;
    private WifiManager wifiManager = null;
    private List<ScanResult> scanResults = null;
    private WifiConfiguration netConfig = null;
    private String passcode = null;
    String userMail = null;
    String userId = null;
    String username = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_take_attendance);
        setTitle("Take Attendance");
        nameTV = (TextView) findViewById(R.id.name);
        codeTV = (TextView) findViewById(R.id.code);
        dateTV = (TextView) findViewById(R.id.date);
        dayTV = (TextView) findViewById(R.id.day);
        imgIV = (ImageView) findViewById(R.id.take);
        msg = (TextView) findViewById(R.id.msg);
        ll = (LinearLayout) findViewById(R.id.detail);
        studentsPresentTV = (TextView) findViewById(R.id.studentsPresent);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy EEEE");
        Intent intent = getIntent();

        userMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userId = userMail.substring(0, userMail.indexOf("@"));
        wifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
        if (intent != null) {
            course = (Course) intent.getExtras().get("course");
            passcode = intent.getExtras().getString("passcode");
            mDatabase.child("Professor").child(userId).child("courses").child(course.getCrn()).setValue(passcode);
            codeTV.setText(passcode);
            crn = course.getCrn();
            nameTV.setText(course.getName());
            codeTV.setText(course.getCode());
            String dateTime = sdf.format(new Date());
            String day = dateTime.substring(11);
            date = dateTime.substring(0, 10);
            dateTV.setText(date);
            dayTV.setText(day);
            Log.d("", "");
            String classDay = day.equalsIgnoreCase("Thursday") ? "R" : day.equalsIgnoreCase("Sunday") ? "U" : day.charAt(0) + "";
            if (course.getDay().contains(classDay)) {
                msg.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);
                mDatabase.child("Course").child(course.getCrn()).child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        status = dataSnapshot.getValue(String.class);

                        if ("OPEN".equals(status)) {
                            imgIV.setImageResource(R.drawable.close);
                            imgIV.setTag("close");
                        } else {
                            imgIV.setImageResource(R.drawable.open);
                            imgIV.setTag("open");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                msg.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
            }
        }
    }

    public void countPresence() {
        mDatabase.child("Attendance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> d = dataSnapshot.getChildren().iterator();
                count = 0;
                while (d.hasNext()) {
                    Student student = d.next().getValue(Student.class);
                    StudentCourse sc = student.getStudentCourses().get(crn);
                    if (sc != null) {
                        HashMap<String, Boolean> attendance = sc.getAttendance();
                        if (attendance != null) {
                            if (attendance.get(date)) {
                                ++count;
                            }
                        }
                    }
                    Log.d("", "");
                }
                studentsPresentTV.setText("Student Count : " + count);
                Log.d("", "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void takeAttendance(View view) {
        String imgName = String.valueOf(view.getTag());
        if ("open".equals(imgName)) {
            mDatabase.child("Attendance").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> d = dataSnapshot.getChildren().iterator();
                    while (d.hasNext()) {
                        DataSnapshot ds = d.next();
                        Student student = ds.getValue(Student.class);
                        StudentCourse sc = student.getStudentCourses().get(course.getCrn());
                        if (sc != null) {
                            HashMap<String, Boolean> attendance = sc.getAttendance();
                            if (attendance == null) {
                                attendance = new HashMap<String, Boolean>();
                            }
                            if (attendance.get(date) == null) {
                                attendance.put(date, false);
                            }
                            sc.setAttendance(attendance);
                            String studentUserName = ds.getKey();
                            mDatabase.child("Attendance").child(studentUserName).child("studentCourses").child(crn).setValue(sc);
                        }
                    }
                    countPresence();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mDatabase.child("Course").child(crn).child("status").setValue("OPEN");
            imgIV.setImageResource(R.drawable.close);
            imgIV.setTag("close");


        } else {
            closeHotspot();
            mDatabase.child("Course").child(crn).child("status").setValue("CLOSED");
            imgIV.setImageResource(R.drawable.open);
            imgIV.setTag("open");

        }
    }


    public void scanCode(View v) {
        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
            startActivityForResult(intent, 0);

        } catch (Exception e) {

            Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {

            if (resultCode == RESULT_OK) {
                contents = data.getStringExtra("SCAN_RESULT");
                if (contents != null && !contents.isEmpty() && contents.contains("@")) {
                    String studentPass = contents.split("@")[1];
                    username = contents.split("@")[0];
                    if (passcode.equals(studentPass)) {
                        DatabaseReference dr = mDatabase.child("Attendance").child(username).child("studentCourses").child(course.getCrn())
                                .child("attendance").child(date);
                        dr.setValue(true);
                        mDatabase.child("Attendance").child(username).child("studentCourses").child(course.getCrn()).child("attendance").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, Boolean> attendance = (Map<String, Boolean>) dataSnapshot.getValue();
                                int presence = 0;
                                for (Map.Entry<String, Boolean> entry : attendance.entrySet()) {
                                    if (entry.getValue()) {
                                        ++presence;
                                    }
                                }
                                double attendancePercentage = (presence * 100) / attendance.size();
                                mDatabase.child("Attendance").child(username).child("studentCourses").child(course.getCrn()).child("attendancePercentage").setValue(attendancePercentage);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Wrong QR", Toast.LENGTH_LONG).show();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //handle cancel
            }
        }
    }

    private void closeHotspot() {
        Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();   //Get all declared methods in WifiManager class
        boolean methodFound = false;
        for (Method method : wmMethods) {
            if (method.getName().equals("setWifiApEnabled")) {
                try {
                    method.invoke(wifiManager, netConfig, false);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void createWifiAccessPoint(String name) {
        //  WifiManager wifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
        Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();   //Get all declared methods in WifiManager class
        boolean methodFound = false;
        for (Method method : wmMethods) {
            if (method.getName().equals("setWifiApEnabled")) {
                methodFound = true;
                netConfig = new WifiConfiguration();
                //netConfig.SSID = name;
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                try {
                    boolean apstatus = (Boolean) method.invoke(wifiManager, netConfig, true);
                    //statusView.setText("Creating a Wi-Fi Network \""+netConfig.SSID+"\"");
                    for (Method isWifiApEnabledmethod : wmMethods) {
                        if (isWifiApEnabledmethod.getName().equals("isWifiApEnabled")) {
                            while (!(Boolean) isWifiApEnabledmethod.invoke(wifiManager)) {
                            }
                            ;
                            for (Method method1 : wmMethods) {
                                if (method1.getName().equals("getWifiApState")) {
                                    int apstate;
                                    apstate = (Integer) method1.invoke(wifiManager);

                                    //                    netConfig=(WifiConfiguration)method1.invoke(wifi);
                                    //statusView.append("\nSSID:"+netConfig.SSID+"\nPassword:"+netConfig.preSharedKey+"\n");
                                }
                            }
                        }
                    }
                    if (apstatus) {
                        System.out.println("SUCCESSdddd");
                    } else {
                        System.out.println("FAILED");
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!methodFound) {
            //statusView.setText("Your phone's API does not contain setWifiApEnabled method to configure an access point");
        }
    }

}
