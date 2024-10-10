package book.mappings.tasks.jarmapping;

import book.mappings.Constants;

public abstract class MapPerVersionMappingsJarTask extends MapJarTask {
    public static final String TASK_NAME = "mapPerVersionMappingsJar";

    public MapPerVersionMappingsJarTask() {
        super(Constants.Groups.MAP_JAR_GROUP, "official", Constants.PER_VERSION_MAPPINGS_NAME);
    }
}
