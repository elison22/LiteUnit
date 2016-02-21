package test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LiteTest implements Comparable<LiteTest> {

    Class testClass;
    Method testMethod;
    String testId;
    boolean passed;
    Throwable error;
    String timeStamp;
    boolean hasRun = false;

    LiteTest(Class testClass, Method testMethod){
        this.testClass = testClass;
        this.testMethod = testMethod;
        testId = testMethod.getAnnotation(test.annotations.LiteTest.class).reqId();
    }

    void execute(){
        try {
            execute(testClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void execute(Object testInstance){
        try{
            testMethod.invoke(testInstance);
            stamp();
            passed = true;
        } catch (InvocationTargetException e) {
            stamp();
            error = e.getTargetException();
            passed = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getTestException() {
        if(error == null)
            return "No exception";

        StringBuilder builder = new StringBuilder();
        builder.append(error.getClass().getName());
        builder.append("\n");
        if(error.getMessage() != null && !error.getMessage().equals("")) {
            builder.append(error.getMessage());
            builder.append("\n");
        }

        StackTraceElement[] stackTrace = error.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.toString().contains(testMethod.getName())) {
                builder.append(element.toString());
                builder.append("\n");
            }
        }
        builder.append(stackTrace[0].toString());

        return builder.toString();
    }

    String getTestTrace() {
        if(error == null)
            return "No trace";

        StringBuilder builder = new StringBuilder();
        builder.append(error.getClass().getName());
        builder.append("\n");
        if(error.getMessage() != null && !error.getMessage().equals("")) {
            builder.append(error.getMessage());
            builder.append("\n");
        }

        for (StackTraceElement element : error.getStackTrace()) {
            if (element.toString().contains("reflect"))
                break;
            builder.append(element.toString());
            builder.append("\n");
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    String getClassName(){
        return testClass.getName();
    }

    String getLogResult(){
        if(!hasRun)
            return "Test has not yet been executed";
        return "";
    }

    String getShortResult(){
        if(!hasRun)
            return "Test has not yet been executed";

        StringBuilder builder = new StringBuilder();
        builder.append(testMethod.getName());
        builder.append(" --> ");
        if(passed)
            builder.append(" SUCCESS");
        else
            builder.append(" FAILURE");
        return builder.toString();
    }

    @Override
    public int compareTo(LiteTest liteTest) {
        int nameDiff = this.testClass.getName().compareTo(liteTest.testClass.getName());
        if(nameDiff != 0)
            return nameDiff;
        nameDiff = this.testMethod.getName().compareTo(liteTest.testMethod.getName());
        if(nameDiff != 0)
            return nameDiff;
        nameDiff = this.testId.compareTo(liteTest.testId);
        return nameDiff;
    }

    private void stamp(){
        hasRun = true;
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    }
}
