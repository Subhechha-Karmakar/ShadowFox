package com.studentmgmt.model;

/**
 * Student model class - represents a single student record.
 * Auto-calculates GPA based on entered marks.
 */
public class Student {

    public static final String[] SUBJECTS = {
        "Mathematics", "Science", "English", "History", "Computer Science"
    };

    public static final String[] COURSES = {
        "Computer Science", "Mathematics", "Physics", "Chemistry",
        "Biology", "Engineering", "Business", "Arts"
    };

    public static final String[] GENDERS = {
        "Male", "Female", "Other", "Prefer not to say"
    };

    private int id;
    private String name;
    private int age;
    private String course;
    private double[] marks; // indexed same as SUBJECTS, each 0-100
    private double gpa;     // auto-calculated on 4.0 scale

    // New personal detail fields
    private String dob;     // Date of Birth (DD/MM/YYYY)
    private String gender;
    private String mobile;
    private String email;

    public Student(int id, String name, int age, String course, double[] marks) {
        this(id, name, age, course, marks, "", "", "", "");
    }

    public Student(int id, String name, int age, String course, double[] marks,
                   String dob, String gender, String mobile, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
        this.marks = marks.clone();
        this.dob = dob != null ? dob : "";
        this.gender = gender != null ? gender : "";
        this.mobile = mobile != null ? mobile : "";
        this.email = email != null ? email : "";
        this.gpa = calculateGpa();
    }

    // ── GPA Calculation ─────────────────────────────────────────────

    private double calculateGpa() {
        double totalGradePoints = 0;
        for (double mark : marks) {
            totalGradePoints += markToGradePoint(mark);
        }
        return Math.round((totalGradePoints / marks.length) * 100.0) / 100.0;
    }

    public static double markToGradePoint(double mark) {
        if (mark >= 90) return 4.0;
        if (mark >= 85) return 3.7;
        if (mark >= 80) return 3.3;
        if (mark >= 75) return 3.0;
        if (mark >= 70) return 2.7;
        if (mark >= 65) return 2.3;
        if (mark >= 60) return 2.0;
        if (mark >= 55) return 1.7;
        if (mark >= 50) return 1.3;
        if (mark >= 45) return 1.0;
        if (mark >= 40) return 0.7;
        return 0.0;
    }

    public void recalculateGpa() {
        this.gpa = calculateGpa();
    }

    // ── Derived properties ──────────────────────────────────────────

    public double getAverageMarks() {
        double sum = 0;
        for (double m : marks) sum += m;
        return Math.round((sum / marks.length) * 100.0) / 100.0;
    }

    public boolean isPassing() {
        return gpa >= 2.0;
    }

    public String getStatus() {
        return isPassing() ? "PASS" : "FAIL";
    }

    public String getLetterGrade() {
        if (gpa >= 3.7) return "A";
        if (gpa >= 3.3) return "A-";
        if (gpa >= 3.0) return "B+";
        if (gpa >= 2.7) return "B";
        if (gpa >= 2.3) return "B-";
        if (gpa >= 2.0) return "C+";
        if (gpa >= 1.7) return "C";
        if (gpa >= 1.3) return "C-";
        if (gpa >= 1.0) return "D";
        return "F";
    }

    // ── Getters & Setters ───────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public double[] getMarks() { return marks.clone(); }
    public void setMarks(double[] marks) {
        this.marks = marks.clone();
        recalculateGpa();
    }

    public double getMark(int subjectIndex) { return marks[subjectIndex]; }
    public void setMark(int subjectIndex, double mark) {
        marks[subjectIndex] = mark;
        recalculateGpa();
    }

    public double getGpa() { return gpa; }

    // New field getters & setters
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob != null ? dob : ""; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender != null ? gender : ""; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile != null ? mobile : ""; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email != null ? email : ""; }

    @Override
    public String toString() {
        return String.format("Student[id=%d, name=%s, gpa=%.2f, status=%s]",
                id, name, gpa, getStatus());
    }
}
