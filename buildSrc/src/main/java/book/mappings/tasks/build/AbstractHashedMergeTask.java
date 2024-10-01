package book.mappings.tasks.build;

import book.mappings.Constants;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;

import java.io.File;

public abstract class AbstractHashedMergeTask extends AbstractTinyMergeTask {
    @InputFile
    public abstract RegularFileProperty getHashedTinyMappings();

    public AbstractHashedMergeTask() {
        super(Constants.PER_VERSION_MAPPINGS_NAME);
    }

    @Override
    public void mergeMappings() throws Exception {
        final File hashedTinyInput =
                this.getTaskNamed(InvertPerVersionMappingsTask.TASK_NAME, InvertPerVersionMappingsTask.class)
                        .getInvertedTinyFile().get().getAsFile();

        this.mergeMappings(hashedTinyInput);
    }
}
