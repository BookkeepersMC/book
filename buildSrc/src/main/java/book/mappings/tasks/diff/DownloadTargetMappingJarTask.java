package book.mappings.tasks.diff;

import java.io.File;
import java.io.IOException;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import book.mappings.Constants;
import book.mappings.tasks.DefaultMappingsTask;

public abstract class DownloadTargetMappingJarTask extends DefaultMappingsTask {
    public static final String TASK_NAME = "downloadTargetMappingsJar";

    @OutputFile
    public abstract RegularFileProperty getTargetMappingsFile();

    @OutputFile
    public abstract RegularFileProperty getTargetUnpickFile();

    @OutputFile
    public abstract RegularFileProperty getTargetUnpickDefinitionsFile();

    @Internal
    public abstract RegularFileProperty getTargetUnpickRemappedDefinitionsFile();

    @OutputFile
    public abstract RegularFileProperty getTargetUnpickConstantsFile();

    public static final String TARGET_MAPPINGS = ".gradle/targets";

    public DownloadTargetMappingJarTask() {
        super("diff");

        final CheckTargetVersionExistsTask checkExists =
                this.getTaskNamed(CheckTargetVersionExistsTask.TASK_NAME, CheckTargetVersionExistsTask.class);
        this.onlyIf(task -> checkExists.getTargetVersion().isPresent());

        this.dependsOn(CheckTargetVersionExistsTask.TASK_NAME);

        final Provider<String> targetOrMappingsVersion = this.getProject().provider(() ->
                checkExists.getTargetVersion().orElse(Constants.MAPPINGS_VERSION)
        );

        this.getTargetMappingsFile().convention(targetOrMappingsVersion.map(version ->
                this.regularProjectFileOf(
                        TARGET_MAPPINGS + "/book-mappings-" + version + "/mappings/mappings.tiny"
                ))
        );
        this.getTargetUnpickFile().convention(targetOrMappingsVersion.map(version ->
                this.regularProjectFileOf(
                        TARGET_MAPPINGS + "/book-mappings-" + version + "/extras/unpick.json"
                )
        ));
        this.getTargetUnpickDefinitionsFile().convention(targetOrMappingsVersion.map(version ->
                this.regularProjectFileOf(
                        TARGET_MAPPINGS + "/book-mappings-" + version + "/extras/definitions.unpick"
                )
        ));
        this.getTargetUnpickRemappedDefinitionsFile().convention(targetOrMappingsVersion.map(version ->
                this.regularProjectFileOf(
                        TARGET_MAPPINGS + "/book-mappings-" + version + "remapped-unpick.unpick"
                )
        ));
        this.getTargetUnpickConstantsFile().convention(targetOrMappingsVersion.map(version ->
                this.regularProjectFileOf(
                        TARGET_MAPPINGS + "/book-mappings-" + version + "-constants.jar"
                )
        ));
    }

    @TaskAction
    public void downloadTargetMappings() throws IOException {
        // TODO eliminate project access in task action
        final String targetVersion =
                this.getTaskNamed(CheckTargetVersionExistsTask.TASK_NAME, CheckTargetVersionExistsTask.class)
                        .getTargetVersion().orElseThrow();

        // TODO eliminate project access in task action
        final File targetMappingsJar =
                this.getProject().file(TARGET_MAPPINGS + "/book-mappings-" + targetVersion + "-v2.jar");
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
                .dest(this.getTargetUnpickConstantsFile().getAsFile().get())
                .download();

        this.getProject()
                .zipTree(targetMappingsJar)
                .getAsFileTree()
                .visit(fileVisitDetails ->
                        fileVisitDetails.copyTo(this.getProject().file(
                                TARGET_MAPPINGS + "/book-mappings-" + targetVersion + "/" +
                                        fileVisitDetails.getRelativePath()
                        ))
                );
    }
}
