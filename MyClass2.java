package com.example;

import java.util.ArrayList;
import java.util.List;

public class MyClass2 {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2");
        list.add("item3");

        MyClass2 main = new MyClass2();
        main.printList(list);
    }

    public void printList(List<String> list) {
        for (String item : list) {
            System.out.println(item);
            System.out.println(y);
        }
    }
}