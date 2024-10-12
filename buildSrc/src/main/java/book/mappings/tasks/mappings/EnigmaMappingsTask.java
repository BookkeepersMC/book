package book.mappings.tasks.mappings;

public abstract class EnigmaMappingsTask extends AbstractEnigmaMappingsTask {

    public EnigmaMappingsTask() {
        // this configuration can stay here because it's what make this an EnigmaMappingsTask
        this.getMainClass().convention(org.quiltmc.enigma.gui.Main.class.getName()).finalizeValue();
    }
}
