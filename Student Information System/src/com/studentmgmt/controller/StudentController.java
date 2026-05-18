package com.studentmgmt.controller;

import com.studentmgmt.model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller – manages student records and provides business logic.
 */
public class StudentController {

    private final List<Student> students = new ArrayList<>();

    // ── CRUD Operations ─────────────────────────────────────────────

    public boolean addStudent(Student student) {
        if (getStudentById(student.getId()) != null) {
            return false; // duplicate ID
        }
        students.add(student);
        return true;
    }

    public boolean updateStudent(int id, String name, int age, String course, double[] marks,
                                  String dob, String gender, String mobile, String email) {
        Student s = getStudentById(id);
        if (s == null) return false;
        s.setName(name);
        s.setAge(age);
        s.setCourse(course);
        s.setMarks(marks);
        s.setDob(dob);
        s.setGender(gender);
        s.setMobile(mobile);
        s.setEmail(email);
        return true;
    }

    public boolean deleteStudent(int id) {
        return students.removeIf(s -> s.getId() == id);
    }

    public Student getStudentById(int id) {
        return students.stream().filter(s -> s.getId() == id).findFirst().orElse(null);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public List<Student> searchStudents(String query) {
        String q = query.toLowerCase().trim();
        return students.stream()
                .filter(s -> s.getName().toLowerCase().contains(q)
                        || String.valueOf(s.getId()).contains(q)
                        || s.getCourse().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    // ── Statistics ──────────────────────────────────────────────────

    public int getPassCount() {
        return (int) students.stream().filter(Student::isPassing).count();
    }

    public int getFailCount() {
        return students.size() - getPassCount();
    }

    public double getAverageGpa() {
        if (students.isEmpty()) return 0;
        double sum = students.stream().mapToDouble(Student::getGpa).sum();
        return Math.round((sum / students.size()) * 100.0) / 100.0;
    }

    public int getTotalStudents() {
        return students.size();
    }
}
