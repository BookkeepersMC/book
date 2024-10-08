package book.mappings.tasks.build;

import book.mappings.Constants;

import java.io.File;

public abstract class AbstractHashedMergeTask extends AbstractTinyMergeTask {
    public AbstractHashedMergeTask(String outputMappings) {
        super(outputMappings, Constants.PER_VERSION_MAPPINGS_NAME);
    }

    @Override
    public void mergeMappings() throws Exception {
        File hashedTinyInput = this.getTaskByType(InvertPerVersionMappingsTask.class).getInvertedTinyFile();
        mergeMappings(hashedTinyInput);
    }
}
