package grading;
/*
 * Copyright (C) 2022 E Brown
 *
 * License is given to COMP2001 students and staff of Memorial University during the operation of the course.
 *
 */

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A test watcher to report the evaluation of junit tests according to @GradeValue method annotations.
 *
 * For COMP2001 classes
 *
 * @author E Brown
 * @version 2022.08.17
 */
public class GradingTestWatcher implements TestWatcher, AfterAllCallback {

    List<ReportItem> reportData = new ArrayList<ReportItem>();
    
    @Override
    public void testAborted(ExtensionContext extensionContext, Throwable throwable) {
        reportTest("ABORTED", extensionContext, throwable.getMessage());
    }

    @Override
    public void testDisabled(ExtensionContext extensionContext, Optional<String> optional) {
        reportTest("DISABLED", extensionContext, optional.orElse(""));
    }

    @Override
    public void testFailed(ExtensionContext extensionContext, Throwable throwable) {
        String extraInfo = throwable.getMessage();
        if( extraInfo == null ){
            extraInfo = "throws " + throwable.toString();
        }
        reportTest("FAIL   ", extensionContext, extraInfo);
    }

    @Override
    public void testSuccessful(ExtensionContext extensionContext) {
        reportTest("SUCCESS", extensionContext,"");
    }

    @Override
    public void afterAll(ExtensionContext context){
        int totVal = 0;
        int totalGrade = 0;
        int classValue = -1;
        String className = "this unit";

        StringBuilder sb = new StringBuilder(1000);
        for(ReportItem r: reportData ) {
            // sb.append(r.toString() + System.lineSeparator());
            System.out.println(r);
            totVal += r.value;
            totalGrade += r.grade;
        }

        /* is the class annotated? */
        Optional<Class<?>> c = context.getTestClass();
        if(c.isPresent()){
            GradeValue anno = c.get().getAnnotation(GradeValue.class); // not working
            classValue = anno != null ? anno.value() : -1;
            className = c.get().getSimpleName();
        }

        System.out.println(String.format("Total: %d/%d for these tests.", totalGrade,totVal));
        String otherTests = String.format("NOTE: %s does not indicate whether %d points is full credit",
                                            className, totVal);
        if(classValue > 0) {
            if(classValue == totVal)
                otherTests = String.format("Score for %s is %d/%d", className, totalGrade, classValue);
            if(classValue > totVal)
                otherTests = String.format("Score for %s is %d/%d. Other tests needed for an additional %d points",
                                            className, totalGrade, classValue, classValue - totVal);
            if(classValue < totVal)
                otherTests = String.format("Warning: %s value %d should not be less than tests value %d",
                                           className, classValue, totVal);
        }
        System.out.println(otherTests);
    }



    private void reportTest(String status, ExtensionContext extensionContext, String moreInfo) {
        // System.out.println(status + " " + extensionContext.getDisplayName());
        Optional<Method> m = extensionContext.getTestMethod();
        Optional<Class<?>> c = extensionContext.getTestClass();
        String testContext = c.isPresent() ? c.get().getSimpleName() : "NO CLASS NAME";
        if (m.isPresent()) {
            // System.out.println(m.get());
            Method method = m.get();
            GradeValue anno = method.getAnnotation(GradeValue.class);
            int value = anno != null ? anno.value() : 0;
            int grade = status == "SUCCESS" ? value : 0;
            // System.out.println("Annotation: " + method.getAnnotation(GradeValue.class) + value);
            // System.out.println("Grade: " + Integer.toString(grade) + "/" + Integer.toString(value));
            reportData.add(new ReportItem(testContext, extensionContext.getDisplayName(),
                    status, status == "SUCCESS",grade,value, moreInfo));
        }
        else {
            reportData.add(new ReportItem(testContext, extensionContext.getDisplayName(),
                    status, status == "SUCCESS",0,0, moreInfo));
        }
    }

    public List<ReportItem> getReportData() {
        return reportData;
    }

    public void printReportData(PrintWriter p){
        p.print(reportData);
    }
}

class ReportItem {
    String displayContext;
    String displayName;
    String status;
    boolean success;
    int grade;
    int value;
    String moreinfo;

    public ReportItem(String displayContext, String displayName, String status, boolean success, int grade, int value, String moreInfo) {
        this.displayContext = displayContext;
        this.displayName = displayName;
        this.status = status;
        this.success = success;
        this.grade = grade;
        this.value = value;
        this.moreinfo = moreInfo;
    }

    @Override
    public String toString() {
        String moreDisplay = "";
        if( moreinfo != null && !moreinfo.isEmpty() && !moreinfo.isBlank() ){
            moreDisplay = "<=== "+ moreinfo;
        }
        return String.format("%1$c %2$d/%3$d %4$s %5$s %6$s %7$s",
                success ? '+' : 'x',
                grade,
                value,
                status,
                displayContext,
                displayName,
                moreDisplay // ((moreinfo.length() > 0) ? ("<== " + moreinfo) : "")
                );
    }
}