package com.example;

import java.io.File;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.Callable;
import javax.annotation.processing.Processor;

public interface JavaCompiler extends Tool, OptionChecker {
    CompilationTask getTask(Writer out,
                            JavaFileManager fileManager,
                            DiagnosticListener<? super JavaFileObject> diagnosticListener,
                            Iterable<String> options,
                            Iterable<String> classes,
                            Iterable<? extends JavaFileObject> compilationUnits);
    StandardJavaFileManager getStandardFileManager(
            DiagnosticListener<? super JavaFileObject> diagnosticListener,
            Locale locale,
            Charset charset);
    interface CompilationTask extends Callable<Boolean> {
        void setProcessors(Iterable<? extends Processor> processors);
        void setLocale(Locale locale);
        Boolean call();
    }
}