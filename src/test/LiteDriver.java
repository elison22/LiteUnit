package test;

import java.io.File;
import java.util.TreeSet;

/**
 * I simple unit test driver. To use LiteDriver, you must first annotate classes
 * and methods with the LiteClass and LiteTest annotations respectively. LiteDriver
 * will use these annotations to find tests to be queued and run.
 * <br>
 * The basic steps to running tests are as follows:
 * <ol>
 *     <li>Write tests and annotate the tests and their classes with @LiteTest and @LiteClass</li>
 *     <li>Queue all the tests that you would like to run for that particular test run</li>
 *     <li>Call executeQueuedTests() to allow the TestDriver to run the tests</li>
 *     <li>Call a print method to see the results of the tests</li>
 *     <li>Call reset() to prep the TestDriver for a new set of tests</li>
 * </ol>
 * <b>NOTE: </b> The runTests() method may be invoked to automate these steps for a single package or class.
 */
public class LiteDriver {

    private String fullSrcPath;    // doesn't remove a leading slash
    private String localTestRoot;
    /**
     * false is the default for whether or not to scan packages recursively.
     */
    public final boolean RECURSE_DEFAULT = false;
    /**
     * false is the default for whether or not to print the full stack trace in tests results
     */
    public final boolean FULL_TRACE_DEFAULT = false;
    /**
     * false is the default for whether or not to print scan classes that are not marked with the @LiteClass annotation
     */
    public final boolean SCAN_NON_TEST_CLASS_DEFAULT = false;
    /**
     * "" is the default for the target starting at the testing root that was set up in the constructor originally
     */
    public final String TARGET_DOT_NAME_DEFAULT = "";
    private char slash = File.separatorChar;
    private TreeSet<LiteTest> queuedTests;
    private boolean hasExecuted = false;

    /**
     * See LiteDriver(String, String)
     */
    public LiteDriver(String pathToSrc) {
        this(pathToSrc, "");
    }

    /**
     * Sets up the test driver to scan files for tests. Because a project's
     * content root cannot be identified at runtime, the user must supply
     * the path (dot or slash separated) to the base directory for all .java files.
     * In many instances this will be simply "src", but if you have configured more
     * that one content root or developing in android, it could be something like
     * "app.src.main.java" where java is the beginning of your code package structure.
     * <br>
     * You may optionally include another dot path beginning with the first folder after
     * the content root that indicates the root of all of the test classes. This will
     * be added as a prefix to all package or class names passed to queueTests() and runTests().
     * @param pathToSrc The dot separated or slash separated path starting after the base directory
     *                  of your project and leading to the content root.
     * @param localTestRoot The dot separated path to the root package of your tests. This defaults
     *                      to TARGET_DOT_NAME_DEFAULT
     */
    public LiteDriver(String pathToSrc, String localTestRoot) {
        String projectRoot = cleanPath(System.getProperty("user.dir"), slash, false);
        String srcPath = cleanPath(pathToSrc, slash, true);
        this.localTestRoot = cleanPath(localTestRoot, slash, true);
        this.fullSrcPath = projectRoot + slash + srcPath;
        this.queuedTests = new TreeSet<>();

        System.out.println("New driver src path: " + this.fullSrcPath);
        System.out.println("New driver test root: " + cleanPath(this.localTestRoot, '.', true));
    }


    // ===== FIND, QUEUE, AND RUN TESTS
    // ================================

    // Overloads for run tests

    public void runTests(){
        runTests(TARGET_DOT_NAME_DEFAULT, RECURSE_DEFAULT, FULL_TRACE_DEFAULT, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    /**
     * See runTests(String, boolean, boolean, boolean)
     */
    public void runTests(String targetDotName){
        runTests(targetDotName, RECURSE_DEFAULT, FULL_TRACE_DEFAULT, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    /**
     * See runTests(String, boolean, boolean, boolean)
     */
    public void runTests(String targetDotName, boolean recurse){
        runTests(targetDotName, recurse, FULL_TRACE_DEFAULT, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    /**
     * See runTests(String, boolean, boolean, boolean)
     */
    public void runTests(String targetDotName, boolean recurse, boolean fullTrace){
        runTests(targetDotName, recurse, fullTrace, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    /**
     * See runTests(String, boolean, boolean, boolean)
     */
    public void runTests(boolean recurse) {
        runTests(TARGET_DOT_NAME_DEFAULT, recurse, FULL_TRACE_DEFAULT, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    /**
     * See runTests(String, boolean, boolean, boolean)
     */
    public void runTests(boolean recurse, boolean fullTrace) {
        runTests(TARGET_DOT_NAME_DEFAULT, recurse, fullTrace, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    /**
     * See runTests(String, boolean, boolean, boolean)
     */
    public void runTests(boolean recurse, boolean fullTrace, boolean scanNonTestClasses) {
        runTests(TARGET_DOT_NAME_DEFAULT, recurse, fullTrace, scanNonTestClasses);
    }

    /**
     * Executes the following functions serially:
     * <ul>
     *     <li>queueTests(String, boolean, boolean)</li>
     *     <li>executeQueuedTests()</li>
     *     <li>prettyPrint(boolean)</li>
     * </ul>
     * The default parameters for the class are used unless overridden by a parameter of this
     * or any other overload of runTests().
     * @param targetDotName The dot-separated name of the package or class to be scanned.
     *                      If the TestDriver was constructed with a localTestRoot parameter,
     *                      that localTestRoot path will automatically be prefixed by targetDotName.
     *                      <br>
     *                      queueTests() will attempt to fix format errors in the targetDotName.
     * @param recurse Set to true if you would like to queue tests in sub packages of targetDotName.
     * @param fullTrace Set to true in order to see the full stack trace for each error.
     * @param scanNonTestClasses Set to true if you would like to queue tests discovered in any class
     *                           even if it does not have the @LiteClass annotation. This is useful if
     *                           want to add tests to a class without making a new class to house the tests.
     */
    public void runTests(String targetDotName, boolean recurse, boolean fullTrace, boolean scanNonTestClasses){
        queueTests(targetDotName, recurse, scanNonTestClasses);
        executeQueuedTests();
        prettyPrint(fullTrace);
        reset();
    }


    // Overloads for queue tests

    /**
     * See queueTests(String, boolean, boolean)
     */
    public void queueTests(){
        queueTests(TARGET_DOT_NAME_DEFAULT, RECURSE_DEFAULT, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    /**
     * See queueTests(String, boolean, boolean)
     */
    public void queueTests(String targetDotName){
        queueTests(targetDotName, RECURSE_DEFAULT, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    /**
     * See queueTests(String, boolean, boolean)
     */
    public void queueTests(String targetDotName, boolean recurse) {
        queueTests(targetDotName, recurse, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    /**
     * See queueTests(String, boolean, boolean)
     */
    public void queueTests(boolean recurse) {
        queueTests(TARGET_DOT_NAME_DEFAULT, recurse, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    /**
     * See queueTests(String, boolean, boolean)
     */
    public void queueTests(boolean recurse, boolean scanNonTestClasses) {
        queueTests(TARGET_DOT_NAME_DEFAULT,recurse,scanNonTestClasses);
    }

    /**
     * Scans classes in specified package or the specific class specified
     * for methods that have the following conditions:
     * <ul>
     *     <li>are marked with @LiteTest</li>
     *     <li>are public</li>
     *     <li>return void</li>
     *     <li>have no parameters</li>
     * </ul>
     * Classes with 'Lite' anywhere in their name are ignored.
     * <br>
     * <b>NOTE: </b> Default parameters for the class will be used if this function is
     * invoked using an overload that does not supply all of the parameters.
     *
     * @param targetDotName The dot-separated name of the package or class to be scanned.
     *                      If the TestDriver was constructed with a localTestRoot parameter,
     *                      that localTestRoot path will automatically be prefixed by targetDotName.
     *                      <br>
     *                      The method will attempt to fix format errors in the targetDotName.
     *
     * @param recurse Set to true if you would like to queue tests in sub packages of targetDotName.
     *
     * @param scanNonTestClasses Set to true if you would like to queue tests discovered in any class
     *                           even if it does not have the @LiteClass annotation. This is useful if
     *                           want to add tests to a class without making a new class to house the tests.
     */
    public void queueTests(String targetDotName, boolean recurse, boolean scanNonTestClasses) {

        if(hasExecuted)
            System.out.println("!!TEST DRIVER ERROR!!\n*You must reset the TestDriver using reset() before you can queue or run a new batch of tests.");
        String fullTargetDotName = cleanPath(targetDotName, '.', true);
        if(localTestRoot != null && !localTestRoot.equals(""))
            fullTargetDotName = cleanPath(localTestRoot + "." + fullTargetDotName, '.', true);

        String targetFullSlashName = fullSrcPath + slash + cleanPath(fullTargetDotName, slash, true);

        LiteTarget testTarget = new LiteTarget(targetFullSlashName, fullTargetDotName, recurse, !scanNonTestClasses);
        int diff = queuedTests.size();
        queuedTests.addAll(testTarget.makeTestList());
        diff = queuedTests.size() - diff;
        System.out.println("Scanning " + (fullTargetDotName.equals("") ? "" : (fullTargetDotName + " ")) + "found " + diff + " new tests.");
    }


    // === TEST EXECUTION LIFECYCLE
    // ============================

    /**
     * Execute the tests that have been queued using the queueTests() method or one of its overloads.
     * Nothing will be printed when this method is invoked. It runs the tests, then
     * organizes the results behind the scenes for analysis and printing later on.
     *
     * If you want to see the results of the tests, use a print function such as prettyPrint()
     * or one of its overloads.
     */
    public void executeQueuedTests(){
        if(hasExecuted)
            System.out.println("!!TEST DRIVER ERROR!!\n*You must reset the TestDriver using reset() before you can queue or run a new batch of tests.");
        for(LiteTest test : queuedTests)
            test.execute();
        hasExecuted = true;
    }

    /**
     * Nicely prints out the results of executeQueuedTests(). Currently this method
     * displays a shortened version of the errors. Use prettyPrint(true) to see more
     * of the stack trace.
     */
    public void prettyPrint(){
        prettyPrint(FULL_TRACE_DEFAULT);
    }
    /**
     * Same as prettyPrint() but with the option to see the stack trace.
     * @param fullTrace Set to true in order to see the full stack trace for each error.
     */
    public void prettyPrint(boolean fullTrace){
        if(!hasExecuted) {
            System.out.println("Tests have not yet been executed...");
            return;
        }
        System.out.println("\n====================\n=== Test Results ===\n====================");
        TreeSet<LiteTest> failures = new TreeSet<>();
//        String prevClassName = "*blah*";

        // print the test results at a high level
        for(LiteTest test : queuedTests){

            String thisClassName = test.getClassName();
            thisClassName = thisClassName.replace(cleanPath(localTestRoot, '.', true) + ".", "");
            /*
            if(!prevClassName.equals(test.getClassName())){
                System.out.println("\n>> " + thisClassName + " <<");
                prevClassName = thisClassName;
            }*/
            System.out.print(thisClassName + ".");
            System.out.println(test.getShortResult());
            if(!test.passed)
                failures.add(test);
        }

        System.out.println("\n===========================\n=== Errors for Failures ===\n===========================");

        for(LiteTest fail : failures){
            String thisClassName = fail.getClassName();
            thisClassName = thisClassName.replace(cleanPath(localTestRoot, '.', true) + ".", "");
            System.out.println("\n>> " + thisClassName + "." + fail.testMethod.getName() + " <<");
            if(fullTrace)
                System.out.println(fail.getTestTrace());
            else
                System.out.println(fail.getTestException());
        }

        if(failures.size() == 0)
            System.out.println("\n ==== SUCCESS!! ==== ");
        else System.out.println("\n ==== FAILURE... ==== ");
        System.out.println("Tests Run: " + queuedTests.size());
        System.out.println("Tests Failed: " + failures.size());

    }

    /**
     * Reset in preparation for new tests.
     */
    public void reset(){
        hasExecuted = false;
        queuedTests.clear();
    }


    // === HELPERS
    // ===========
    private String trim(String stringToTrim, char charToRemove, boolean trimFront){

        if(trimFront) {
            while (stringToTrim.startsWith(String.valueOf(charToRemove))) {
                if (stringToTrim.length() == 1)
                    return "";
                else
                    stringToTrim = stringToTrim.substring(1);
            }
        }

        while(stringToTrim.endsWith(String.valueOf(charToRemove))) {
            if(stringToTrim.length() == 1)
                return "";
            else
                stringToTrim = stringToTrim.substring(0,stringToTrim.length()-1);
        }
        return stringToTrim;
    }
    private String cleanPath(String stringToClean, char replaceChar, boolean trimFront){
        stringToClean = stringToClean.replace('.', replaceChar);
        stringToClean = stringToClean.replace(slash, replaceChar);
        stringToClean = trim(stringToClean, replaceChar, trimFront);
        return stringToClean;
    }



    /*
    // ===== GRAVEYARD =====
    // =====================

    public void setScanNonTestClassesDefault(boolean scan){
        SCAN_NON_TEST_CLASS_DEFAULT = scan;
    }
    public void setShowFullTraceDefualt(boolean show){
        FULL_TRACE_DEFAULT = show;
    }
    public void setRecurseDefault(boolean recurse){
        RECURSE_DEFAULT = recurse;
    }


    @Deprecated
    public void runQuickTest(String targetDotName){
        runQuickTest(targetDotName, RECURSE_DEFAULT, FULL_TRACE_DEFAULT, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    @Deprecated
    public void runQuickTest(String targetDotName, boolean recurse){
        runQuickTest(targetDotName, recurse, FULL_TRACE_DEFAULT, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    @Deprecated
    public void runQuickTest(String targetDotName, boolean recurse, boolean fullTrace){
        runQuickTest(targetDotName, recurse, fullTrace, SCAN_NON_TEST_CLASS_DEFAULT);
    }
    @Deprecated
    public void runQuickTest(String targetDotName, boolean recurse, boolean fullTrace, boolean scanNonTestClassed) {
        targetDotName = cleanPath(targetDotName, '.', true);
        String packagePath = fullSrcPath + slash + cleanPath(targetDotName, slash, true);
        LiteQuickTest test = new LiteQuickTest(packagePath, targetDotName, recurse, fullTrace, !scanNonTestClassed);
        test.runTests();
        test.printResult();
    }*/


}
