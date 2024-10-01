package book.mappings.tasks;

import org.gradle.api.DefaultTask;
import book.mappings.FileConstants;

public abstract class DefaultMappingsTask extends DefaultTask implements MappingsTask {
    protected final FileConstants fileConstants;

    public DefaultMappingsTask(String group) {
        this.fileConstants = this.mappingsExt().getFileConstants();
        this.setGroup(group);
    }
}
