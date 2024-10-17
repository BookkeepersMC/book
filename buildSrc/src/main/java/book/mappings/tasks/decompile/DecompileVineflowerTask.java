package book.mappings.tasks.decompile;

import java.io.IOException;

import book.mappings.decompile.Decompilers;

import org.apache.commons.io.FileUtils;

public abstract class DecompileVineflowerTask extends DecompileTask {
    public DecompileVineflowerTask() {
        this.getDecompiler().set(Decompilers.VINEFLOWER);
        this.getDecompiler().finalizeValue();
    }

    @Override
    public void decompile() throws IOException {
        FileUtils.deleteDirectory(this.getOutput().get().getAsFile());

        super.decompile();
    }
}
