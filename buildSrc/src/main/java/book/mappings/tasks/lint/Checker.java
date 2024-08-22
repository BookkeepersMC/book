package book.mappings.tasks.lint;

import java.io.Serializable;
import java.util.Set;
import java.util.function.Function;

import org.quiltmc.enigma.api.translation.mapping.EntryMapping;
import org.quiltmc.enigma.api.translation.representation.AccessFlags;
import org.quiltmc.enigma.api.translation.representation.entry.Entry;
import org.quiltmc.enigma.api.translation.representation.entry.FieldEntry;

/**
 * A checker checks mapping entries for formatting/convention errors and warnings.
 *
 * @param <E> the checked entry
 */
public interface Checker<E extends Entry<?>> extends Serializable {
    /**
     * The default checkers in Filament, following Yarn conventions.
     */
    Set<Checker<Entry<?>>> DEFAULT_CHECKERS = Set.of(
            new JavadocChecker(),
            new EntryNamingChecker(),
            new SpellingChecker(),
            new FieldNamingChecker().withTypeGuard(FieldEntry.class)
    );

    /**
     * Checks an entry and reports any errors/warnings to a reporter.
     *
     * @param entry          the entry to check
     * @param mapping        its mapping
     * @param accessProvider a function that provides the access flags of an entry
     * @param errorReporter  the error reporter
     */
    void check(E entry, EntryMapping mapping, Function<Entry<?>, AccessFlags> accessProvider, ErrorReporter errorReporter);

    /**
     * Updates the checker with extension properties
     *
     * @param parameters the extension for the lint action
     */
    default void update(MappingLintTask.LintParameters parameters) {
    }

    /**
     * Creates a new checker with a type guard. The created checker delegates to this checker,
     * but only when the entry is an instance of the specified type.
     *
     * @param entryType the required entry type
     * @return the created checker
     */
    default Checker<Entry<?>> withTypeGuard(Class<E> entryType) {
        return (entry, mapping, access, errorReporter) -> {
            if (entryType.isInstance(entry)) {
                check(entryType.cast(entry), mapping, access, errorReporter);
            }
        };
    }
}
