package test;

import test.annotations.LiteClass;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by brandt on 2/20/16.
 */
public class LiteTarget {

    private String fullSlashPath;
    private String localDotPath;
    private boolean recurse;
    private boolean requireClassAnnotation;
    private char slash = File.separatorChar;

    LiteTarget(String fullSlashPath, String localDotPath, boolean recurse, boolean requireClassAnnotation) {
        this.fullSlashPath = fullSlashPath;
        this.localDotPath = localDotPath;
        this.recurse = recurse;
        this.requireClassAnnotation = requireClassAnnotation;
    }

    ArrayList<LiteTest> makeTestList(){

        ArrayList<LiteTest> discoveredTests = new ArrayList<>();
        scan(fullSlashPath, localDotPath, discoveredTests);
        return discoveredTests;

    }

    private void scan(String slashPath, String dotPath, ArrayList<LiteTest> discoveredTests){

        try {
            File currentFile = new File(slashPath);

            if(currentFile.isDirectory()) {
                File[] contents = currentFile.listFiles();
                for (File dirMember : contents) {
                    String newSlashPath = slashPath + slash + dirMember.getName();
                    String newDotPath = dotPath + "." + dirMember.getName().replaceFirst("[.][^.]+$", "");
                    if(dirMember.isDirectory() && !recurse)
                        continue;
                    scan(newSlashPath, newDotPath, discoveredTests);
                }
            }
            else if(currentFile.isFile() && isJava(currentFile)){
                Class discoveredClass = Class.forName(dotPath);
                if (isTest(discoveredClass))
                    scanClass(discoveredClass, discoveredTests);
            }
            else if(!isJava(currentFile))
                scan(slashPath + ".java", dotPath, discoveredTests);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scanClass(Class toScan, ArrayList<LiteTest> discoveredTests){

        for (Method method : toScan.getDeclaredMethods()) {
            if (isTest(method))
                discoveredTests.add(new LiteTest(toScan, method));
        }
    }


    private boolean isJava(File toCheck){
        return !toCheck.getName().contains("Lite") &&
                toCheck.getName().contains(".java");
    }

    private boolean isTest(Class toCheck) {
        return !requireClassAnnotation ||
                toCheck.isAnnotationPresent(LiteClass.class);
    }

    private boolean isTest(Method toCheck) {
        return Modifier.isPublic(toCheck.getModifiers()) &&
                toCheck.getParameterTypes().length == 0 &&
                toCheck.isAnnotationPresent(test.annotations.LiteTest.class);
    }


}
