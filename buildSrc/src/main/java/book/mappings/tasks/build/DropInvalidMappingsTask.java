package book.mappings.tasks.build;

import org.quiltmc.enigma.command.DropInvalidMappingsCommand;
import org.gradle.api.tasks.TaskAction;
import book.mappings.Constants;
import book.mappings.tasks.DefaultMappingsTask;
import book.mappings.tasks.jarmapping.MapPerVersionMappingsJarTask;

import java.io.File;

public class DropInvalidMappingsTask extends DefaultMappingsTask {
    public static final String TASK_NAME = "dropInvalidMappings";
    private final File mappings = getProject().file("mappings");

    public DropInvalidMappingsTask() {
        super(Constants.Groups.BUILD_MAPPINGS_GROUP);
        getInputs().dir(mappings);
        this.dependsOn(MapPerVersionMappingsJarTask.TASK_NAME);
    }

    @TaskAction
    public void dropInvalidMappings() {
        getLogger().info(":dropping invalid mappings");

        String[] args = new String[]{
                fileConstants.perVersionMappingsJar.getAbsolutePath(),
                mappings.getAbsolutePath()
        };

        try {
            new DropInvalidMappingsCommand().run(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
