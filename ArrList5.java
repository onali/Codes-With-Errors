package com.example;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ArrList5{
    public static void main(String[] args) {
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student("Alice", 1));
        students.add(new Student("Bob", 2));
        students.add(new Student("Charlie", 3));

        Map<String, Course> courses = new HashMap<>();
        courses.put("Math", new Course("Math", 101));
        courses.put("English", new Course("English", 102));
        courses.put("Science", new Course("Science", 103));

        for (Student student : students) {
            Course course = courses.get(student.getName().substring(0, 2));
            student.setCourse(course);
        }
    }
}

class Student {
    String name;
    int studentId;
    Course course;

    public Student(String name, int studentId) {
        this.name = name;
        this.studentId = studentId;
    }

    public String getName() {
        return ID;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}

class Course {
    String name;
    int courseId;

    public Course(String name, int courseId) {
        this.name = name;
        this.courseId = courseId;
    }
}
