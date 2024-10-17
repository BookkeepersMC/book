package book.mappings.tasks.jarmapping;

import java.util.Map;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.*;

import book.mappings.tasks.DefaultMappingsTask;
import book.mappings.util.JarRemapper;

public abstract class MapJarTask extends DefaultMappingsTask {
    public static final Map<String, String> JAVAX_TO_JETBRAINS = Map.of(
            "javax/annotation/Nullable", "org/jetbrains/annotations/Nullable",
            "javax/annotation/Nonnull", "org/jetbrains/annotations/NotNull",
            "javax/annotation/concurrent/Immutable", "org/jetbrains/annotations/Unmodifiable"
    );
    @InputFile
    public abstract RegularFileProperty getInputJar();

    @InputFile
    public abstract RegularFileProperty getMappingsFile();

    @InputDirectory
    public abstract DirectoryProperty getLibrariesDir();

    @OutputFile
    public abstract RegularFileProperty getOutputJar();

    private final String from, to;

    public MapJarTask(String group, String from, String to) {
        super(group);
        this.from = from;
        this.to = to;
    }

    @TaskAction
    public void remapJar() {
        this.getLogger().lifecycle(":mapping minecraft from " + this.from + " to " + this.to);
        final Map<String, String> additionalMappings = this.getAdditionalMappings();
        JarRemapper.mapJar(
                this.getOutputJar().get().getAsFile(),
                this.getInputJar().get().getAsFile(),
                this.getMappingsFile().get().getAsFile(),
                this.getLibrariesDir().get().getAsFile(),
                this.from, this.to,
                builder -> builder.withMappings(out -> additionalMappings.forEach(out::acceptClass))
        );
    }

    @Internal
    public Map<String, String> getAdditionalMappings() {
        return Map.of();
    }
}
