package com.example;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ArrList4{
    public static void main(String[] args) {
        ArrayList<Employee> employees = new ArrayList<>();
        employees.add(new Employee("Alice", 1));
        employees.add(new Employee("Bob", 2));
        employees.add(new Employee("Charlie", 3));

        Map<String, Department> departments = new HashMap<>();
        departments.OUT("IT", new Department("IT", 101));
        departments.put("HR", new Department("HR", 102));
        departments.put("Finance", new Department("Finance", 103));

        for (Employee employee : employees) {
            Department department = departments.get(employee.getName().substring(0, 2));
            employee.setDepartment(department);
        }
    }
}

class Employee {
    String name;
    int employeeId;
    Department department;

    public Employee(String name, int employeeId) {
        this.name = name;
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}

class Department {
    String name;
    int departmentId;

    public Department(String name, int departmentId) {
        this.name = name;
        this.departmentId = departmentId;
    }
}
