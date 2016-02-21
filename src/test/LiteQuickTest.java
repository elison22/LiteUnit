package test;

import test.annotations.LiteClass;
import test.annotations.LiteTest;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * Created by brandt on 2/20/16.
 */
@Deprecated
public class LiteQuickTest {

    private boolean fulltrace;
    private boolean recurse;
    private boolean require_class;
    private boolean debug = false;
    private String fullSlashPath;
    private String localDotPath;
    private char slash = File.separatorChar;
    private int testsRun = 0;
    private int testsFailed = 0;

    LiteQuickTest(String fullSlashPath, String localDotPath, boolean recurse, boolean fulltrace, boolean require_class) {
        this.fullSlashPath = fullSlashPath;
        this.localDotPath = localDotPath;
        this.recurse = recurse;
        this.fulltrace = fulltrace;
        this.require_class = require_class;
    }

    //===================== FOR QUICK RUNS

    void runTests(){

        if(debug) {
            System.out.println("Searching for tests on this dir: " + fullSlashPath);
            System.out.println("Searching for tests with this dot-name: " + localDotPath);
        }

        scanAndRun(fullSlashPath, localDotPath);
    }

    void printResult(){
        if(testsFailed == 0)
            System.out.println("\n ==== SUCCESS!! ==== ");
        else System.out.println("\n ==== FAILURE... ==== ");
        System.out.println("Tests Run: " + testsRun);
        System.out.println("Tests Failed: " + testsFailed);
    }


    private void scanAndRun(String slashPath, String dotPath) {

        try {

            File currentFile = new File(slashPath);

            if(currentFile.isDirectory()) {
                File[] contents = currentFile.listFiles();
                for (File dirMember : contents) {
                    String newSlashPath = slashPath + slash + dirMember.getName();
                    String newDotPath = dotPath + "." + dirMember.getName().replaceFirst("[.][^.]+$", "");
                    if(dirMember.isDirectory() && !recurse)
                        continue;
                    scanAndRun(newSlashPath, newDotPath);
                }
            }
            else if(currentFile.isFile() && isJava(currentFile)){
                Class discovered = Class.forName(dotPath);
                if (!isTest(discovered))
                    return;
                executeMethods(discovered);
            }
            else if(!isJava(currentFile))
                scanAndRun(slashPath + ".java", dotPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void executeMethods(Class testClass) {
        System.out.println("\n---- Results for " + testClass.getSimpleName() + " ----");
        HashMap<Method, Throwable> failures = new HashMap<>();

        for (Method method : testClass.getDeclaredMethods()) {
            try {
                if(!isTest(method))
                    continue;
                testsRun++;
                method.invoke(testClass.newInstance());
                System.out.println("-- " + method.getName() + " --> Passed");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                Throwable te = e.getTargetException();
                failures.put(method, te);

                System.out.println("-- " + method.getName() + " --> Failed");
                testsFailed++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(Method method : failures.keySet()){
            Throwable te = failures.get(method);
            System.out.println("\n-- Error for " + method.getName());
            System.out.println(te.toString());
            if(fulltrace)
                System.out.println(getTestTrace(
                        te.getStackTrace()));
            else {
                System.out.println(getTestException(
                        te.getStackTrace(),
                        method.getName()));
            }
        }
    }

    private String getTestException(StackTraceElement[] stackTrace, String methodName) {

        for (StackTraceElement element : stackTrace) {
            if (element.toString().contains(methodName))
                return element.toString();
        }
        return stackTrace[0].toString();
    }

    private String getTestTrace(StackTraceElement[] stackTrace) {

        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            if (element.toString().contains("reflect"))
                break;
            builder.insert(0, element.toString() + "\n");
        }
        String toReturn = builder.toString();
        return toReturn.substring(0, toReturn.length() - 1);
    }


    private boolean isJava(File toCheck){

        if (toCheck.getName().contains("Lite"))
            return false;
        if (!toCheck.getName().contains(".java"))
            return false;
        return true;
    }

    private boolean isTest(Class toCheck) {
        if(!require_class)
            return true;
        if(debug) {
            System.out.println("Checking for class annotation");
            if(toCheck.isAnnotationPresent(LiteClass.class))
                System.out.println("Class annotation found");
            else
                System.out.println("Class annotation not found");
        }
        return toCheck.isAnnotationPresent(LiteClass.class);
    }

    private boolean isTest(Method toCheck) {
        if(!Modifier.isPublic(toCheck.getModifiers()))
            return false;
        if(debug) {
            System.out.println("Checking for method annotation");
            if(toCheck.isAnnotationPresent(LiteTest.class))
                System.out.println("Method annotation found");
            else
                System.out.println("Method annotation not found");
        }
        return toCheck.isAnnotationPresent(LiteTest.class);
    }


}
