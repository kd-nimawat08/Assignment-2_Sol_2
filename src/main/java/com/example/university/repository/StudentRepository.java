package com.example.university.repository;

import com.example.university.model.Student;
import com.example.university.model.Course;

import java.util.*;

public interface StudentRepository {
    ArrayList<Student> getStudents();

    Student getStudentById(int studentId);

    Student addStudent(Student student);

    Student updateStudent(int studentId, Student student);

    void deleteStudent(int studentId);

    List<Course> getCoursesOfStudent(int studentId);
}