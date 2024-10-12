package book.mappings.tasks.build;

import java.util.Map;

import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.InputFile;
import org.gradle.jvm.tasks.Jar;
import book.mappings.Constants;
import book.mappings.tasks.MappingsTask;

import javax.inject.Inject;

public abstract class MappingsV2JarTask extends Jar implements MappingsTask {
    public static final String JAR_UNPICK_META_PATH = "extras/unpick.json";
    public static final String JAR_UNPICK_DEFINITION_PATH = "extras/definitions.unpick";
    public static final String JAR_MAPPINGS_PATH = "mappings/mappings.tiny";

    @InputFile
    public abstract RegularFileProperty getUnpickMeta();

    @InputFile
    public abstract RegularFileProperty getUnpickDefinition();

    @InputFile
    public abstract RegularFileProperty getMappings();

    // unpick version can't be a property because it's used when the task is instantiated
    public final String unpickVersion;

    @Inject
    public MappingsV2JarTask(String unpickVersion) {
        this.setGroup(Constants.Groups.BUILD_MAPPINGS_GROUP);
        this.outputsNeverUpToDate();

        this.unpickVersion = unpickVersion;

        final Provider<RegularFile> unpickMeta = this.getUnpickMeta();

        this.from(unpickMeta, copySpec -> {
            copySpec.expand(Map.of("version", this.unpickVersion));
            copySpec.rename(unused -> JAR_UNPICK_META_PATH);
        });

        this.from(this.getUnpickDefinition(), copySpec -> copySpec.rename(unused -> JAR_UNPICK_DEFINITION_PATH));

        this.from(this.getMappings(), copySpec -> copySpec.rename(unused -> JAR_MAPPINGS_PATH));
    }
}
