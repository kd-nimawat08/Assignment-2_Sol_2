package com.example.university.service;

import com.example.university.model.Student;
import com.example.university.model.Course;

import com.example.university.repository.CourseJpaRepository;
import com.example.university.repository.StudentJpaRepository;
import com.example.university.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class StudentJpaService implements StudentRepository {

    @Autowired
    private StudentJpaRepository studentJpaRepository;

    @Autowired
    private CourseJpaRepository courseJpaRepository;

    @Override
    public ArrayList<Student> getStudents() {
        List<Student> studentsList = studentJpaRepository.findAll();
        ArrayList<Student> students = new ArrayList<>(studentsList);
        return students;
    }

    @Override
    public Student getStudentById(int studentId) {
        try {
            Student student = studentJpaRepository.findById(studentId).get();
            return student;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Student addStudent(Student student) {
        List<Integer> courseIds = new ArrayList<>();

        for (Course course : student.getCourses()) {
            courseIds.add(course.getCourseId());
        }
        List<Course> courses = courseJpaRepository.findAllById(courseIds);
        if (courses.size() != courseIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more courses are not found");
        }

        student.setCourses(courses);
        for (Course course : courses) {
            course.getStudents().add(student);
        }

        studentJpaRepository.save(student);

        courseJpaRepository.saveAll(courses);
        return student;
    }

    @Override
    public Student updateStudent(int studentId, Student student) {

        try {
            Student existingStudent = studentJpaRepository.findById(studentId).get();
            if (student.getStudentName() != null) {
                existingStudent.setStudentName(student.getStudentName());
            }
            if (student.getEmail() != null) {
                existingStudent.setEmail(student.getEmail());
            }
            if (student.getCourses() != null) {
                List<Course> existingCourses = existingStudent.getCourses();

                for (Course course : existingCourses) {
                    course.getStudents().remove(existingStudent);
                }
                courseJpaRepository.saveAll(existingCourses);
                List<Integer> courseIds = new ArrayList<>();

                for (Course course : student.getCourses()) {
                    courseIds.add(course.getCourseId());
                }

                List<Course> courses = courseJpaRepository.findAllById(courseIds);

                if (courses.size() != courseIds.size()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more courses are not found");
                }
                existingStudent.setCourses(courses);
                for (Course course : courses) {
                    course.getStudents().add(existingStudent);
                }
                courseJpaRepository.saveAll(courses);
            }
            studentJpaRepository.save(existingStudent);
            return existingStudent;
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void deleteStudent(int studentId) {
        try {
            Student student = studentJpaRepository.findById(studentId).get();

            List<Course> courses = student.getCourses();

            for (Course course : courses) {
                course.getStudents().remove(student);
            }
            courseJpaRepository.saveAll(courses);
            studentJpaRepository.deleteById(studentId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @Override
    public List<Course> getCoursesOfStudent(int studentId) {
        try {
            Student student = studentJpaRepository.findById(studentId).get();
            List<Course> courses = student.getCourses();
            return courses;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}