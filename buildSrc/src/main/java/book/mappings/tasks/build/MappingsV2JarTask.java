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

public class MappingsV2JarTask extends Jar implements MappingsTask {
    @InputFile
    private final RegularFileProperty mappings;

    public MappingsV2JarTask() {
        this.setGroup(Constants.Groups.BUILD_MAPPINGS_GROUP);
        this.outputsNeverUpToDate();
        getDestinationDirectory().set(getProject().file("build/libs"));

        File unpickMetaFile = mappingsExt().getFileConstants().unpickMeta;
        String version = libs().findVersion("unpick").map(VersionConstraint::getRequiredVersion).orElseThrow(() -> new RuntimeException("Could not find unpick version"));
        from(unpickMetaFile, copySpec -> {
            copySpec.expand(Map.of("version", version));
            copySpec.rename(unpickMetaFile.getName(), "extras/unpick.json");
        });

        RegularFileProperty combineUnpickDefinitions = getTaskByType(CombineUnpickDefinitionsTask.class).getOutput();
        from(combineUnpickDefinitions, copySpec -> {
            copySpec.rename(combineUnpickDefinitions.get().getAsFile().getName(), "extras/definitions.unpick");
        });

        mappings = getObjectFactory().fileProperty();

        from(mappings, copySpec -> {
            copySpec.rename((originalName) -> "mappings/mappings.tiny");
        });
    }

    public RegularFileProperty getMappings() {
        return mappings;
    }
}
