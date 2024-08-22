package book.mappings;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;
import book.mappings.tasks.build.BuildMappingsTinyTask;
import book.mappings.tasks.build.CompressTinyTask;
import book.mappings.tasks.build.DropInvalidMappingsTask;
import book.mappings.tasks.build.GeneratePackageInfoMappingsTask;
import book.mappings.tasks.build.InvertPerVersionMappingsTask;
import book.mappings.tasks.build.MergeIntermediaryTask;
import book.mappings.tasks.build.MergeTinyTask;
import book.mappings.tasks.build.MergeTinyV2Task;
import book.mappings.tasks.build.RemoveIntermediaryTask;
import book.mappings.tasks.build.TinyJarTask;
import book.mappings.tasks.diff.CheckTargetVersionExistsTask;
import book.mappings.tasks.diff.CheckUnpickVersionsMatchTask;
import book.mappings.tasks.diff.DownloadTargetMappingJarTask;
import book.mappings.tasks.diff.RemapTargetMinecraftJarTask;
import book.mappings.tasks.jarmapping.MapNamedJarTask;
import book.mappings.tasks.jarmapping.MapPerVersionMappingsJarTask;
import book.mappings.tasks.lint.DownloadDictionaryFileTask;
import book.mappings.tasks.lint.FindDuplicateMappingFilesTask;
import book.mappings.tasks.lint.MappingLintTask;
import book.mappings.tasks.setup.CheckIntermediaryMappingsTask;
import book.mappings.tasks.setup.DownloadIntermediaryMappingsTask;
import book.mappings.tasks.setup.DownloadMinecraftJarsTask;
import book.mappings.tasks.setup.DownloadMinecraftLibrariesTask;
import book.mappings.tasks.setup.DownloadPerVersionMappingsTask;
import book.mappings.tasks.setup.DownloadVersionsManifestTask;
import book.mappings.tasks.setup.DownloadWantedVersionManifestTask;
import book.mappings.tasks.setup.ExtractServerJarTask;
import book.mappings.tasks.setup.MergeJarsTask;
import book.mappings.tasks.unpick.CombineUnpickDefinitionsTask;
import book.mappings.tasks.unpick.RemapUnpickDefinitionsTask;
import book.mappings.tasks.unpick.gen.OpenGlConstantUnpickGenerator;

public class MappingsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getExtensions().add("mappings", new MappingsExtension(target));

        TaskContainer tasks = target.getTasks();
        tasks.create(DownloadVersionsManifestTask.TASK_NAME, DownloadVersionsManifestTask.class);
        tasks.create(DownloadWantedVersionManifestTask.TASK_NAME, DownloadWantedVersionManifestTask.class);
        tasks.create(DownloadMinecraftJarsTask.TASK_NAME, DownloadMinecraftJarsTask.class);
        tasks.create(ExtractServerJarTask.TASK_NAME, ExtractServerJarTask.class);
        tasks.create(MergeJarsTask.TASK_NAME, MergeJarsTask.class);
        tasks.create(DownloadMinecraftLibrariesTask.TASK_NAME, DownloadMinecraftLibrariesTask.class);

        tasks.create(DownloadPerVersionMappingsTask.TASK_NAME, DownloadPerVersionMappingsTask.class);
        tasks.create(InvertPerVersionMappingsTask.TASK_NAME, InvertPerVersionMappingsTask.class);
        tasks.create(BuildMappingsTinyTask.TASK_NAME, BuildMappingsTinyTask.class);
        tasks.create(MergeTinyTask.TASK_NAME, MergeTinyTask.class);
        tasks.create(MergeTinyV2Task.TASK_NAME, MergeTinyV2Task.class);
        tasks.create(TinyJarTask.TASK_NAME, TinyJarTask.class);
        tasks.create(CompressTinyTask.TASK_NAME, CompressTinyTask.class);
        tasks.create(DropInvalidMappingsTask.TASK_NAME, DropInvalidMappingsTask.class);

        tasks.create(MapPerVersionMappingsJarTask.TASK_NAME, MapPerVersionMappingsJarTask.class);
        tasks.create(MapNamedJarTask.TASK_NAME, MapNamedJarTask.class);

        tasks.create(CombineUnpickDefinitionsTask.TASK_NAME, CombineUnpickDefinitionsTask.class);
        tasks.create(RemapUnpickDefinitionsTask.TASK_NAME, RemapUnpickDefinitionsTask.class);
        tasks.create(OpenGlConstantUnpickGenerator.TASK_NAME, OpenGlConstantUnpickGenerator.class);

        tasks.create(GeneratePackageInfoMappingsTask.TASK_NAME, GeneratePackageInfoMappingsTask.class);
        tasks.create(DownloadDictionaryFileTask.TASK_NAME, DownloadDictionaryFileTask.class);
        final var mappingLintTask = tasks.create(MappingLintTask.TASK_NAME, MappingLintTask.class);
        tasks.create(FindDuplicateMappingFilesTask.TASK_NAME, FindDuplicateMappingFilesTask.class, task -> {
            task.getMappingDirectory().set(mappingLintTask.getMappingDirectory());
            mappingLintTask.dependsOn(task);
        });

        tasks.create(CheckIntermediaryMappingsTask.TASK_NAME, CheckIntermediaryMappingsTask.class);
        tasks.create(DownloadIntermediaryMappingsTask.TASK_NAME, DownloadIntermediaryMappingsTask.class);
        tasks.create(MergeIntermediaryTask.TASK_NAME, MergeIntermediaryTask.class);
        tasks.create(RemoveIntermediaryTask.TASK_NAME, RemoveIntermediaryTask.class);

        tasks.create(CheckTargetVersionExistsTask.TASK_NAME, CheckTargetVersionExistsTask.class);
        tasks.create(DownloadTargetMappingJarTask.TASK_NAME, DownloadTargetMappingJarTask.class);
        tasks.create(CheckUnpickVersionsMatchTask.TASK_NAME, CheckUnpickVersionsMatchTask.class);
        tasks.create(RemapTargetMinecraftJarTask.TASK_NAME, RemapTargetMinecraftJarTask.class);
    }

    public static MappingsExtension getExtension(Project project) {
        return project.getExtensions().getByType(MappingsExtension.class);
    }
}
