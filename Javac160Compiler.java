package com.example;

import java.io.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

// Uses JDK 1.6.0 tools classes
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;



public class Javac160Compiler extends Javac160FilteringCompiler {
    public Javac160Compiler(JavaVersion.FullVersion version, String location, List<? extends File> defaultBootClassPath) {
        super(version, location, defaultBootClassPath);
    }

    public boolean isAvailable() {
        try {
            // Diagnostic was introduced in the Java 1.6 compiler
            Class<?> diagnostic = Class.forName("javax.tools.Diagnostic");
            diagnostic.getMethod("getKind");
            // The Apple Java 6 Developer Preview 6 (javase6release1dp6.dmg) doesn't contain this class, and because
            // of that, it can't be used as a Java 6 compiler
            Class.forName("javax.lang.model.element.Name");
            // javax.tools.Diagnostic is also found in rt.jar; to test if tools.jar
            // is availble, we need to test for a class only found in tools.jar
            Class.forName("com.sun.tools.javac.main.JavaCompiler");
            return true;
        }
        catch (Exception e) { return false; }
        catch (LinkageError e) { return false; }
    }


    /** Compile the given files.
     *  @param files  Source files to compile.
     *  @param classPath  Support jars or directories that should be on the classpath.  If @code{null}, the default is used.
     *  @param sourcePath  Location of additional sources to be compiled on-demand.  If @code{null}, the default is used.
     *  @param destination  Location (directory) for compiled classes.  If @code{null}, the default in-place location is used.
     *  @param bootClassPath  The bootclasspath (contains Java API jars or directories); should be consistent with @code{sourceVersion}
     *                        If @code{null}, the default is used.
     *  @param sourceVersion  The language version of the sources.  Should be consistent with @code{bootClassPath}.  If @code{null},
     *                        the default is used.
     *  @param showWarnings  Whether compiler warnings should be shown or ignored.
     *  @return Errors that occurred. If no errors, should be zero length (not null).
     */
    public List<? extends DJError> compile(List<? extends File> files, List<? extends File> classPath,
                                           List<? extends File> sourcePath, File destination,
                                           List<? extends File> bootClassPath, String sourceVersion, boolean showWarnings) {
        debug.logStart("compile()");
        debug.logValues(new String[]{ "this", "files", "classPath", "sourcePath", "destination", "bootClassPath",
                        "sourceVersion", "showWarnings" },
                this, files, classPath, sourcePath, destination, bootClassPath, sourceVersion, showWarnings);
        java.util.List<File> filteredClassPath = getFilteredClassPath(classPath);

        Context context = _createContext(filteredClassPath, sourcePath, destination, bootClassPath, sourceVersion, showWarnings);
        LinkedList<DJError> errors = new LinkedList<DJError>();
        new CompilerErrorListener(context, errors);

        JavaCompiler compiler = JavaCompiler.instance(context);

        /** Default FileManager provided by Context class */
        DefaultFileManager fileManager = (DefaultFileManager) context.get(JavaFileManager.class);
        com.sun.tools.javac.util.List<JavaFileObject> fileObjects = com.sun.tools.javac.util.List.nil();
        for (File f : files) fileObjects = fileObjects.prepend(fileManager.getRegularFile(f));

        try { compiler.compile(fileObjects); }
        catch(Throwable t) {  // compiler threw an exception/error (typically out of memory error)
            errors.addFirst(new DJError("Compile exception: " + t, false));
            error.log(t);
        }

        debug.logEnd("compile()");
        return errors;
    }

    private Context _createContext(List<? extends File> classPath, List<? extends File> sourcePath, File destination,
                                   List<? extends File> bootClassPath, String sourceVersion, boolean showWarnings) {

        if (bootClassPath == null) { bootClassPath = _defaultBootClassPath; }

        Context context = new Context();
        Options options = Options.instance(context);
        for (Map.Entry<String, String> e : CompilerOptions.getOptions(showWarnings).entrySet()) {
            options.put(e.getKey(), e.getValue());
        }

        //Should be setable some day?
        options.put("-g", "");

        if (classPath != null) { options.put("-classpath", IOUtil.pathToString(classPath)); }
        if (sourcePath != null) { options.put("-sourcepath", IOUtil.pathToString(sourcePath)); }
        if (destination != null) { options.put("-d", destination.getPath()); }
        if (bootClassPath != null) { options.put("-bootclasspath", IOUtil.pathToString(bootClassPath)); }
        if (sourceVersion != null) { options.put("-source", sourceVersion); }
        if (!showWarnings) { options.put("-nowarn", ""); }

        return context;
    }

    /** We need to embed a DiagnosticListener in our own Context.  This listener will build a CompilerError list. */
    private static class CompilerErrorListener implements DiagnosticListener<JavaFileObject> {

        private List<? super DJError> _errors;

        public CompilerErrorListener(Context context, List<? super DJError> errors) {
            _errors = errors;
            context.put(DiagnosticListener.class, this);
        }

        public void report(Diagnostic<? extends JavaFileObject> d) {

            Diagnostic.Kind dt = d.getKind();
            boolean isWarning = false;  // init required by javac

            switch (dt) {
                case OTHER:             return;
                case NOTE:              return;
                case MANDATORY_WARNING: isWarning = true; break;
                case WARNING:           isWarning = true; break;
                case ERROR:             isWarning = false; break;
            }

            /* The new Java 6.0 Diagnostic interface appears to be broken.  The expression d.getSource().getName() returns a
             * non-existent path--the name of the test file (allocated as a TEMP file) appended to the source root for
             * DrJava--in GlobalModelCompileErrorsTest.testCompileFailsCorrectLineNumbers().  The expression
             * d.getSource().toUri().getPath() returns the correct result as does ((JCDiagnostic) d).getSourceName(). */

            if (d.getSource()!=null) {
                _errors.add(new DJError(new File(d.getSource().toUri().getPath()), // d.getSource().getName() fails! 
                        ((int) d.getLineNumber()) - 1,  // javac starts counting at 1
                        ((int) d.getColumnNumber()) - 1,
                        d.getMessage(null),    // null is the locale
                        isWarning));
            }
            else {
                _errors.add(new DJError(d.getMessage(null), isWarning));
            }
        }
    }

}