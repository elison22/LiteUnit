package test;

import java.io.File;
import java.util.TreeSet;

public class LiteDriver {

    private String fullSrcPath;    // doesn't remove a leading slash
    private String localTestRoot;
    private boolean recurseDefault = false;
    private boolean fullTraceDefault = false;
    private boolean scanNonTestClassedDefault = true;
    private char slash = File.separatorChar;
    private TreeSet<LiteTest> queuedTests;
    private boolean hasExecuted = false;

    public LiteDriver(String pathToSrc) {
        this(pathToSrc, "");
    }
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
    public void runTests(String targetDotName){
        runTests(targetDotName, recurseDefault, fullTraceDefault, scanNonTestClassedDefault);
    }
    public void runTests(String targetDotName, boolean recurse){
        runTests(targetDotName, recurse, fullTraceDefault, scanNonTestClassedDefault);
    }
    public void runTests(String targetDotName, boolean recurse, boolean fullTrace){
        runTests(targetDotName, recurse, fullTrace, scanNonTestClassedDefault);
    }
    public void runTests(String targetDotName, boolean recurse, boolean fullTrace, boolean scanNonTestClasses){
        queueTests(targetDotName, recurse, scanNonTestClasses);
        executeQueuedTests();
        prettyPrint(fullTrace);
        reset();
    }

    public void queueTests(String targetDotName){
        queueTests(targetDotName, recurseDefault, scanNonTestClassedDefault);
    }
    public void queueTests(String targetDotName, boolean recurse) {
        queueTests(targetDotName, recurse, scanNonTestClassedDefault);
    }
    public void queueTests(String targetDotName, boolean recurse, boolean scanNonTestClasses) {

        String fullTargetDotName = cleanPath(targetDotName, '.', true);
        if(localTestRoot != null && !localTestRoot.equals(""))
            fullTargetDotName = cleanPath(localTestRoot + "." + fullTargetDotName, '.', true);

        String targetFullSlashName = fullSrcPath + slash + cleanPath(fullTargetDotName, slash, true);

        LiteTarget testTarget = new LiteTarget(targetFullSlashName, fullTargetDotName, recurse, !scanNonTestClasses);
        int diff = queuedTests.size();
        queuedTests.addAll(testTarget.makeTestList());
        diff = queuedTests.size() - diff;
        System.out.println("Scanning " + targetDotName + " found " + diff + " new tests.");

    }


    // === TEST EXECUTION LIFECYCLE
    // ============================
    public void executeQueuedTests(){
        for(LiteTest test : queuedTests)
            test.execute();
        hasExecuted = true;
    }

    public void prettyPrint(){
        prettyPrint(fullTraceDefault);
    }
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

    public void reset(){
        hasExecuted = false;
        queuedTests.clear();
    }


    // === SETTING DEFAULTS
    // ====================
    public void setCcanNonTestClassesDefault(boolean scan){
        scanNonTestClassedDefault = scan;
    }
    public void setShowFullTraceDefualt(boolean show){
        fullTraceDefault = show;
    }
    public void setRecurseDefault(boolean recurse){
        recurseDefault = recurse;
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
    @Deprecated
    public void runQuickTest(String targetDotName){
        runQuickTest(targetDotName, recurseDefault, fullTraceDefault, scanNonTestClassedDefault);
    }
    @Deprecated
    public void runQuickTest(String targetDotName, boolean recurse){
        runQuickTest(targetDotName, recurse, fullTraceDefault, scanNonTestClassedDefault);
    }
    @Deprecated
    public void runQuickTest(String targetDotName, boolean recurse, boolean fullTrace){
        runQuickTest(targetDotName, recurse, fullTrace, scanNonTestClassedDefault);
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
