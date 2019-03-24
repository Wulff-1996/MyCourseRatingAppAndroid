package com.example.mycourseratingapp.Controllers;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mycourseratingapp.R;
import com.example.mycourseratingapp.Models.Entities.Course;
import com.example.mycourseratingapp.Models.Entities.CourseRating;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CourseRatingsActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private Course course;
    private CourseRating courseRating;
    private FirebaseFirestore firebaseFirestore;
    private List<CourseRating> courseRatings;

    private RatingBar subjectRelevanceRatingBar;
    private RatingBar performanceRatingBar;
    private RatingBar preparationRatingBar;
    private RatingBar feedbackRatingBar;
    private RatingBar examplesRatingBar;
    private RatingBar jobOpportunitiesRatingBar;

    private TextView headlineTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_course);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firebaseFirestore = FirebaseFirestore.getInstance();

        init();
    }

    @SuppressLint("SetTextI18n")
    private void init()
    {
        course = getIntent().getParcelableExtra("Course");

        courseRatings = new ArrayList<>();

        this.headlineTextView = findViewById(R.id.rateCourseTextView);
        headlineTextView.setText("All Ratings");

        this.subjectRelevanceRatingBar = findViewById(R.id.subjectRelevanceRatingBar);
        this.performanceRatingBar = findViewById(R.id.perormanceRatingBar);
        this.preparationRatingBar = findViewById(R.id.preperationRatingBar);
        this.feedbackRatingBar = findViewById(R.id.feedbackRatingBar);
        this.examplesRatingBar = findViewById(R.id.examplesRatingBar);
        this.jobOpportunitiesRatingBar = findViewById(R.id.jobOpportunitiesRatingBar);

        subjectRelevanceRatingBar.setIsIndicator(true);
        performanceRatingBar.setIsIndicator(true);
        preparationRatingBar.setIsIndicator(true);
        feedbackRatingBar.setIsIndicator(true);
        examplesRatingBar.setIsIndicator(true);
        jobOpportunitiesRatingBar.setIsIndicator(true);

        Button rateButton = findViewById(R.id.rateButton);
        rateButton.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getRatings();
    }

    private void getRatings()
    {
        final List<DocumentSnapshot> documentSnapshots = new ArrayList<>();

        firebaseFirestore.collection("courseRatings").document(course.getId()).collection("ratings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                           for (DocumentSnapshot document: task.getResult().getDocuments())
                            {
                                getRating(document, task.getResult().getDocuments().size());
                            }
                        }
                        else
                        {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getRating(DocumentSnapshot document, final int count)
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
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        courseRatings.add(document.toObject(CourseRating.class));
                        calculateRatingAverage(courseRatings, count);
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

    private void calculateRatingAverage(List<CourseRating> courseRatings, int count)
    {
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

        subjectRating = subjectRating / count;
        performanceRating = performanceRating / count;
        preparationRating = preparationRating / count;
        feedbackRating = feedbackRating / count;
        examplesRating = examplesRating / count;
        jobOpportunitiesRating = jobOpportunitiesRating / count;

        this.courseRating = new CourseRating();
        courseRating.setSubjectRelevance(subjectRating);
        courseRating.setPerformance(performanceRating);
        courseRating.setPreparation(preparationRating);
        courseRating.setFeedBack(feedbackRating);
        courseRating.setExamples(examplesRating);
        courseRating.setJobOpportunities(jobOpportunitiesRating);

        subjectRelevanceRatingBar.setRating((float) courseRating.getSubjectRelevance());
        performanceRatingBar.setRating((float) courseRating.getPerformance());
        preparationRatingBar.setRating((float) courseRating.getPreparation());
        feedbackRatingBar.setRating((float) courseRating.getFeedBack());
        examplesRatingBar.setRating((float) courseRating.getExamples());
        jobOpportunitiesRatingBar.setRating((float) courseRating.getJobOpportunities());
    }
}
