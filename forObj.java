public class forObj {
    Object o;

    public forObj(Object o) {
        this.o = o;
    }
    forObj isNotNull() {
        if (o == null) {
            throw new RuntimeException();
        }
        return this;
    }

    forObj isNull() {
        if (o != null) {
            throw new RuntimeException();
        }
        return this;
    }

    forObj isEqualTo(Object o2) {
        if (!o.equals(o2)) {
            throw new RuntimeException();
        }
        return this;
    }

    forObj isNotEqualTo(Object o2) {
        if (o.equals(o2)) {
            throw new RuntimeException();
        }
        return this;
    }

    forObj isInstanceOf(Class c) {
        if (!(c.isInstance(o))) {
            throw new RuntimeException();
        }
        return this;
    }
}