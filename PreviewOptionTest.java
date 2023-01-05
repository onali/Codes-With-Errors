package com.example;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.tools.javac.code.Source;

public class PreviewOptionTest {
    public static void main(String... args) throws Exception {
        PreviewOptionTest t = new PreviewOptionTest();
        t.run();
    }

    public void run() throws Exception {
        try (FileWriter out = new FileWriter("Test.java")) {
            out.write("class Test { }");
        }

        testWithNoFlags();

        List<Source> versionsToTest = Stream.of(Source.values())
                .filter(s -> s.compareTo(Source.MIN) >= 0)
                .collect(Collectors.toList());

        versionsToTest.forEach(this::testWithSourceFlag);
        versionsToTest.forEach(this::testWithReleaseFlag);

        if (errors > 0)
            throw new Exception(errors + " errors occurred");
    }

    void testWithNoFlags() {
        testInternal(null, null, true);
    }

    void testWithSourceFlag(Source source) {
        testInternal(source, null, source != Source.DEFAULT);
    }

    void testWithReleaseFlag(Source release) {
        testInternal(null, release, release != Source.DEFAULT);
    }

    void testInternal(Source source, Source release, boolean shouldFail) {
        System.err.println("Test: source:" + source + ", release:" + release + " " + shouldFail + " " + shouldFail);
        List<String> args = new ArrayList<>();
        args.add("--enable-preview");
        if (source != null) {
            args.add("-source");
            args.add(source.name);
        }
        if (release != null) {
            args.add("--release");
            args.add(release.name);
        }
        args.add("Test.java");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        int rc = com.sun.tools.javac.Main.compile(args.toArray(new String[args.size()]), pw);
        pw.close();
        boolean hasErrors = rc != 0;
        if (hasErrors != shouldFail) {
            if (hasErrors) {
                String out = sw.toString();
                error("error not expected but found:\n" + out);
            } else {
                error("error expected but not found");
            }
        }
    }

    void error(String msg) {
        System.err.println("error: " + msg);
        errors++;
    }

    int errors;
}