import java.util.*;
import java.lang.reflect.*;
import java.lang.Class;
import java.util.Arrays;
import java.lang.annotation.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Unit {
    private static int MAX_TESTS = 100;
    private static void makeLists(List<Method> beforeClass, List<Method> afterClass, List<Method> before, List<Method> after, List<Method> tests, Method[] methods) {
        for (Method method : methods) {
            Annotation[] a = method.getDeclaredAnnotations();
            if (a.length > 0) {
                switch (a[0].toString()) {
                    case "@BeforeClass()" -> distributeMethods(a, beforeClass, method);
                    case "@AfterClass()" -> distributeMethods(a, afterClass, method);
                    case "@Before()" -> distributeMethods(a, before, method);
                    case "@After()" -> distributeMethods(a, after, method);
                    case "@Test()" -> distributeMethods(a, tests, method);
                }
            }
        }
    }
    public static Map<String, Throwable> testClass(String name) {
        Map<String, Throwable> allErrors = new HashMap<>();
        List<Method> beforeClass = new ArrayList<>();
        List<Method> afterClass = new ArrayList<>();
        List<Method> before = new ArrayList<>();
        List<Method> after = new ArrayList<>();
        List<Method> tests = new ArrayList<>();

        try {
            Class<?> c = Class.forName(name);
            Method[] methods = c.getDeclaredMethods();
            Constructor<?> cons = c.getConstructor();
            Object o = cons.newInstance();
            Arrays.sort(methods, Comparator.comparing(Method::getName));

            makeLists(beforeClass, afterClass, before, after, tests, methods);
            for (Method method : beforeClass) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException();
                }
                method.invoke(o);
            }
            for (Method method : tests) {
                for (Method methodBefore : before) {
                    methodBefore.invoke(o);
                }
                try {
                    method.invoke(o);
                    allErrors.put(method.getName(), null);
                }
                catch (InvocationTargetException e) {
                    allErrors.put(method.getName(), e.getTargetException());
                }
                for (Method methodAfter : after) {
                    methodAfter.invoke(o);
                }
            }
            for (Method method : afterClass) {
                if (!Modifier.isStatic(method.getModifiers())) {
                    throw new RuntimeException();
                }
                method.invoke(o);
            }
        }
        catch (InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return allErrors;
    }

    private static void distributeMethods(Annotation[] a, List<Method> methods, Method method) {
        if (a.length != 1) {
            throw new RuntimeException("Not correct amount of annotations");
        }
        methods.add(method);
    }

    public static Map<String, Object[]> quickCheckClass(String name) {
        Map<String, Object[]> allTests = new HashMap<>();

        try {
            Class<?> c = Class.forName(name);
            Method[] methods = c.getDeclaredMethods();
            Constructor<?> cons = c.getConstructor();
            Object o = cons.newInstance();
            for (Method method : methods) {
                Annotation[] a = method.getDeclaredAnnotations();
                for (Annotation value : a) {
                    if (value.toString().equals("@Property()")) {
                        Annotation[][] paramA = method.getParameterAnnotations();
                        switch (paramA[0][0].annotationType().getName()) {
                            case "IntRange" -> setupIntRange(paramA, allTests, o, method);
                            case "StringSet" -> setupStringRange(paramA, allTests, o, method);
                            case "ListLength" -> setupListRange(paramA, allTests, o, method);
                            case "ForAll" -> setupObjectRange(paramA, allTests, o, method, methods);
                        }
                    }
                }
            }
        }
        catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException |
               IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return allTests;
    }

    private static void setupListRange(Annotation[][] paramA, Map<String, Object[]> allTests, Object o, Method method) throws IllegalAccessException {
        Parameter[] params = method.getParameters();
        Annotation listA = paramA[0][0];
        Annotation a = ((AnnotatedParameterizedType) params[0].getAnnotatedType()).getAnnotatedActualTypeArguments()[0].getDeclaredAnnotations()[0];
        switch (a.annotationType().getName()) {
            case "IntRange" -> listInts(allTests, o, method, ((ListLength) listA).min(), ((ListLength) listA).max(), ((IntRange) a).min(), ((IntRange) a).max());
            case "StringSet" -> listStrings(allTests, o, method, ((ListLength) listA).min(), ((ListLength) listA).max(), ((StringSet) a).strings());
            case "ListLength" -> listLists(allTests, o, method, ((ListLength) listA).min(), ((ListLength) listA).max());
            case "ForAll" -> System.out.println("ForAll");
        }
    }

    private static void listLists(Map<String, Object[]> allTests, Object o, Method method, int listMin, int listMax) throws IllegalAccessException {
        Annotation a = ((AnnotatedParameterizedType) (method.getParameters()[0]).getAnnotatedType()).getAnnotatedActualTypeArguments()[0].getDeclaredAnnotations()[0];

//        List<Object> rangeList = new ArrayList<>(List.of(allStrings));
//        Set<List<Object>> totalTestList = findCombos(rangeList, listMin, listMax);
//        List<List<Object>> realTestList = new ArrayList<>(totalTestList);
//        checkAllPassedList(allTests, o, method, realTestList);
    }

    private static void listStrings(Map<String, Object[]> allTests, Object o, Method method, int listMin, int listMax, String[] allStrings) throws IllegalAccessException {
        List<Object> rangeList = new ArrayList<>(List.of(allStrings));
        Set<List<Object>> totalTestList = findCombos(rangeList, listMin, listMax);
        List<List<Object>> realTestList = new ArrayList<>(totalTestList);
        checkAllPassedList(allTests, o, method, realTestList);
    }

    private static void listInts(Map<String, Object[]> allTests, Object o, Method method, int listMin, int listMax, int rangeMin, int rangeMax) throws IllegalAccessException {
        List<Object> rangeList = IntStream.range(rangeMin, rangeMax + 1).boxed().collect(Collectors.toList());
        Set<List<Object>> totalTestList = findCombos(rangeList, listMin, listMax);
        List<List<Object>> realTestList = new ArrayList<>(totalTestList);
        checkAllPassedList(allTests, o, method, realTestList);
    }

    private static void checkAllPassedList(Map<String, Object[]> allTests, Object o, Method method, List<List<Object>> allRanges) throws IllegalAccessException {
        Boolean allPassed = true;
        int counter = 0;
        for (List<Object> currList : allRanges) {
            if (counter >= MAX_TESTS) {
                break;
            }
            allPassed = allPassed && runListTestMethod(allTests, o, method, currList);
            if (!allPassed) {break;}
            counter++;
        }
        if (allPassed) {
            allTests.put(method.getName(), null);
        }
    }

    private static boolean runListTestMethod(Map<String, Object[]> allTests, Object o, Method method, List<Object> testArr) throws IllegalAccessException {
        Boolean succ;
        Object[] realArr = new Object[1];
        realArr[0] = testArr;

        try {
            succ = (Boolean) method.invoke(o, testArr);
            if (!succ) {
                allTests.put(method.getName(), realArr);
                return false;
            }
        } catch (InvocationTargetException e) {
            allTests.put(method.getName(), realArr);
            return false;
        }
        return true;
    }

    private static int counter;
    public static Set<List<Object>> findCombos(List<Object> os, int minListLength, int maxListLength) {
        Set<List<Object>> combos = new HashSet<>();
        counter = 0;
        for (int i = minListLength; i <= maxListLength; i++) {
            createCombos(os, 0, new ArrayList<>(), combos, i);
            createCombosRepeat(os, new ArrayList<>(), combos, i);
        }
        return combos;
    }

    public static void createCombos(List<Object> os, int index, List<Object> currOs, Set<List<Object>> combos, int length) {
        if (currOs.size() == length) {
            counter++;
            combos.add(new ArrayList<>(currOs));
            return;
        }
        if (index == os.size() || counter > 500) {
            return;
        }

        createCombos(os, index + 1, currOs, combos, length);
        currOs.add(os.get(index));
        createCombos(os, index, currOs, combos, length);
        currOs.remove(currOs.size() - 1);
    }

    public static void createCombosRepeat(List<Object> os, List<Object> currOs, Set<List<Object>> combos, int length) {
        if (currOs.size() == length) {
            counter++;
            combos.add(new ArrayList<>(currOs));
            return;
        }
        if (counter > 500) {
            return;
        }

        for (int i = 0; i < os.size(); i++) {
            currOs.add(os.get(i));
            createCombosRepeat(os, currOs, combos, length);
            currOs.remove(currOs.size() - 1);
        }
    }

    private static void setupObjectRange(Annotation[][] paramA, Map<String, Object[]> allTests, Object o, Method method, Method[] allMethods) throws IllegalAccessException, InvocationTargetException {
        for (Annotation[] annotationRow : paramA) {
            for (Annotation annotation : annotationRow) {
                String innerMethodName = ((ForAll) annotation).name();
                Method innerMethod = null;
                for (Method m : allMethods) {
                    if (m.getName().equals(innerMethodName)) {
                        innerMethod = m;
                        break;
                    }
                }

                List<Object> allOs = new ArrayList<>();

                for (int i = 0; i < Math.min(((ForAll) annotation).times(), MAX_TESTS); i++) {
                    Object innerObject = innerMethod.invoke(o);
                    allOs.add(innerObject);
                }

                checkAllPassedObj(allTests, o, method, allOs);
            }
        }
    }

    private static void checkAllPassedObj(Map<String, Object[]> allTests, Object o, Method method, List<Object> allOs) throws IllegalAccessException {
        Boolean allPassed = true;
        for (Object currO : allOs) {
            allPassed = allPassed && runObjTestMethod(allTests, o, method, currO);
            if (!allPassed) {break;}
        }
        if (allPassed) {
            allTests.put(method.getName(), null);
        }
    }

    private static boolean runObjTestMethod(Map<String, Object[]> allTests, Object o, Method method, Object testObj) throws IllegalAccessException {
        Boolean succ;
        Object[] objArr = new Object[1];
        objArr[0] = testObj;

        try {
            succ = (Boolean) method.invoke(o, testObj);
            if (!succ) {
                allTests.put(method.getName(), objArr);
                return false;
            }
        } catch (InvocationTargetException e) {
            allTests.put(method.getName(), objArr);
            return false;
        }
        return true;
    }

    private static void setupStringRange(Annotation[][] paramA, Map<String, Object[]> allTests, Object o, Method method) throws IllegalAccessException {
        List<List<Object>> allRanges = new ArrayList<>();
        for (Annotation[] annotationRow : paramA) {
            for (Annotation annotation : annotationRow) {
                List<Object> s = Arrays.asList(((StringSet) annotation).strings());
                allRanges.add(s);
            }
        }

        List<List<Object>> allRangeCombos = getAllCombos(allRanges);
        checkAllPassed(allTests, o, method, allRangeCombos);
    }

    private static void setupIntRange(Annotation[][] paramA, Map<String, Object[]> allTests, Object o, Method method) throws IllegalAccessException {
        List<minMaxCombo> list = new ArrayList<>();
        for (Annotation[] annotationRow : paramA) {
            for (Annotation annotation : annotationRow) {
                list.add(new minMaxCombo(((IntRange) annotation).min(), ((IntRange) annotation).max()));
            }
        }

        List<List<Object>> allRanges = generateIntCombos(list);
        checkAllPassed(allTests, o, method, allRanges);
    }
    
    private static void checkAllPassed(Map<String, Object[]> allTests, Object o, Method method, List<List<Object>> allRanges) throws IllegalAccessException {
        Boolean allPassed = true;
        for (List<Object> currList : allRanges) {
            allPassed = allPassed && runTestMethod(allTests, o, method, currList);
            if (!allPassed) {break;}
        }
        if (allPassed) {
            allTests.put(method.getName(), null);
        }
    }

    private static boolean runTestMethod(Map<String, Object[]> allTests, Object o, Method method, List<Object> testArr) throws IllegalAccessException {
        Object[] realArr = testArr.toArray(new Object[0]);

        Boolean succ;
        try {
            succ = (Boolean) method.invoke(o, realArr);
            if (!succ) {
                allTests.put(method.getName(), realArr);
                return false;
            }
        } catch (InvocationTargetException e) {
            allTests.put(method.getName(), realArr);
            return false;
        }
        return true;
    }

    private static List<List<Object>> generateIntCombos(List<minMaxCombo> list) {
        List<List<Object>> allRanges = new ArrayList<>();

        for (minMaxCombo c : list) {
            List<Object> currList = IntStream.range(c.min, c.max + 1).boxed().collect(Collectors.toList());
            allRanges.add(currList);
        }

        return getAllCombos(allRanges);
    }

    public static List<List<Object>> getAllCombos(List<List<Object>> allRanges) {
        List<List<Object>> result = new ArrayList<>();
        getAllCombosHelper(allRanges, 0, new ArrayList<>(), result);
        return result;
    }

    private static void getAllCombosHelper(List<List<Object>> allRanges, int index, List<Object> currOs, List<List<Object>> result) {
        if (result.size() == MAX_TESTS) { return; }
        if (index == allRanges.size()) {
            result.add(new ArrayList<>(currOs));
            return;
        }
        List<Object> currList = allRanges.get(index);
        for (Object elem: currList) {
            currOs.add(elem);
            getAllCombosHelper(allRanges, index + 1, currOs, result);
            currOs.remove(currOs.size() - 1);
        }
    }
}