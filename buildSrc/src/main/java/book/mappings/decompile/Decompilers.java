package book.mappings.decompile;

import book.mappings.decompile.vineflower.VineflowerDecompiler;

public enum Decompilers {
    VINEFLOWER(VineflowerDecompiler::new);

    private final DecompilerProvider provider;

    Decompilers(DecompilerProvider decompilerProvider) {
        this.provider = decompilerProvider;
    }

    public DecompilerProvider getProvider() {
        return this.provider;
    }
}
