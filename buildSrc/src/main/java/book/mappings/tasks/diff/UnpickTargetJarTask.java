package book.mappings.tasks.diff;

import book.mappings.tasks.unpick.UnpickJarTask;

public abstract class UnpickTargetJarTask extends UnpickJarTask implements UnpickVersionsMatchConsumingTask {
    public static final String TASK_NAME = "unpickTargetJar";
}
