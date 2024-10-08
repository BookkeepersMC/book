package book.mappings;

import java.io.File;

import org.gradle.api.Project;
import book.mappings.tasks.lint.DownloadDictionaryFileTask;

public class FileConstants {
    public final File buildDir;
    public final File cacheFilesMinecraft;
    public final File tempDir;
    public final File perVersionMappingsJar;
    public final File unpickedJar;
    public final File namedJar;
    public final File libraries;

    public final File dictionaryDir;
    public final File dictionaryFile;

    public final File unpickDefinitions;
    public final File unpickMeta;
    public final File unpickGlDefinitions;
    public final File unpickGlStateManagerDefinitions;

    public FileConstants(Project project) {
        buildDir = project.file("build/mappings");
        cacheFilesMinecraft = project.file(".gradle/minecraft");
        tempDir = project.file(".gradle/temp");
        libraries = new File(cacheFilesMinecraft, "libraries");

        perVersionMappingsJar = project.file(Constants.MINECRAFT_VERSION + "-" + Constants.PER_VERSION_MAPPINGS_NAME + ".jar");
        unpickedJar = project.file(Constants.MINECRAFT_VERSION + "-" + Constants.PER_VERSION_MAPPINGS_NAME + "-unpicked.jar");
        namedJar = project.file(Constants.MINECRAFT_VERSION + "-named.jar");

        dictionaryDir = project.file(".gradle/lint/dictionary");
        dictionaryFile = project.file(dictionaryDir.toPath().resolve(DownloadDictionaryFileTask.REVISION + ".txt"));

        unpickDefinitions = project.file("unpick-definitions");
        unpickMeta = new File(unpickDefinitions, "unpick.json");
        unpickGlDefinitions = new File(unpickDefinitions, "unpick_gl.unpick");
        unpickGlStateManagerDefinitions = new File(unpickDefinitions, "unpick_glstatemanager.unpick");
    }
}
