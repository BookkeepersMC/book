package book.mappings.tasks.build;

import java.io.IOException;
import java.util.Map;

import book.mappings.tasks.decompile.DecompileTask;

import org.apache.commons.io.FileUtils;

public abstract class GenFakeSourceTask extends DecompileTask {
    public static final String TASK_NAME = "genFakeSource";

    public GenFakeSourceTask() {
        this.getDecompilerOptions().putAll(Map.of(
                "rsy", "1",
                "dgs", "1",
                "pll", "99999"
        ));
    }

    @Override
    public void decompile() throws IOException {
        FileUtils.deleteDirectory(this.getOutput().get().getAsFile());

        super.decompile();

        this.getLogger().lifecycle(":Fake source generated");
    }
}
