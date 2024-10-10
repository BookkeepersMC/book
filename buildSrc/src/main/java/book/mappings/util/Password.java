package book.mappings.util;

import java.io.Serializable;

public record Password(String password) implements Serializable {
    @Override
    public String toString() {
        return "<password hidden>";
    }
}
