package book.mappings.tasks.jarmapping;

import java.util.Map;

import book.mappings.Constants;
import book.mappings.MappingsPlugin;
import book.mappings.tasks.build.AddProposedMappingsTask;
import book.mappings.tasks.setup.DownloadMinecraftLibrariesTask;

import static book.mappings.MappingsPlugin.INSERT_AUTO_GENERATED_MAPPINGS_TASK_NAME;

public abstract class MapNamedJarTask extends MapJarTask {
    public static final String TASK_NAME = "mapNamedJar";

    public MapNamedJarTask() {
        super(Constants.Groups.MAP_JAR_GROUP, Constants.PER_VERSION_MAPPINGS_NAME, "named");

        this.getInputs()
                .files(this.getTaskNamed(DownloadMinecraftLibrariesTask.TASK_NAME).getOutputs().getFiles().getFiles());

        this.getInputJar().convention(() -> this.fileConstants.unpickedJar);
        this.getMappingsFile().convention(
                this.getTaskNamed(INSERT_AUTO_GENERATED_MAPPINGS_TASK_NAME, AddProposedMappingsTask.class)
                        .getOutputMappings()
        );
        this.getOutputJar().convention(() -> this.fileConstants.namedJar);

        this.dependsOn(INSERT_AUTO_GENERATED_MAPPINGS_TASK_NAME, "unpickHashedJar");
    }

    public Map<String, String> getAdditionalMappings() {
        return JAVAX_TO_JETBRAINS;
    }
}

