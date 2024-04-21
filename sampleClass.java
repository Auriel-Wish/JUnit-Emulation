import java.util.*;
import java.util.regex.PatternSyntaxException;

public class sampleClass {
    @AfterClass public static void ac2() {}

    @Test public boolean m3() {
        return false;
    }

    @Before public void b1() {}

    @AfterClass public static void ac3() {}

    @Test public boolean m4() throws Exception {
        throw new Exception();
    }

    @BeforeClass public static void bc1() {}

    @Test public boolean m1() {
        return true;
    }

    @After public void a2() {}

    @BeforeClass public static void bc2() {}

    @Before public void b2() {}

    @Test public boolean m2() throws IllegalArgumentException {
        throw new IllegalArgumentException();
    }

    @BeforeClass public static void bc3() {}

    @AfterClass public static void ac1() {}

    @After public void a1() {}

//    @Property public boolean testIntRange1(@IntRange(min=-10, max=10) Integer i) {
//        return i < 0;
//    }
//
//    @Property public boolean testIntRange2(@IntRange(min=-10, max=100) Integer i, @IntRange(min=3, max=7) Integer j, @IntRange(min=0, max=1) Integer k) {
//        return true;
//    }
//
//    @Property public boolean testIntRange3(@IntRange(min=5, max=10) Integer i, @IntRange(min=3, max=7) Integer j, @IntRange(min=0, max=1) Integer k) throws Exception {
//        if (k == 1) {throw new Exception();}
//        return true;
//    }
//
//    @Property public boolean testStringSet(@StringSet(strings={"s1", "s2"}) String s) {
//        return s.contains("1");
//    }

    @Property public boolean testForAll1(@ForAll(name="genIntSet", times=10) Object o) {
        Set s = (Set) o;
        s.add("foo");
        return s.contains("foo");
    }

    int count1 = 0;
    public Object genIntSet() {
        Set s = new HashSet();
        for (int i=0; i < count1; i++) { s.add(i); }
        count1++;
        return s;
    }

    @Property public boolean testForAll2(@ForAll(name="genIntSet2", times=10000) Object o) {
        Set s = (Set) o;
        s.add("foo");
        return s.contains("foo");
    }

    int count2 = 0;
    public Object genIntSet2() {
        Set s = new HashSet();
        for (int i=0; i < count2; i++) { s.add(i); }
        count2++;
        return s;
    }

    @Property public boolean testForAll3(@ForAll(name="genIntSet3", times=10000) Object o) {
        Set s = (Set) o;
        return !s.contains(8);
    }

    int count3 = 0;
    public Object genIntSet3() {
        Set s = new HashSet();
        for (int i=0; i < count3; i++) { s.add(i); }
        count3++;
        return s;
    }


//    @Property public boolean testListRange1(@ListLength(min=0, max=2) List<@IntRange(min=5, max=7) Integer> list) {
//        return !list.contains(6);
//    }
//
//    @Property public boolean testListRange2(@ListLength(min=0, max=2) List<@StringSet(strings={"s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8"}) String> list) {
//        return !list.contains("s6");
//    }
//
    @Property public boolean testListRange3(@ListLength(min=3, max=7) List<@StringSet(strings={"s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8"}) String> list) {
        return !list.contains("s10");
    }

//    @Property public boolean testListRange4(@ListLength(min=0, max=2) List<@ListLength(min=0, max=2) List<@IntRange(min=5, max=7) Integer>> list) {
//        List<Integer> l = new ArrayList<>();
//        l.add(5);
//        l.add(7);
//        return !list.contains(l);
//    }
}
