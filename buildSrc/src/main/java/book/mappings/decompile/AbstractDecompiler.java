package book.mappings.decompile;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import org.gradle.api.Project;

import book.mappings.decompile.javadoc.ClassJavadocProvider;
import book.mappings.decompile.javadoc.FieldJavadocProvider;
import book.mappings.decompile.javadoc.MethodJavadocProvider;

public abstract class AbstractDecompiler {
    private final Project project;

    public AbstractDecompiler(Project project) {
        this.project = project;
    }

    public void decompile(Collection<Path> sources, Path outputDir, Map<String, Object> options, Collection<File> libraries) {
        this.decompile(sources.stream().map(Path::toFile).toList(), outputDir.toFile(), options, libraries);
    }

    public abstract void decompile(Collection<File> sources, File outputDir, Map<String, Object> options, Collection<File> libraries);

    protected Project getProject() {
        return this.project;
    }

    public void withClassJavadocProvider(ClassJavadocProvider javadocProvider) {
    }

    public void withFieldJavadocProvider(FieldJavadocProvider javadocProvider) {
    }

    public void withMethodJavadocProvider(MethodJavadocProvider javadocProvider) {
    }
}
