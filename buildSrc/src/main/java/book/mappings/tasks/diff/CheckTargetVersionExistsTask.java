package book.mappings.tasks.diff;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.stream.StreamSupport;

import org.gradle.api.GradleException;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import book.mappings.Constants;
import book.mappings.tasks.DefaultMappingsTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public abstract class CheckTargetVersionExistsTask extends DefaultMappingsTask {
    public static final String TASK_NAME = "checkTargetVersionExists";

    @Internal
    protected abstract Property<String> getTargetVersionImpl();

    @OutputFile
    public abstract RegularFileProperty getMetaFile();

    public CheckTargetVersionExistsTask() {
        super("diff");
        this.getTargetVersionImpl().convention(this.getProject().provider(() -> {
            throw new GradleException(
                    "targetVersion has not been populated. " +
                            "Its should only be accessed from others tasks' inputs or predicates and " +
                            "only after checking if it's present."
            );
        }));
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
            this.getTargetVersionImpl().set(StreamSupport.stream(parsed.getAsJsonArray().spliterator(), false)
                    .max(Comparator.comparing(
                            element -> element.getAsJsonObject().get("build").getAsInt(),
                            Integer::compare
                    ))
                    .map(element -> element.getAsJsonObject().get("version").getAsString())
                    .orElse(null)
            );
        } catch (IOException e) {
            this.getLogger().error("Failed to read target version", e);
        }
    }

    @Internal
    public Provider<String> getTargetVersion() {
        return this.getTargetVersionImpl();
    }
}
