package com.example.mycourseratingapp.Models.Entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Course implements Parcelable {

    private java.lang.String id;
    private java.lang.String title;
    private java.lang.String description;
    private java.lang.String courseType;
    private int semester;
    private java.lang.String teacher;
    private String teacherEmail;

    public Course() {
    }

    public Course(String id, String title, String description, String courseType, int semester, String teacher, String teacherEmail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.courseType = courseType;
        this.semester = semester;
        this.teacher = teacher;
        this.teacherEmail = teacherEmail;
    }

    protected Course(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        courseType = in.readString();
        semester = in.readInt();
        teacher = in.readString();
        teacherEmail = in.readString();
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public java.lang.String getId() {
        return id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public java.lang.String getTitle() {
        return title;
    }

    public void setTitle(java.lang.String title) {
        this.title = title;
    }

    public java.lang.String getDescription() {
        return description;
    }

    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    public java.lang.String getCourseType() {
        return courseType;
    }

    public void setCourseType(java.lang.String courseType) {
        this.courseType = courseType;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public java.lang.String getTeacher() {
        return teacher;
    }

    public void setTeacher(java.lang.String teacher) {
        this.teacher = teacher;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", courseType='" + courseType + '\'' +
                ", semester=" + semester +
                ", teacher='" + teacher + '\'' +
                ", teacherEmail='" + teacherEmail + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(courseType);
        dest.writeInt(semester);
        dest.writeString(teacher);
        dest.writeString(teacherEmail);
    }
}
