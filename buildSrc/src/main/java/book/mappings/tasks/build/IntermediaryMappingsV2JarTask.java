package book.mappings.tasks.build;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;

import static book.mappings.util.ProviderUtil.exists;

public abstract class IntermediaryMappingsV2JarTask extends MappingsV2JarTask {
    @Override
    @Optional
    @InputFile
    public abstract RegularFileProperty getMappings();

    public IntermediaryMappingsV2JarTask(String unpickVersion) {
        super(unpickVersion);

        this.onlyIf(unused -> exists(this.getMappings()));
    }
}
