class forStr {
    String s;

    public forStr(String s) {
        this.s = s;
    }

    forStr isNotNull() {
        if (s == null) {
            throw new RuntimeException();
        }
        return this;
    }

    forStr isNull() {
        if (s != null) {
            throw new RuntimeException();
        }
        return this;
    }

    forStr isEqualTo(Object o) {
        if (!s.equals(o)) {
            throw new RuntimeException();
        }
        return this;
    }

    forStr isNotEqualTo(Object o) {
        if (s.equals(o)) {
            throw new RuntimeException();
        }
        return this;
    }

    forStr startsWith(String s2) {
        if (!s.startsWith(s2)) {
            throw new RuntimeException();
        }
        return this;
    }

    forStr isEmpty() {
        if (!s.isEmpty()) {
            throw new RuntimeException();
        }
        return this;
    }

    forStr contains(String s2) {
        if (!s.contains(s2)) {
            throw new RuntimeException();
        }
        return this;
    }
}