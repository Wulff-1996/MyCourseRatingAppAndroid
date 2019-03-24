package com.example.mycourseratingapp.Controllers;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mycourseratingapp.Models.Entities.Course;
import com.example.mycourseratingapp.Models.Entities.CourseRating;
import com.example.mycourseratingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "MainActivity";

    private Course course;
    private FirebaseFirestore firebaseFirestore;

    private TextView titleTextView;
    private TextView typeTextView;
    private TextView semesterTextView;
    private TextView teacherTextView;

    private RatingBar averageRatingBar;

    private Button seeAllRatingsButton;
    private Button showRateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        init();
    }

    private void init()
    {
        course = getIntent().getParcelableExtra("Course");
        firebaseFirestore = FirebaseFirestore.getInstance();

        this.titleTextView = findViewById(R.id.titleTextView);
        this.typeTextView = findViewById(R.id.courseTypeTextView);
        this.semesterTextView = findViewById(R.id.semesterTextView);
        this.teacherTextView = findViewById(R.id.teacherTextView);
        this.averageRatingBar = findViewById(R.id.courseAverageRatingBar);
        this.seeAllRatingsButton = findViewById(R.id.allRatingsButton);
        this.showRateButton = findViewById(R.id.showRateButton);

        this.seeAllRatingsButton.setOnClickListener(this);
        this.showRateButton.setOnClickListener(this);

        titleTextView.setText(String.valueOf(course.getTitle()));
        typeTextView.setText(String.valueOf(course.getCourseType()));
        semesterTextView.setText(String.valueOf(course.getSemester()));
        teacherTextView.setText(String.valueOf(course.getTeacher()));

        this.averageRatingBar.setIsIndicator(true);
    }

    private void getAllRatingsForCourse()
    {
        final int count[] = {0};

        final List<CourseRating> courseRatings = new ArrayList<>();

        firebaseFirestore.collection("courseRatings").document(course.getId()).collection("ratings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();
                            count[0] = myListOfDocuments.size();

                            for (DocumentSnapshot document: myListOfDocuments)
                            {

                                DocumentReference docRef = firebaseFirestore.collection("courseRatings")
                                        .document(course.getId())
                                        .collection("ratings").document(document.getId());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists())
                                            {
                                                courseRatings.add(document.toObject(CourseRating.class));
                                                calculateRatingAverage(count, courseRatings);
                                            }
                                            else
                                            {
                                                Log.d(TAG, "No such document");
                                            }
                                        }
                                        else
                                        {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        }
                        else
                        {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void calculateRatingAverage(int[] count, List<CourseRating> courseRatings)
    {
        float average;

        double subjectRating = 0;
        double performanceRating = 0;
        double preparationRating = 0;
        double feedbackRating = 0;
        double examplesRating = 0;
        double jobOpportunitiesRating = 0;

        for (CourseRating cr: courseRatings)
        {
            subjectRating += cr.getSubjectRelevance();
            performanceRating += cr.getPerformance();
            preparationRating += cr.getPreparation();
            feedbackRating += cr.getFeedBack();
            examplesRating += cr.getExamples();
            jobOpportunitiesRating += cr.getJobOpportunities();
        }

        subjectRating = subjectRating / count[0];
        performanceRating = performanceRating / count[0];
        preparationRating = preparationRating / count[0];
        feedbackRating = feedbackRating / count[0];
        examplesRating = examplesRating / count[0];
        jobOpportunitiesRating = jobOpportunitiesRating / count[0];

        average = (float) ((subjectRating + performanceRating
                + preparationRating + feedbackRating
                + examplesRating + jobOpportunitiesRating) / 6);

        this.averageRatingBar.setRating (average);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSavedInstance()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        getAllRatingsForCourse();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == findViewById(R.id.allRatingsButton).getId())
        {
            Intent intent = new Intent(CourseDetailsActivity.this, CourseRatingsActivity.class);

            intent.putExtra("Course", course);

            startActivity(intent);
        }
        else if (i == findViewById(R.id.showRateButton).getId())
        {
            Intent intent = new Intent(CourseDetailsActivity.this, RateCourseActivity.class);

            intent.putExtra("Course", course);

            startActivity(intent);
        }
    }
}
