package book.mappings.tasks.diff;

import java.io.IOException;

import book.mappings.tasks.decompile.DecompileTask;

import org.apache.commons.io.FileUtils;

public abstract class DecompileTargetTask extends DecompileTask implements UnpickVersionsMatchConsumingTask {
    // @Input
    // public abstract Property<String> getJavadocNamespace();
    //
    // @InputFile
    // public abstract RegularFileProperty getJavadocSource();

    @Override
    public void decompile() throws IOException {
        FileUtils.deleteDirectory(this.getOutput().get().getAsFile());

        // try {
        //     final MappingsJavadocProvider javadocProvider = new MappingsJavadocProvider(
        //         this.getJavadocSource().get().getAsFile(), this.getJavadocNamespace().get()
        //     );
        //
        //     this.classJavadocProvider(javadocProvider);
        //     this.fieldJavadocProvider(javadocProvider);
        //     this.methodJavadocProvider(javadocProvider);
        // } catch (IOException e) {
        //     throw new GradleException("Failed to create javadoc provider", e);
        // }

        super.decompile();
    }
}
