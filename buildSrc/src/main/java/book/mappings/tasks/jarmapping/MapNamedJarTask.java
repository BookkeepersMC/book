package book.mappings.tasks.jarmapping;

import java.util.Map;

import book.mappings.Constants;

public abstract class MapNamedJarTask extends MapJarTask {
    public static final String TASK_NAME = "mapNamedJar";

    public MapNamedJarTask() {
        super(Constants.Groups.MAP_JAR_, Constants.PER_VERSION_MAPPINGS_NAME, "named");
    }

    public Map<String, String> getAdditionalMappings() {
        return JAVAX_TO_JETBRAINS;
    }
}

