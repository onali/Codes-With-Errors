package com.example;

import java.util.ArrayList;
import java.util.List;

public class MyClass {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2")
        list.add("item3");

        MyClass main = new MyClass();
        main.printList(list);
    }

    public void printList(List<String> list) {
        for (String item : list) {
            System.out.println(item);
        }
    }
