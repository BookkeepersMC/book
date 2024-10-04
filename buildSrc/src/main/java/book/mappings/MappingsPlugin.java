package book.mappings;

import book.mappings.decompile.Decompilers;
import book.mappings.tasks.EnigmaProfileConsumingTask;
import book.mappings.tasks.build.*;
import book.mappings.tasks.diff.*;
import book.mappings.tasks.jarmapping.MapJarTask;
import book.mappings.tasks.setup.*;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskContainer;
import book.mappings.tasks.jarmapping.MapNamedJarTask;
import book.mappings.tasks.jarmapping.MapPerVersionMappingsJarTask;
import book.mappings.tasks.lint.DownloadDictionaryFileTask;
import book.mappings.tasks.lint.FindDuplicateMappingFilesTask;
import book.mappings.tasks.lint.MappingLintTask;
import book.mappings.tasks.unpick.CombineUnpickDefinitionsTask;
import book.mappings.tasks.unpick.RemapUnpickDefinitionsTask;
import book.mappings.tasks.unpick.gen.OpenGlConstantUnpickGeneratorTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

import static book.mappings.util.ProviderUtil.provideProjectDir;

public abstract class MappingsPlugin implements Plugin<Project> {
    public static final String INSERT_AUTO_GENERATED_MAPPINGS_TASK_NAME = "insertAutoGeneratedMappings";
    public static final String DOWNLOAD_PER_VERSION_MAPPINGS_TASK_NAME = "downloadPerVersionMappings";
    public static final String EXTRACT_TINY_PER_VERSION_MAPPINGS_TASK_NAME = "extractTinyPerVersionMappings";
    public static final String EXTRACT_TINY_INTERMEDIARY_MAPPINGS_TASK_NAME = "extractTinyIntermediaryMappings";

    // TODO probably move to FileConstants
    public static final String TARGET_MAPPINGS_DIR = ".gradle/targets";

    private static final String QUILT_MAPPINGS_PREFIX = "quilt-mappings-";
    public static final String DECOMPILE_TARGET_VINEFLOWER_TASK_NAME = "decompileTargetVineflower";

    public static Provider<RegularFile> provideMappingsDestFile(
            Provider<Directory> destDir, String mappingsName, String fileExt
    ) {
        return destDir.map(dir -> dir.file(Constants.MINECRAFT_VERSION + "-" + mappingsName + "." + fileExt));
    }

    @Override
    public void apply(@NotNull Project project) {
        final var ext = project.getExtensions().create("mappings", MappingsExtension.class, project);

        final TaskContainer tasks = project.getTasks();

        final Provider<Directory> mappingsDestDir =
                provideProjectDir(project, ext.getFileConstants().cacheFilesMinecraft);

        tasks.withType(EnigmaProfileConsumingTask.class).configureEach(task ->
                task.getEnigmaProfile().convention(ext.enigmaProfile)
        );

        // provide an informative error message if no profile is specified
        ext.getEnigmaProfileFile().convention(() -> {
            throw new GradleException(
                    "No enigma profile specified. " +
                            "A profile must be specified to use an EnigmaProfileConsumingTask."
            );
        });

        final var downloadVersionsManifest = tasks.register(
                DownloadVersionsManifestTask.TASK_NAME, DownloadVersionsManifestTask.class,
                task -> {
                    task.getManifestFile().convention(() -> new File(
                            ext.getFileConstants().cacheFilesMinecraft,
                            "version_manifest_v2.json"
                    ));
                }
        );

        final var downloadWantedVersionManifest = tasks.register(
                DownloadWantedVersionManifestTask.TASK_NAME, DownloadWantedVersionManifestTask.class,
                task -> {
                    task.getManifest().convention(
                            downloadVersionsManifest.flatMap(DownloadVersionsManifestTask::getManifestFile)
                    );

                    task.getVersionFile().convention(() ->
                            new File(ext.getFileConstants().cacheFilesMinecraft, Constants.MINECRAFT_VERSION + ".json")
                    );
                }
        );

        final var downloadMinecraftJars = tasks.register(
                DownloadMinecraftJarsTask.TASK_NAME, DownloadMinecraftJarsTask.class,
                task -> {
                    task.getVersionFile().convention(
                            downloadWantedVersionManifest.flatMap(DownloadWantedVersionManifestTask::getVersionFile)
                    );

                    task.getClientJar().convention(() -> new File(
                            ext.getFileConstants().cacheFilesMinecraft,
                            Constants.MINECRAFT_VERSION + "-client.jar"
                    ));
                    task.getServerBootstrapJar().convention(() -> new File(
                            ext.getFileConstants().cacheFilesMinecraft,
                            Constants.MINECRAFT_VERSION + "-server-bootstrap.jar"
                    ));
                }
        );

        final var extractServerJar = tasks.register(
                ExtractServerJarTask.TASK_NAME, ExtractServerJarTask.class,
                task -> {
                    task.getServerBootstrapJar().convention(
                            downloadMinecraftJars.flatMap(DownloadMinecraftJarsTask::getServerBootstrapJar)
                    );

                    task.getServerJar().convention(() -> new File(
                            ext.getFileConstants().cacheFilesMinecraft,
                            Constants.MINECRAFT_VERSION + "-server.jar"
                    ));
                }
        );

        final var mergeJars = tasks.register(MergeJarsTask.TASK_NAME, MergeJarsTask.class, task -> {
            task.getClientJar().convention(downloadMinecraftJars.flatMap(DownloadMinecraftJarsTask::getClientJar));
            task.getServerJar().convention(extractServerJar.flatMap(ExtractServerJarTask::getServerJar));

            // TODO see if output jars like this can all go in a directory (build/minecraftJars/?)
            final File mergedFile = project.file(Constants.MINECRAFT_VERSION + "-merged.jar");
            task.getMergedFile().convention(() -> mergedFile);
        });

        final var downloadMinecraftLibraries = tasks.register(
                DownloadMinecraftLibrariesTask.TASK_NAME, DownloadMinecraftLibrariesTask.class,
                task -> {
                    task.getVersionFile().convention(
                            downloadWantedVersionManifest.flatMap(DownloadWantedVersionManifestTask::getVersionFile)
                    );
                    task.getLibrariesDir().convention(provideProjectDir(project, ext.getFileConstants().libraries));
                }
        );

        tasks.withType(MapJarTask.class).configureEach(task -> {
            task.getLibrariesDir().convention(
                    downloadMinecraftLibraries.flatMap(DownloadMinecraftLibrariesTask::getLibrariesDir)
            );
        });

        final var downloadPerVersionMappings = tasks.register(
                DOWNLOAD_PER_VERSION_MAPPINGS_TASK_NAME, DownloadMappingsTask.class,
                task -> {
                    task.getMappingsConfiguration().convention(
                            project.getConfigurations().named(Constants.PER_VERSION_MAPPINGS_NAME)
                    );
                    task.getJarFile().convention(
                            provideMappingsDestFile(mappingsDestDir, Constants.PER_VERSION_MAPPINGS_NAME, "jar")
                    );
                }
        );

        final var extractTinyPerVersionMappings = tasks.register(
                EXTRACT_TINY_PER_VERSION_MAPPINGS_TASK_NAME, ExtractTinyMappingsTask.class,
                task -> {
                    task.getJarFile().convention(downloadPerVersionMappings.flatMap(DownloadMappingsTask::getJarFile));
                    task.getTinyFile().convention(
                            provideMappingsDestFile(mappingsDestDir, Constants.PER_VERSION_MAPPINGS_NAME, "tiny")
                    );
                }
        );

        final var invertPerVersionMappings =
                tasks.register(InvertPerVersionMappingsTask.TASK_NAME, InvertPerVersionMappingsTask.class);

        final var buildMappingsTiny = tasks.register(BuildMappingsTinyTask.TASK_NAME, BuildMappingsTinyTask.class);

        final var mapPerVersionMappingsJar = tasks.register(
                MapPerVersionMappingsJarTask.TASK_NAME, MapPerVersionMappingsJarTask.class,
                task -> {
                    task.getInputJar().convention(mergeJars.flatMap(MergeJarsTask::getMergedFile));

                    task.getMappingsFile().convention(
                            extractTinyPerVersionMappings.flatMap(ExtractTinyMappingsTask::getTinyFile)
                    );

                    task.getOutputJar().convention(() -> ext.getFileConstants().perVersionMappingsJar);
                }
        );

        final var insertAutoGeneratedMappings = tasks.register(
                INSERT_AUTO_GENERATED_MAPPINGS_TASK_NAME, AddProposedMappingsTask.class,
                task -> {
                    task.getInputJar().convention(
                            mapPerVersionMappingsJar.flatMap(MapPerVersionMappingsJarTask::getOutputJar)
                    );

                    task.getInputMappings().convention(buildMappingsTiny.flatMap(BuildMappingsTinyTask::getOutputMappings));

                    task.getOutputMappings().convention(() ->
                            new File(ext.getFileConstants().buildDir, INSERT_AUTO_GENERATED_MAPPINGS_TASK_NAME + ".tiny")
                    );
                }
        );

        tasks.register(
                MergeTinyTask.TASK_NAME, MergeTinyTask.class,
                task -> {
                    task.getInput().convention(buildMappingsTiny.flatMap(BuildMappingsTinyTask::getOutputMappings));

                    task.getHashedTinyMappings().convention(
                            invertPerVersionMappings.flatMap(InvertPerVersionMappingsTask::getInvertedTinyFile)
                    );

                    task.getOutputMappings().convention(() -> new File(ext.getFileConstants().buildDir, "mappings.tiny"));
                }
        );

        final var mergeTinyV2 = tasks.register(MergeTinyV2Task.TASK_NAME, MergeTinyV2Task.class, task -> {
            // TODO eliminate this
            task.dependsOn("v2UnmergedMappingsJar");

            task.getInput().convention(
                    insertAutoGeneratedMappings.flatMap(AddProposedMappingsTask::getOutputMappings)
            );

            task.getHashedTinyMappings().convention(
                    invertPerVersionMappings.flatMap(InvertPerVersionMappingsTask::getInvertedTinyFile)
            );

            task.getOutputMappings().convention(() -> new File(ext.getFileConstants().buildDir, "merged2.tiny"));
        });

        tasks.register(TinyJarTask.TASK_NAME, TinyJarTask.class);

        tasks.register(CompressTinyTask.TASK_NAME, CompressTinyTask.class);

        tasks.register(DropInvalidMappingsTask.TASK_NAME, DropInvalidMappingsTask.class);

        tasks.register(MapNamedJarTask.TASK_NAME, MapNamedJarTask.class, task -> {
            // TODO eliminate this
            task.dependsOn("unpickHashedJar");

            // TODO make this take the output of unpickHashedJar
            task.getInputJar().convention(() -> ext.getFileConstants().unpickedJar);
            task.getMappingsFile().convention(
                    insertAutoGeneratedMappings.flatMap(AddProposedMappingsTask::getOutputMappings)
            );

            task.getOutputJar().convention(() -> ext.getFileConstants().namedJar);
        });

        tasks.register(CombineUnpickDefinitionsTask.TASK_NAME, CombineUnpickDefinitionsTask.class);

        tasks.register(RemapUnpickDefinitionsTask.TASK_NAME, RemapUnpickDefinitionsTask.class);

        tasks.register(OpenGlConstantUnpickGeneratorTask.TASK_NAME, OpenGlConstantUnpickGeneratorTask.class, task -> {
            task.getVersionFile().convention(
                    downloadMinecraftLibraries.flatMap(DownloadMinecraftLibrariesTask::getVersionFile)
            );

            task.getPerVersionMappingsJar().convention(
                    mapPerVersionMappingsJar.flatMap(MapPerVersionMappingsJarTask::getOutputJar)
            );

            task.getArtifactsByUrl().convention(
                    downloadMinecraftLibraries.flatMap(DownloadMinecraftLibrariesTask::getArtifactsByUrl)
            );

            task.getUnpickGlStateManagerDefinitions().convention(() ->
                    ext.getFileConstants().unpickGlStateManagerDefinitions
            );

            task.getUnpickGlDefinitions().convention(() -> ext.getFileConstants().unpickGlDefinitions);
        });

        tasks.register(GeneratePackageInfoMappingsTask.TASK_NAME, GeneratePackageInfoMappingsTask.class);

        tasks.register(DownloadDictionaryFileTask.TASK_NAME, DownloadDictionaryFileTask.class);

        final var mappingLint = tasks.register(MappingLintTask.TASK_NAME, MappingLintTask.class);

        tasks.register(FindDuplicateMappingFilesTask.TASK_NAME, FindDuplicateMappingFilesTask.class,
                task -> {
                    task.getMappingDirectory().convention(mappingLint.get().getMappingDirectory());
                    mappingLint.get().dependsOn(task);
                }
        );

        final var checkIntermediaryMappings =
                tasks.register(CheckIntermediaryMappingsTask.TASK_NAME, CheckIntermediaryMappingsTask.class);

        final var downloadIntermediaryMappings = tasks.register(
                DownloadIntermediaryMappingsTask.TASK_NAME, DownloadIntermediaryMappingsTask.class,
                task -> {
                    task.getMappingsConfiguration().convention(
                            project.getConfigurations().named(Constants.INTERMEDIARY_MAPPINGS_NAME)
                    );
                    task.getJarFile().convention(
                            provideMappingsDestFile(mappingsDestDir, Constants.INTERMEDIARY_MAPPINGS_NAME, "jar")
                    );

                    task.dependsOn(checkIntermediaryMappings);
                    task.onlyIf(unused -> checkIntermediaryMappings.get().isPresent());
                }
        );

        final var extractTinyIntermediaryMappings = tasks.register(
                EXTRACT_TINY_INTERMEDIARY_MAPPINGS_TASK_NAME, ExtractTinyMappingsTask.class,
                task -> {
                    task.getJarFile().convention(downloadIntermediaryMappings.flatMap(DownloadMappingsTask::getJarFile));
                    task.getTinyFile().convention(
                            provideMappingsDestFile(mappingsDestDir, Constants.INTERMEDIARY_MAPPINGS_NAME, "tiny")
                    );
                }
        );

        tasks.register(
                MergeIntermediaryTask.TASK_NAME, MergeIntermediaryTask.class,
                task -> {
                    task.onlyIf(unused -> checkIntermediaryMappings.get().isPresent());

                    task.getInput().convention(
                            extractTinyIntermediaryMappings.flatMap(ExtractTinyMappingsTask::getTinyFile)
                    );

                    task.getMergedTinyMappings().convention(mergeTinyV2.flatMap(MergeTinyV2Task::getOutputMappings));

                    task.getOutputMappings().convention(() ->
                            new File(ext.getFileConstants().buildDir, "mappings-intermediaryMerged.tiny")
                    );
                }
        );

        tasks.register(RemoveIntermediaryTask.TASK_NAME, RemoveIntermediaryTask.class);

        final var checkTargetVersionExists = tasks.register(
                CheckTargetVersionExistsTask.TASK_NAME, CheckTargetVersionExistsTask.class,
                task -> {
                    task.outputsNeverUpToDate();
                    task.getMetaFile().convention(() -> new File(
                            ext.getFileConstants().cacheFilesMinecraft,
                            QUILT_MAPPINGS_PREFIX + Constants.MINECRAFT_VERSION + ".json"
                    ));
                }
        );

        tasks.withType(TargetVersionConsumingTask.class).configureEach(task -> {
            // TODO temporary, until CheckTargetVersionExistsTask is converted to a BuildService
            task.dependsOn(checkTargetVersionExists);

            task.getTargetVersion().convention(
                    checkTargetVersionExists.flatMap(CheckTargetVersionExistsTask::getTargetVersion)
            );

            task.onlyIf(unused -> task.getTargetVersion().isPresent());
        });

        final var downloadTargetMappingsJar = tasks.register(
                DownloadTargetMappingJarTask.TASK_NAME, DownloadTargetMappingJarTask.class,
                task -> {
                    task.getTargetUnpickConstantsFile().convention(task.provideVersionedProjectFile(version ->
                            Path.of(TARGET_MAPPINGS_DIR, QUILT_MAPPINGS_PREFIX + version + "-constants.jar")
                    ));

                    task.getTargetJar().convention(task.provideVersionedProjectFile(version ->
                            Path.of(MappingsPlugin.TARGET_MAPPINGS_DIR, "quilt-mappings-" + version + "-v2.jar")
                    ));
                }
        );

        final var extractTargetMappingsJar = tasks.register(
                ExtractTargetMappingJarTask.TASK_NAME, ExtractTargetMappingJarTask.class,
                task -> {
                    task.getTargetJar().convention(
                            downloadTargetMappingsJar.flatMap(DownloadTargetMappingJarTask::getTargetJar)
                    );
                    task.getExtractionDest().convention(task.provideVersionedProjectDir(version ->
                            Path.of(MappingsPlugin.TARGET_MAPPINGS_DIR, "quilt-mappings-" + version)
                    ));
                }
        );

        final var checkUnpickVersionsMatch = tasks.register(
                CheckUnpickVersionsMatchTask.TASK_NAME, CheckUnpickVersionsMatchTask.class,
                task -> {
                    task.getUnpickJson().convention(
                            extractTargetMappingsJar.flatMap(ExtractTargetMappingJarTask::getExtractionDest)
                                    .map(dest -> dest.dir("extras").file("unpick.json"))
                    );
                }
        );

        tasks.withType(UnpickVersionsMatchConsumingTask.class).configureEach(task -> {
            // TODO temporary, until CheckUnpickVersionsMatchTask is converted to a BuildService
            task.dependsOn(checkUnpickVersionsMatch);

            task.getUnpickVersionsMatch().convention(
                    checkUnpickVersionsMatch.flatMap(CheckUnpickVersionsMatchTask::isMatch)
            );

            task.onlyIf(unused -> task.getUnpickVersionsMatch().getOrElse(false));
        });

        final var remapTargetUnpickDefinitions = tasks.register(
                RemapTargetUnpickDefinitionsTask.TASK_NAME, RemapTargetUnpickDefinitionsTask.class,
                task -> {
                    task.getInput().convention(
                            extractTargetMappingsJar.flatMap(ExtractTargetMappingJarTask::getExtractionDest)
                                    .map(dest -> dest.dir("extras").file("definitions.unpick"))
                    );

                    task.getMappings().convention(
                            extractTargetMappingsJar.flatMap(ExtractTargetMappingJarTask::getExtractionDest)
                                    .map(dest -> dest.dir("mappings").file("mappings.tiny"))
                    );

                    task.getOutput().convention(task.provideVersionedProjectFile(version ->
                            Path.of(TARGET_MAPPINGS_DIR, QUILT_MAPPINGS_PREFIX + version + "remapped-unpick.unpick")
                    ));
                }
        );

        final var unpickTargetJar = tasks.register(UnpickTargetJarTask.TASK_NAME, UnpickTargetJarTask.class, task -> {
            task.getInputFile().convention(
                    mapPerVersionMappingsJar.flatMap(MapPerVersionMappingsJarTask::getOutputJar)
            );

            task.getUnpickDefinition().convention(
                    remapTargetUnpickDefinitions.flatMap(RemapTargetUnpickDefinitionsTask::getOutput)
            );

            task.getUnpickConstantsJar().convention(
                    downloadTargetMappingsJar.flatMap(DownloadTargetMappingJarTask::getTargetUnpickConstantsFile)
            );

            task.getOutputFile().convention(task.provideVersionedProjectFile(version ->
                    Path.of(TARGET_MAPPINGS_DIR, QUILT_MAPPINGS_PREFIX + version + "-unpicked.jar")
            ));
        });

        final var remapTargetMinecraftJar = tasks.register(
                RemapTargetMinecraftJarTask.TASK_NAME, RemapTargetMinecraftJarTask.class,
                task -> {
                    task.dependsOn(unpickTargetJar);

                    task.getInputJar().convention(unpickTargetJar.flatMap(UnpickTargetJarTask::getOutputFile));

                    task.getMappingsFile().convention(
                            extractTargetMappingsJar.flatMap(ExtractTargetMappingJarTask::getExtractionDest)
                                    .map(dest -> dest.dir("mappings").file("mappings.tiny"))
                    );

                    task.getOutputJar().convention(task.provideVersionedProjectFile(version ->
                            Path.of(TARGET_MAPPINGS_DIR, QUILT_MAPPINGS_PREFIX + version + "-named.jar")
                    ));
                }
        );

        tasks.register(DECOMPILE_TARGET_VINEFLOWER_TASK_NAME, DecompileTargetTask.class, task -> {
            task.getDecompiler().convention(Decompilers.VINEFLOWER);

            task.getNamespace().convention("named");

            task.getInput().convention(remapTargetMinecraftJar.flatMap(RemapTargetMinecraftJarTask::getOutputJar));

            task.getLibraries().convention(
                    project.files(project.getConfigurations().named("decompileClasspath"))
            );

            task.getTargetMappingsFile().convention(
                    extractTargetMappingsJar.flatMap(ExtractTargetMappingJarTask::getExtractionDest)
                            .map(dest -> dest.dir("mappings").file("mappings.tiny"))
            );

            task.getOutput().convention(() -> project.file("namedTargetSrc"));
        });
    }
}