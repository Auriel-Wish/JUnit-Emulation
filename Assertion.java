public class Assertion {
    static forObj assertThat(Object o) {
	    return new forObj(o);
    }

    static forStr assertThat(String s) {
	    return new forStr(s);
    }

    static forBool assertThat(boolean b) {
        return new forBool(b);
    }

    static forInt assertThat(int i) {
	    return new forInt(i);
    }
}