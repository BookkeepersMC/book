package book.mappings.tasks.mappings;

import java.util.List;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.JavaExec;
import book.mappings.Constants;
import book.mappings.tasks.MappingsTask;

public class EnigmaMappingsTask extends JavaExec implements MappingsTask {
    public EnigmaMappingsTask() {
        this.setGroup(Constants.Groups.MAPPINGS_GROUP);
        this.getMainClass().set("org.quiltmc.enigma.gui.Main");
        this.classpath(getProject().getConfigurations().getByName("enigmaRuntime"));
        jarToMap = getObjectFactory().fileProperty();
        jvmArgs("-Xmx2048m");
    }

    @Override
    public void exec() {
        args(List.of(
                "-jar", this.jarToMap.get().getAsFile().getAbsolutePath(), "-mappings", getProject().file("mappings").getAbsolutePath(), "-profile", "enigma_profile.json"
        ));
        super.exec();
    }

    @InputFile
    private final RegularFileProperty jarToMap;

    public RegularFileProperty getJarToMap() {
        return jarToMap;
    }
}
