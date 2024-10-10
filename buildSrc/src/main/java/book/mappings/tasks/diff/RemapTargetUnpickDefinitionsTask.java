package book.mappings.tasks.diff;

import book.mappings.tasks.unpick.RemapUnpickDefinitionsTask;

public abstract class RemapTargetUnpickDefinitionsTask extends RemapUnpickDefinitionsTask implements UnpickVersionsMatchConsumingTask {
    public static final String TASK_NAME = "remapTargetUnpickDefinitions";
}
