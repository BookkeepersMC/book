package book.mappings.tasks.build;

import java.io.File;

import book.mappings.BookMappingsPlugin;
import book.mappings.tasks.setup.ExtractTinyMappingsTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.VisibleForTesting;
import book.mappings.Constants;
import book.mappings.tasks.DefaultMappingsTask;

import net.fabricmc.stitch.commands.tinyv2.CommandReorderTinyV2;

public abstract class InvertPerVersionMappingsTask extends DefaultMappingsTask {
    public static final String TASK_NAME = "invertPerVersionMappings";

    @InputFile
    protected abstract RegularFileProperty getInput();

    @OutputFile
    public abstract RegularFileProperty getInvertedTinyFile();

    public InvertPerVersionMappingsTask() {
        super(Constants.Groups.BUILD_MAPPINGS_GROUP);
        this.dependsOn(BookMappingsPlugin.DOWNLOAD_PER_VERSION_MAPPINGS_TASK_NAME);

        this.getInvertedTinyFile().convention(() -> new File(
                this.fileConstants.cacheFilesMinecraft,
                "%s-%s-inverted.tiny".formatted(Constants.MINECRAFT_VERSION, Constants.PER_VERSION_MAPPINGS_NAME)
        ));

        this.getInput().convention(
                this.getTaskNamed(BookMappingsPlugin.EXTRACT_TINY_PER_VERSION_MAPPINGS_TASK_NAME, ExtractTinyMappingsTask.class)
                        .getTinyFile()
        );
    }

    @TaskAction
    public void invertPerVersionMappings() throws Exception {
        this.getLogger().lifecycle(":building inverted {}", Constants.PER_VERSION_MAPPINGS_NAME);

        invertMappings(this.getInput().get().getAsFile(), this.getInvertedTinyFile().get().getAsFile());
    }

    @VisibleForTesting
    public static void invertMappings(File input, File output) throws Exception {
        final String[] args = {
                input.getAbsolutePath(),
                output.getAbsolutePath(),
                Constants.PER_VERSION_MAPPINGS_NAME, "official"
        };

        new CommandReorderTinyV2().run(args);
    }
}
