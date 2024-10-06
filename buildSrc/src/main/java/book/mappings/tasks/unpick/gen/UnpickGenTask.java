package book.mappings.tasks.unpick.gen;

import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.OutputFiles;

public interface UnpickGenTask extends Task {
    @OutputFiles
    FileCollection getGeneratedUnpickDefinitions();
}
