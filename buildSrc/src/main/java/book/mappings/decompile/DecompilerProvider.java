package book.mappings.decompile;

import org.gradle.api.Project;

public interface DecompilerProvider {
    AbstractDecompiler provide(Project project);
}
