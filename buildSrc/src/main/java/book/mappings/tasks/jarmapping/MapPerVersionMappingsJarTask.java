package book.mappings.tasks.jarmapping;

import book.mappings.Constants;
import book.mappings.MappingsPlugin;
import book.mappings.tasks.setup.DownloadMinecraftLibrariesTask;
import book.mappings.tasks.setup.ExtractTinyMappingsTask;
import book.mappings.tasks.setup.MergeJarsTask;

public abstract class MapPerVersionMappingsJarTask extends MapJarTask {
    public static final String TASK_NAME = "mapPerVersionMappingsJar";

    public MapPerVersionMappingsJarTask() {
        super(Constants.Groups.MAP_JAR_GROUP, "official", Constants.PER_VERSION_MAPPINGS_NAME);
        this.dependsOn(
                DownloadMinecraftLibrariesTask.TASK_NAME,
                MappingsPlugin.DOWNLOAD_PER_VERSION_MAPPINGS_TASK_NAME,
                MergeJarsTask.TASK_NAME
        );

        this.getInputJar().convention(this.getTaskNamed(MergeJarsTask.TASK_NAME, MergeJarsTask.class).getMergedFile());
        this.getMappingsFile().convention(
                this.getTaskNamed(MappingsPlugin.EXTRACT_TINY_PER_VERSION_MAPPINGS_TASK_NAME, ExtractTinyMappingsTask.class)
                        .getTinyFile()
        );
        this.getOutputJar().convention(() -> this.fileConstants.perVersionMappingsJar);

        this.getInputs().files(this.fileConstants.libraries);
    }
}
