package com.example.mycourseratingapp.Models.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class CourseRating implements Parcelable
{
    private double subjectRelevance;
    private double Performance;
    private double preparation;
    private double feedBack;
    private double examples;
    private double jobOpportunities;

    public CourseRating() {
    }

    public CourseRating(double subjectRelevance, double performance, double preparation, double feedBack, double examples, double jobOpportunities) {
        this.subjectRelevance = subjectRelevance;
        Performance = performance;
        this.preparation = preparation;
        this.feedBack = feedBack;
        this.examples = examples;
        this.jobOpportunities = jobOpportunities;
    }

    protected CourseRating(Parcel in) {
        subjectRelevance = in.readDouble();
        Performance = in.readDouble();
        preparation = in.readDouble();
        feedBack = in.readDouble();
        examples = in.readDouble();
        jobOpportunities = in.readDouble();
    }

    public static final Creator<CourseRating> CREATOR = new Creator<CourseRating>() {
        @Override
        public CourseRating createFromParcel(Parcel in) {
            return new CourseRating(in);
        }

        @Override
        public CourseRating[] newArray(int size) {
            return new CourseRating[size];
        }
    };

    public double getSubjectRelevance() {
        return subjectRelevance;
    }

    public void setSubjectRelevance(double subjectRelevance) {
        this.subjectRelevance = subjectRelevance;
    }

    public double getPerformance() {
        return Performance;
    }

    public void setPerformance(double performance) {
        Performance = performance;
    }

    public double getPreparation() {
        return preparation;
    }

    public void setPreparation(double preparation) {
        this.preparation = preparation;
    }

    public double getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(double feedBack) {
        this.feedBack = feedBack;
    }

    public double getExamples() {
        return examples;
    }

    public void setExamples(double examples) {
        this.examples = examples;
    }

    public double getJobOpportunities() {
        return jobOpportunities;
    }

    public void setJobOpportunities(double jobOpportunities) {
        this.jobOpportunities = jobOpportunities;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(subjectRelevance);
        dest.writeDouble(Performance);
        dest.writeDouble(preparation);
        dest.writeDouble(feedBack);
        dest.writeDouble(examples);
        dest.writeDouble(jobOpportunities);
    }
}
