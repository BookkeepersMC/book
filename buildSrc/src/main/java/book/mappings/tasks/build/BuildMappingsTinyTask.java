package book.mappings.tasks.build;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import book.mappings.tasks.MappingsDirConsumingTask;
import book.mappings.util.ProviderUtil;
import org.gradle.api.tasks.InputFile;
import org.quiltmc.enigma.command.MapSpecializedMethodsCommand;
import org.quiltmc.enigma.api.translation.mapping.serde.MappingParseException;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.VisibleForTesting;
import book.mappings.Constants;
import book.mappings.tasks.DefaultMappingsTask;
import book.mappings.tasks.jarmapping.MapPerVersionMappingsJarTask;

public abstract class BuildMappingsTinyTask extends DefaultMappingsTask implements MappingsDirConsumingTask {
    public static final String TASK_NAME = "buildMappingsTiny";

    @InputFile
    public abstract RegularFileProperty getPerVersionMappingsJar();

    @OutputFile
    public abstract RegularFileProperty getOutputMappings();

    public BuildMappingsTinyTask() {
        super(Constants.Groups.BUILD_MAPPINGS);
    }

    @TaskAction
    public void execute() throws IOException, MappingParseException {
        this.getLogger().lifecycle(":generating tiny mappings");

        buildMappingsTiny(
                // this.fileConstants.perVersionMappingsJar.toPath(),
                ProviderUtil.getPath(this.getPerVersionMappingsJar()),
                ProviderUtil.getPath(this.getMappingsDir()),
                ProviderUtil.getPath(this.getOutputMappings())
        );
    }

    @VisibleForTesting
    public static void buildMappingsTiny(
            Path perVersionMappingsJar, Path mappings, Path outputMappings
    ) throws IOException, MappingParseException {
        MapSpecializedMethodsCommand.run(
                perVersionMappingsJar,
                mappings,
                outputMappings,
                Constants.PER_VERSION_MAPPINGS_NAME,
                "named"
        );
    }
}
