package book.mappings.tasks.setup;

import java.io.File;
import java.io.IOException;

import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import book.mappings.Constants;
import book.mappings.tasks.DefaultMappingsTask;

public class DownloadVersionsManifestTask extends DefaultMappingsTask {
    public static final String TASK_NAME = "downloadVersionsManifest";
    @OutputFile
    private final File manifestFile;

    public DownloadVersionsManifestTask() {
        super(Constants.Groups.SETUP_GROUP);

        manifestFile = new File(fileConstants.cacheFilesMinecraft, "version_manifest_v2.json");
    }

    @TaskAction
    public void downloadVersionsManifestTask() throws IOException {
        getLogger().lifecycle(":downloading minecraft versions manifest");
        startDownload()
                .src("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json")
                .dest(manifestFile)
                .overwrite(true)
                .download();
    }

    public File getManifestFile() {
        return manifestFile;
    }
}
