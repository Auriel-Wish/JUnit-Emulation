class forInt {
    int i;

    public forInt(int i) {
        this.i = i;
    }

    forInt isEqualTo(int i2) {
        if (i != i2) {
            throw new RuntimeException();
        }

        return this;
    }

    forInt isLessThan(int i2) {
        if (i >= i2) {
            throw new RuntimeException();
        }

        return this;
    }

    forInt isGreaterThan(int i2) {
        if (i <= i2) {
            throw new RuntimeException();
        }

        return this;
    }
}