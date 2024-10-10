package book.mappings;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Provider;
import org.quiltmc.enigma.api.EnigmaProfile;

import java.io.File;
import java.io.IOException;

public abstract class BookMappingsExtension {
    public static final String EXTENSION_NAME = "bookMappings";

    public abstract DirectoryProperty getMappingsDir();

    public final Provider<EnigmaProfile> enigmaProfile;

    /**
     * Don't parse this to create an {@link EnigmaProfile}, use {@link #enigmaProfile} instead.
     * <p>
     * This is exposed so it can be passed to external processes.
     */
    public abstract RegularFileProperty getEnigmaProfileConfig();

    public abstract RegularFileProperty getUnpickMeta();

    private final FileConstants fileConstants;

    public static BookMappingsExtension get(Project project) {
        return project.getExtensions().getByType(BookMappingsExtension.class);
    }

    public BookMappingsExtension(Project project) {
        this.fileConstants = new FileConstants(project);

        this.enigmaProfile = this.getEnigmaProfileConfig()
                .map(RegularFile::getAsFile)
                .map(File::toPath)
                .map(profilePath -> {
                    try {
                        return EnigmaProfile.read(profilePath);
                    } catch (IOException e) {
                        throw new GradleException("Failed to read enigma profile", e);
                    }
                });
    }

    public FileConstants getFileConstants() {
        return this.fileConstants;
    }
}
