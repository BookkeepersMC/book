package book.mappings.tasks;

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.quiltmc.enigma.api.EnigmaProfile;
import org.quiltmc.enigma.api.service.JarIndexerService;

import java.util.Collection;
import java.util.stream.Stream;

import static org.quiltmc.enigma_plugin.Arguments.SIMPLE_TYPE_FIELD_NAMES_PATH;

public interface EnigmaProfileConsumingTask extends MappingsTask {
    @Internal(
            "An EnigmaProfile cannot be fingerprinted. " +
                    "Up-to-date-ness is ensured by getSimpleTypeFieldNamesFiles and its source, " +
                    "MappingsExtension::getEnigmaProfileFile."
    )
    Property<EnigmaProfile> getEnigmaProfile();

    /**
     * Don't parse this to create an {@link EnigmaProfile}, use {@link #getEnigmaProfile() enigmaProfile} instead.
     * <p>
     * This is exposed so it can be passed to external processes.
     */
    @InputFile
    RegularFileProperty getEnigmaProfileConfig();


    /**
     * Holds any {@code simple_type_field_names} configuration files obtained from the
     * {@link #getEnigmaProfile() EnigmaProfile}.
     * <p>
     * {@link EnigmaProfileConsumingTask}s may not access these files directly, but they affect enigma's behavior,
     * so they must be considered for up-to-date checks.
     */
    @InputFiles
    Property<FileCollection> getSimpleTypeFieldNamesFiles();
}
