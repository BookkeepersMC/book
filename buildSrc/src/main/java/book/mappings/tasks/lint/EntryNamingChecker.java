package book.mappings.tasks.lint;

import java.util.function.Function;

import org.quiltmc.enigma.api.translation.mapping.EntryMapping;
import org.quiltmc.enigma.api.translation.representation.AccessFlags;
import org.quiltmc.enigma.api.translation.representation.entry.ClassEntry;
import org.quiltmc.enigma.api.translation.representation.entry.Entry;
import org.quiltmc.enigma.api.translation.representation.entry.FieldEntry;
import org.quiltmc.enigma.api.translation.representation.entry.MethodEntry;

public class EntryNamingChecker implements Checker<Entry<?>> {
    private static final String UNMAPPED_CLASS_PACKAGE = "net/minecraft/unmapped/";

    @Override
    public void check(Entry<?> entry, EntryMapping mapping, Function<Entry<?>, AccessFlags> accessProvider, ErrorReporter errorReporter) {
        if (mapping.targetName() == null) {
            return;
        }

        if (entry instanceof FieldEntry) {
            // Fields have their own naming checker
            return;
        }

        String name = mapping.targetName();
        AccessFlags access = accessProvider.apply(entry);

        if (access == null) {
            throw new RuntimeException("Invalid mappings detected. Please run './gradlew dropInvalidMappings'.");
        }

        if (entry instanceof ClassEntry) {
            if (name.startsWith(UNMAPPED_CLASS_PACKAGE)) {
                errorReporter.error("named class in unmapped classes package");
            } else {
                name = name.substring(name.lastIndexOf('/') + 1);
                if (!startsWithUppercase(name)) {
                    errorReporter.error("class name does not start with uppercase character");
                }
            }
        } else {
            if (entry instanceof MethodEntry) {
                if (entry.getName().equals("<init>") || entry.getName().equals("<clinit>")) {
                    errorReporter.error("constructor method has a name");
                    return;
                }
            }

            if (startsWithUppercase(name)) {
                errorReporter.error("non-class entry name starts with uppercase character");
            }
        }
    }

    private static boolean startsWithUppercase(String s) {
        return Character.isUpperCase(s.charAt(0));
    }
}
