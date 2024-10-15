package book.mappings;

import java.io.File;

import org.gradle.api.Project;
import book.mappings.tasks.lint.DownloadDictionaryFileTask;

public class FileConstants {
    public final File tempDir;

    public FileConstants(Project project) {
        this.tempDir = project.file(".gradle/temp");
    }
}
