package com.example.mycourseratingapp.Controllers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.mycourseratingapp.Models.Entities.Course;
import com.example.mycourseratingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CoursesOverviewActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "MainActivity";
    private FirebaseFirestore firebaseFirestore;
    private List<Course> courses;
    private List<String> courseTitles;
    private Adapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_overview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();
    }

    private void init(){
        this.firebaseFirestore = FirebaseFirestore.getInstance();

        this.courses = new ArrayList<>();
        this.courseTitles = new ArrayList<>();

        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.courseTitles);

        this.listView = findViewById(R.id.listView);
        this.listView.setAdapter((ListAdapter) adapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {

                Intent intent = new Intent(CoursesOverviewActivity.this, CourseDetailsActivity.class);

                intent.putExtra("Course", courses.get(position));

                startActivity(intent);
            }
        });

        findViewById(R.id.signOutButton).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //  loadData
        fetchCoursesFromDB();
    }

    public void fetchCoursesFromDB()
    {
        this.courses.clear();

        firebaseFirestore.collection("courses")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                courses.add(document.toObject(Course.class));
                            }
                            reloadAllData();
                        }
                        else
                        {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void reloadAllData(){

        this.courseTitles.clear();

        for (Course c : courses)
        {
            this.courseTitles.add(c.getTitle());
        }

        // update data in our adapter
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.courseTitles);

        this.listView.setAdapter((ListAdapter) this.adapter);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.signOutButton) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(this, MainActivity.class);

            startActivity(intent);
        }
    }
}
