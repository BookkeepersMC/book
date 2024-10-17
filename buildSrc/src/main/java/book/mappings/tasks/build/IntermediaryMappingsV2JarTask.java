package book.mappings.tasks.build;

import static book.mappings.util.ProviderUtil.exists;

import javax.inject.Inject;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;

public abstract class IntermediaryMappingsV2JarTask extends MappingsV2JarTask {
    @Override
    @Optional
    @InputFile
    public abstract RegularFileProperty getMappings();

    @Inject
    public IntermediaryMappingsV2JarTask(String unpickVersion) {
        super(unpickVersion);

        this.onlyIf(unused -> exists(this.getMappings()));
    }
}
