package com.example;
import java.util.HashMap;

public class GPA {
    public static void main(String[] args) {
        HashMap<String, Integer> students = new HashMap<>();
        students.put("John", 3.8);
        students.put("Jane", 4.0);
        students.put("Mike", 3.2);

        for (String name : students.keySet()) {
            System.out.println(name + ": " + students.get(name));
        }
    }
}