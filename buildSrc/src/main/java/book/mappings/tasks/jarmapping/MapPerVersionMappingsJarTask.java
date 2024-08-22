package book.mappings.tasks.jarmapping;

import book.mappings.Constants;
import book.mappings.tasks.setup.DownloadMinecraftLibrariesTask;
import book.mappings.tasks.setup.DownloadPerVersionMappingsTask;
import book.mappings.tasks.setup.MergeJarsTask;

public class MapPerVersionMappingsJarTask extends MapJarTask {
    public static final String TASK_NAME = "mapPerVersionMappingsJar";

    public MapPerVersionMappingsJarTask() {
        super(Constants.Groups.MAP_JAR_GROUP, "official", Constants.PER_VERSION_MAPPINGS_NAME);
        this.dependsOn(DownloadMinecraftLibrariesTask.TASK_NAME, DownloadPerVersionMappingsTask.TASK_NAME, MergeJarsTask.TASK_NAME);

        inputJar.convention(getTaskByType(MergeJarsTask.class)::getMergedFile);
        mappingsFile.convention(getTaskByType(DownloadPerVersionMappingsTask.class)::getTinyFile);
        outputJar.convention(() -> this.fileConstants.perVersionMappingsJar);

        getInputs().files(fileConstants.libraries);
    }
}
