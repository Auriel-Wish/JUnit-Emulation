import java.util.Map;

public class mainTest {
    public static void main(String[] args) {
//        Map<String, Throwable> allErrors = Unit.testClass("sampleClass");
//        System.out.println(allErrors);
        Map<String, Object[]> allTests = Unit.quickCheckClass("sampleClass");
        int counter = 0;
        for (Map.Entry<String, Object[]> entry : allTests.entrySet()) {
            String fname = entry.getKey();
            Object[] params = entry.getValue();

            if (params != null) {
                System.out.println("\n" + fname + " failed at: ");
                System.out.print("    ");
                for (Object o : params) {
                    System.out.print(o.toString() + ", ");
                }
            }
            else {
                System.out.println("\n\n" + fname + " succeeded all tests or the first 100 tests");
            }
        }
    }
}