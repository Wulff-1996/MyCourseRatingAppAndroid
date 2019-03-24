package com.example.mycourseratingapp.Controllers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.mycourseratingapp.Models.Entities.Course;
import com.example.mycourseratingapp.Models.Entities.CourseRating;
import com.example.mycourseratingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RateCourseActivity extends AppCompatActivity implements
        RatingBar.OnRatingBarChangeListener, View.OnClickListener {

    private static String TAG = "MainActivity";

    private Course course;
    private CourseRating courseRating;
    private FirebaseFirestore firebaseFirestore;

    private RatingBar subjectRelevanceRatingBar;
    private RatingBar performanceRatingBar;
    private RatingBar preparationRatingBar;
    private RatingBar feedbackRatingBar;
    private RatingBar examplesRatingBar;
    private RatingBar jobOpportunitiesRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_course);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firebaseFirestore = FirebaseFirestore.getInstance();
        init();
    }

    private void init()
    {
        course = getIntent().getParcelableExtra("Course");
        courseRating = new CourseRating();

        this.subjectRelevanceRatingBar = findViewById(R.id.subjectRelevanceRatingBar);
        this.performanceRatingBar = findViewById(R.id.perormanceRatingBar);
        this.preparationRatingBar = findViewById(R.id.preperationRatingBar);
        this.feedbackRatingBar = findViewById(R.id.feedbackRatingBar);
        this.examplesRatingBar = findViewById(R.id.examplesRatingBar);
        this.jobOpportunitiesRatingBar = findViewById(R.id.jobOpportunitiesRatingBar);


        this.subjectRelevanceRatingBar.setOnRatingBarChangeListener(this);
        this.performanceRatingBar.setOnRatingBarChangeListener(this);
        this.preparationRatingBar.setOnRatingBarChangeListener(this);
        this.feedbackRatingBar.setOnRatingBarChangeListener(this);
        this.examplesRatingBar.setOnRatingBarChangeListener(this);
        this.jobOpportunitiesRatingBar.setOnRatingBarChangeListener(this);

        Button rateButton = findViewById(R.id.rateButton);
        rateButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCourseRating();
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        ratingBar.setRating(rating);

        if (ratingBar.getId() == R.id.subjectRelevanceRatingBar){
            courseRating.setSubjectRelevance(rating);
        } else if (ratingBar.getId() == R.id.perormanceRatingBar) {
            courseRating.setPerformance(rating);
        } else if (ratingBar.getId() == R.id.preperationRatingBar){
            courseRating.setPreparation(rating);
        } else if (ratingBar.getId() == R.id.feedbackRatingBar){
            courseRating.setFeedBack(rating);
        } else if (ratingBar.getId() == R.id.examplesRatingBar){
            courseRating.setExamples(rating);
        } else if (ratingBar.getId() == R.id.jobOpportunitiesRatingBar){
            courseRating.setJobOpportunities(rating);
        }
    }

    public void add(CourseRating courseRating)
    {
        firebaseFirestore.collection("courseRatings").document(course.getId())
                .collection("ratings").document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .set(courseRating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Rating successfully added to the Database",
                                Toast.LENGTH_LONG);

                        toast.show();

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void getCourseRating()
    {
        DocumentReference docRef = firebaseFirestore.collection("courseRatings")
                .document(course.getId())
                .collection("ratings").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
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

                        setCourseRating(document.toObject(CourseRating.class));
                        updateRating();
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

    @SuppressLint("IntentReset")
    private void sendEmail()
    {
        Log.i("Send email", "");

        String message = "Hello " + course.getTeacher() +"."
                + "\n\nYou have received a new Rating."
                + "\nCourse that was rated is: " + course.getTitle() +"."
                + "\nRated by: " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + "."
                + "\n\nSubject relevance: " + String.valueOf(subjectRelevanceRatingBar.getRating()) + "."
                + "\n\nPerformance: " + String.valueOf(performanceRatingBar.getRating()) + "."
                + "\n\nPreparation: " + String.valueOf(preparationRatingBar.getRating()) + "."
                + "\n\nFeedback: " + String.valueOf(feedbackRatingBar.getRating()) + "."
                + "\n\nQuality of Examples: " + String.valueOf(examplesRatingBar.getRating()) + "."
                + "\n\nJop Opportunities: " + String.valueOf(jobOpportunitiesRatingBar.getRating()) + ".";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, course.getTeacherEmail());
        intent.setData(Uri.parse("mailto:"+course.getTeacherEmail()));
        intent.putExtra(Intent.EXTRA_SUBJECT, "New Rating");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        try {

            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            e.printStackTrace();
            Log.d("Email error:",e.toString());
        }
    }

    private void updateRating()
    {
        this.subjectRelevanceRatingBar.setRating((float) this.courseRating.getSubjectRelevance());
        this.performanceRatingBar.setRating((float) this.courseRating.getPerformance());
        this.preparationRatingBar.setRating((float) this.courseRating.getPreparation());
        this.feedbackRatingBar.setRating((float) this.courseRating.getFeedBack());
        this.examplesRatingBar.setRating((float) this.courseRating.getExamples());
        this.jobOpportunitiesRatingBar.setRating((float) this.courseRating.getJobOpportunities());
    }

    private void setCourseRating(CourseRating courseRating){this.courseRating = courseRating;}

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == findViewById(R.id.rateButton).getId())
        {
            add(courseRating);
            sendEmail();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSavedInstance()");
        outState.putParcelable ("COURSE_RATING", courseRating);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState()");

        courseRating = savedInstanceState.getParcelable("COURSE_RATING");

        Log.d(TAG, courseRating.toString());

        if(courseRating != null)
        {
            subjectRelevanceRatingBar.setRating((float) courseRating.getSubjectRelevance());
            performanceRatingBar.setRating((float) courseRating.getPerformance());
            preparationRatingBar.setRating((float) courseRating.getPreparation());
            feedbackRatingBar.setRating((float) courseRating.getFeedBack());
            examplesRatingBar.setRating((float) courseRating.getExamples());
            jobOpportunitiesRatingBar.setRating((float) courseRating.getJobOpportunities());
        }
    }
}
