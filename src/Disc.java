public enum Disc {
    EMPTY(0),
    BLACK(1),
    WHITE(2),
    POSSIBLE(3);

    private int value;

    Disc(int value) {
        this.value = value;
    }

    public int getNumber() {
        return value;
    }

    @Override
    public String toString() {
        return switch (this) {
            case BLACK -> "◌";
            case WHITE -> "●";
            case EMPTY -> " ";
            case POSSIBLE -> "x";
        };
    }

    public Disc opponent() {
        return switch (this) {
            case BLACK -> WHITE;
            case WHITE -> BLACK;
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }

    public Disc switchColor() {
        switch (this) {
            case BLACK -> {
                return WHITE;
            }
            case WHITE -> {
                return BLACK;
            }
            case EMPTY -> {
                return EMPTY;
            }
        }
        return EMPTY;
    }
}
