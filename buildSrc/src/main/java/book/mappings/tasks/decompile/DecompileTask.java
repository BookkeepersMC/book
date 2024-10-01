package book.mappings.tasks.decompile;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import book.mappings.Constants;
import book.mappings.decompile.AbstractDecompiler;
import book.mappings.decompile.Decompilers;
import book.mappings.decompile.javadoc.ClassJavadocProvider;
import book.mappings.decompile.javadoc.FieldJavadocProvider;
import book.mappings.decompile.javadoc.MethodJavadocProvider;
import book.mappings.tasks.DefaultMappingsTask;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class DecompileTask extends DefaultMappingsTask {
    @Input
    public abstract Property<Decompilers> getDecompiler();

    @Optional
    @Input
    public abstract MapProperty<String, Object> getDecompilerOptions();

    @Optional
    @Input
    public abstract Property<FileCollection> getLibraries();

    @InputFiles
    public abstract RegularFileProperty getInput();

    @OutputDirectory
    public abstract RegularFileProperty getOutput();


    private ClassJavadocProvider classJavadocProvider;
    private FieldJavadocProvider fieldJavadocProvider;
    private MethodJavadocProvider methodJavadocProvider;

    public DecompileTask() {
        super(Constants.Groups.DECOMPILE_GROUP);
    }

    @TaskAction
    public void decompile() {
        final Map<String, Object> options = this.getDecompilerOptions().getOrElse(new HashMap<>());

        final Collection<File> libraries = this.getLibraries().<Collection<File>>map(FileCollection::getFiles)
                .getOrElse(Collections.emptyList());

        final AbstractDecompiler decompiler = this.getAbstractDecompiler();

        if (this.classJavadocProvider != null) {
            decompiler.withClassJavadocProvider(this.classJavadocProvider);
        }
        if (this.fieldJavadocProvider != null) {
            decompiler.withFieldJavadocProvider(this.fieldJavadocProvider);
        }
        if (this.methodJavadocProvider != null) {
            decompiler.withMethodJavadocProvider(this.methodJavadocProvider);
        }

        decompiler.decompile(this.getInput().getAsFile().get(), this.getOutput().getAsFile().get(), options, libraries);
    }

    @Internal
    public AbstractDecompiler getAbstractDecompiler() {
        return this.getDecompiler().get().getProvider().provide(this.getProject());
    }

    public void classJavadocProvider(ClassJavadocProvider provider) {
        this.classJavadocProvider = provider;
    }

    public void fieldJavadocProvider(FieldJavadocProvider provider) {
        this.fieldJavadocProvider = provider;
    }

    public void methodJavadocProvider(MethodJavadocProvider provider) {
        this.methodJavadocProvider = provider;
    }
}
