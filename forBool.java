class forBool {
    boolean b;

    public forBool(boolean b) {
        this.b = b;
    }

    forBool isEqualTo(boolean b2) {
        if (b != b2) {
            throw new RuntimeException();
        }
        return this;
    }

    forBool isTrue() {
        if (!b) {
            throw new RuntimeException();
        }
        return this;
    }

    forBool isFalse() {
        if (b) {
            throw new RuntimeException();
        }
        return this;
    }
}