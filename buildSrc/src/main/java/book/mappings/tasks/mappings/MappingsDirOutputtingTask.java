package book.mappings.tasks.mappings;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.Internal;

import book.mappings.tasks.MappingsTask;

public interface MappingsDirOutputtingTask extends MappingsTask {
    @Internal(
            """
            This is only used to resolve relative output paths against.
            A task should not add the whole directory to its output unless the task is
            untracked and its output is not intended for consumption by other tasks.
            """
    )
    DirectoryProperty getMappingsDir();
}
