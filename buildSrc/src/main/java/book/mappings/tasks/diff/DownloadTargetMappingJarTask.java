package book.mappings.tasks.diff;

import java.io.File;
import java.io.IOException;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import book.mappings.tasks.DefaultMappingsTask;

public abstract class DownloadTargetMappingJarTask extends DefaultMappingsTask implements TargetVersionConsumingTask {
    public static final String TASK_NAME = "downloadTargetMappingsJar";

    @OutputFile
    public abstract RegularFileProperty getTargetJar();

    @OutputFile
    public abstract RegularFileProperty getTargetUnpickConstantsFile();

    public DownloadTargetMappingJarTask() {
        super("diff");
    }

    @TaskAction
    public void downloadTargetMappings() throws IOException {
        // TODO eliminate project access in task action
        final String targetVersion = this.getTargetVersion().get();

        // TODO eliminate project access in task action
        final File targetMappingsJar = this.getTargetJar().get().getAsFile();
        this.startDownload()
                .src(
                        "https://bookkeepersmc.github.io/m2/com/bookkeepersmc/book-mappings/" + targetVersion +
                                "/book-mappings-" + targetVersion + "-v2.jar"
                )
                .dest(targetMappingsJar)
                .download();

        this.startDownload()
                .src(
                        "https://bookkeepersmc.github.io/m2/com/bookkeepersmc/book-mappings/" + targetVersion +
                                "/book-mappings-" + targetVersion + "-constants.jar"
                )
                .dest(this.getTargetUnpickConstantsFile().get().getAsFile())
                .download();
    }
}
