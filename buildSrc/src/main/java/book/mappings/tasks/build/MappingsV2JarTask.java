package book.mappings.tasks.build;

import java.io.File;
import java.util.Map;

import org.gradle.api.artifacts.VersionConstraint;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.jvm.tasks.Jar;
import book.mappings.Constants;
import book.mappings.tasks.MappingsTask;
import book.mappings.tasks.unpick.CombineUnpickDefinitionsTask;

public abstract class MappingsV2JarTask extends Jar implements MappingsTask {
    @InputFile
    public abstract RegularFileProperty getMappings();

    public MappingsV2JarTask() {
        this.setGroup(Constants.Groups.BUILD_MAPPINGS_GROUP);
        this.outputsNeverUpToDate();
        this.getDestinationDirectory().set(this.getProject().file("build/libs"));

        final File unpickMetaFile = this.mappingsExt().getFileConstants().unpickMeta;
        final String version = this.libs().findVersion("unpick").map(VersionConstraint::getRequiredVersion)
                .orElseThrow(() -> new RuntimeException("Could not find unpick version"));

        this.from(unpickMetaFile, copySpec -> {
            copySpec.expand(Map.of("version", version));
            copySpec.rename(unpickMetaFile.getName(), "extras/unpick.json");
        });

        final RegularFileProperty combineUnpickDefinitions =
                this.getTaskNamed(CombineUnpickDefinitionsTask.TASK_NAME, CombineUnpickDefinitionsTask.class).getOutput();
        this.from(combineUnpickDefinitions, copySpec ->
                copySpec.rename(
                        combineUnpickDefinitions.get().getAsFile().getName(),
                        "extras/definitions.unpick"
                )
        );

        this.from(this.getMappings(), copySpec ->
                copySpec.rename((originalName) -> "mappings/mappings.tiny")
        );
    }
}
