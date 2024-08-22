package book.mappings.decompile.javadoc;

public interface ClassJavadocProvider {
    ClassJavadocProvider EMPTY = (className, isRecord) -> null;

    String provideClassJavadoc(String className, boolean isRecord);
}
