package book.mappings.tasks;

import org.gradle.api.Task;
import book.mappings.BookMappingsExtension;
import book.mappings.util.DownloadImmediate;

public interface MappingsTask extends Task {
    default DownloadImmediate.Builder startDownload() {
        return new DownloadImmediate.Builder(this);
    }

    default <T extends Task> T getTaskNamed(String name, Class<T> taskClass) {
        return this.getProject().getTasks().named(name, taskClass).get();
    }

    default void outputsNeverUpToDate() {
        this.getOutputs().upToDateWhen(task -> false);
    }

    default BookMappingsExtension mappingsExt() {
        return BookMappingsExtension.get(this.getProject());
    }
}
