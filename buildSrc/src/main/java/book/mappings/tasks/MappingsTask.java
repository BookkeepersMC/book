package book.mappings.tasks;

import org.gradle.api.Task;
import org.gradle.api.artifacts.VersionCatalog;
import org.gradle.api.artifacts.VersionCatalogsExtension;
import book.mappings.BookMappingsExtension;
import book.mappings.util.DownloadImmediate;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;

public interface MappingsTask extends Task {
    default DownloadImmediate.Builder startDownload() {
        return new DownloadImmediate.Builder(this);
    }

    default Task getTaskNamed(String taskName) {
        return this.getProject().getTasks().getByName(taskName);
    }

    default <T extends Task> T getTaskNamed(String name, Class<T> taskClass) {
        return this.getProject().getTasks().named(name, taskClass).get();
    }

    default RegularFile regularProjectFileOf(String path) {
        return this.getProjectDirectory().file(path);
    }

    default Provider<RegularFile> regularProjectFileOf(Provider<? extends CharSequence> path) {
        return this.getProjectDirectory().file(path);
    }

    private Directory getProjectDirectory() {
        return this.getProject().getLayout().getProjectDirectory();
    }

    default void outputsNeverUpToDate() {
        this.getOutputs().upToDateWhen(task -> false);
    }

    default BookMappingsExtension mappingsExt() {
        return BookMappingsExtension.get(this.getProject());
    }

    default VersionCatalogsExtension versionCatalogs() {
        return this.getProject().getExtensions().getByType(VersionCatalogsExtension.class);
    }

    default VersionCatalog libs() {
        return this.versionCatalogs().named("libs");
    }
}
