package book.mappings.tasks.diff;

import java.util.Map;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;

import book.mappings.Constants;
import book.mappings.tasks.jarmapping.MapJarTask;

public abstract class RemapTargetMinecraftJarTask extends MapJarTask implements UnpickVersionsMatchConsumingTask {
    public static final String TASK_NAME = "remapTargetMinecraftJar";

    @Internal("temporary")
    @Override
    public abstract RegularFileProperty getInputJar();

    @Input
    @Optional
    public abstract Property<Boolean> getUnpickVersionsMatch();

    public RemapTargetMinecraftJarTask() {
        super("diff", Constants.PER_VERSION_MAPPINGS_NAME, "named");
    }

    public Map<String, String> getAdditionalMappings() {
        return MapJarTask.JAVAX_TO_JETBRAINS;
    }
}
