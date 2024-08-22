package book.mappings.decompile.javadoc;

public interface FieldJavadocProvider {
    FieldJavadocProvider EMPTY = (fieldName, descriptor, owner) -> null;

    String provideFieldJavadoc(String fieldName, String descriptor, String owner);
}
