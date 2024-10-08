package book.mappings.tasks.unpick;

import java.util.List;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.OutputFile;
import book.mappings.Constants;
import book.mappings.tasks.MappingsTask;

public class UnpickJarTask extends JavaExec implements MappingsTask {
    private final RegularFileProperty inputFile;
    private final RegularFileProperty outputFile;
    private final RegularFileProperty unpickDefinition;
    private final RegularFileProperty unpickConstantsJar;

    public UnpickJarTask() {
        this.setGroup(Constants.Groups.UNPICK);

        this.getMainClass().set("daomephsta.unpick.cli.Main");
        classpath(getProject().getConfigurations().getByName("unpick"));

        ObjectFactory objectFactory = getProject().getObjects();
        inputFile = objectFactory.fileProperty();
        outputFile = objectFactory.fileProperty();
        unpickDefinition = objectFactory.fileProperty();
        unpickConstantsJar = objectFactory.fileProperty();
    }

    @Override
    public void exec() {
        args(List.of(
                inputFile.get().getAsFile().getAbsolutePath(), outputFile.get().getAsFile().getAbsolutePath(), unpickDefinition.get().getAsFile().getAbsolutePath(), unpickConstantsJar.get().getAsFile().getAbsolutePath()
        ));
        args(getProject().getConfigurations().getByName("decompileClasspath").getFiles());
        super.exec();
    }

    @InputFile
    public RegularFileProperty getInputFile() {
        return inputFile;
    }

    @OutputFile
    public RegularFileProperty getOutputFile() {
        return outputFile;
    }

    @InputFile
    public RegularFileProperty getUnpickDefinition() {
        return unpickDefinition;
    }

    @InputFile
    public RegularFileProperty getUnpickConstantsJar() {
        return unpickConstantsJar;
    }
}
