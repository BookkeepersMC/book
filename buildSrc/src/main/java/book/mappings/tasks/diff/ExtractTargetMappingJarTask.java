package book.mappings.tasks.diff;

import book.mappings.tasks.DefaultMappingsTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

public abstract class ExtractTargetMappingJarTask extends DefaultMappingsTask implements TargetVersionConsumingTask {
    public static final String TASK_NAME = "extractTargetMappingsJar";

    @InputFile
    @Optional
    public abstract RegularFileProperty getTargetJar();

    @OutputDirectory
    public abstract DirectoryProperty getExtractionDest();

    private final FileTree targetJarZipTree;

    public ExtractTargetMappingJarTask() {
        super("diff");

        this.targetJarZipTree = this.getProject().zipTree(this.getTargetJar());
    }

    @TaskAction
    public void extractTargetMappings() {
        this.targetJarZipTree.getAsFileTree().visit(fileVisitDetails -> fileVisitDetails.copyTo(this.getExtractionDest().file(fileVisitDetails.getRelativePath().getPathString()).get().getAsFile()));
    }
}
