package book.mappings.tasks.diff;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.StreamSupport;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import book.mappings.Constants;
import book.mappings.tasks.DefaultMappingsTask;

public abstract class CheckTargetVersionExistsTask extends DefaultMappingsTask {
    public static final String TASK_NAME = "checkTargetVersionExists";

    @Internal
    private Optional<String> targetVersion = Optional.empty();

    @OutputFile
    public abstract RegularFileProperty getMetaFile();

    public CheckTargetVersionExistsTask() {
        super("diff");
        this.outputsNeverUpToDate();
        this.getMetaFile().convention(() -> new File(this.fileConstants.cacheFilesMinecraft, "book-mappings-" + Constants.MINECRAFT_VERSION + ".json"));
    }

    @TaskAction
    public void checkExists() {
        try {
            final File metaFile = this.getMetaFile().get().getAsFile();
            this.startDownload()
                    .src("https://bookkeepersmc.github.io/meta/v2/versions/mappings" + Constants.MINECRAFT_VERSION)
                    .dest(metaFile)
                    .overwrite(true)
                    .download();

            final JsonElement parsed = JsonParser.parseReader(new FileReader(metaFile));
            this.targetVersion = StreamSupport.stream(parsed.getAsJsonArray().spliterator(), false)
                    .max(Comparator.comparing(
                            element -> element.getAsJsonObject().get("build").getAsInt(),
                            Integer::compare
                    ))
                    .map(element -> element.getAsJsonObject().get("version").getAsString());
        } catch (IOException e) {
            this.targetVersion = Optional.empty();
        }
    }

    public Optional<String> getTargetVersion() {
        return this.targetVersion;
    }
}
