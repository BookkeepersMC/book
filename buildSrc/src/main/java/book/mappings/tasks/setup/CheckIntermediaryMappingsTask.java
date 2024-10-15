package book.mappings.tasks.setup;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import book.mappings.Constants;
import book.mappings.tasks.DefaultMappingsTask;

import java.io.File;
import java.util.Set;

public abstract class CheckIntermediaryMappingsTask extends DefaultMappingsTask {
    public static final String TASK_NAME = "checkIntermediaryMappings";

    @Internal
    private boolean present = false;

    public CheckIntermediaryMappingsTask() {
        super(Constants.Groups.SETUP);
    }

    public boolean isPresent() {
        return this.present;
    }

    @TaskAction
    public void checkIntermediaryMappings() {
        final Configuration configuration = this.getProject().getConfigurations().getByName("intermediary");
        final Set<File> files;

        try {
            files = configuration.resolve();
        } catch (Exception e) {
            return;
        }

        this.present = !files.isEmpty();
    }
}
