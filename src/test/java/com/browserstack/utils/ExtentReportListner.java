package com.browserstack.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;


public class ExtentReportListner  {

    // initialize the HtmlReporter
    public static ExtentHtmlReporter reports;
    // create ExtentReports and attach reporter(s)
    public static ExtentReports extent;
    // creates a toggle for the given test, adds all log events under it
    public static ExtentTest test;
    // initialize the HtmlReporter
    public static ExtentHtmlReporter reports1;
    // create ExtentReports and attach reporter(s)
    public static ExtentReports extent1;
    // creates a toggle for the given test, adds all log events under it
    public static ExtentTest test1;


    String all_details = "";
    static int total_passed = 0;
    static int total_failed = 0;
    static int total_skipped = 0;


    // In this file two reports are being generated
    // 1. reports (failed and passed tests)
    // 2. reports1 (only failed tests)
    public static void onTestStart(String reportName) {

        reports=new ExtentHtmlReporter("./"+reportName+".html");
        reports.config().setReportName("BrowserStackReport");

        extent= new ExtentReports();
        extent.attachReporter(reports);

        reports1=new ExtentHtmlReporter("./"+reportName+"-failed-tests.html");
        reports1.config().setReportName("BrowserStackReport-failed tests");

        extent1= new ExtentReports();
        extent1.attachReporter(reports1);

    }

    public void onTestPass(String ProjName, String BuildName,String sessionName, String os, String browser) {

        // os logo file Eg.Mac logo, Windows logo
        // TBD: Mobile logos
        String os_icon = "src/test/resources/icons/os_icons/"+os+".png";
        String os_icon_html_tag ="<img src=\""+os_icon+"\" alt=\""+browser+"\" width=\"35\" height=\"35\">";
        String browser_icon = "src/test/resources/icons/browser_icons/"+browser+".png";
        String browser_icon_html_tag ="<img src=\""+browser_icon+"\" alt=\""+browser+"\" width=\"35\" height=\"35\">";
        test=extent.createTest( os_icon_html_tag+browser_icon_html_tag+BuildName+"->"+sessionName);
        test.pass("<b>"+sessionName +"</b> is marked PASSED<br>"+all_details);
        total_passed++;

    }

    public void onTestFailure(String ProjName, String BuildName,String sessionName, String os, String browser) {

        // os logo file Eg.Mac logo, Windows logo
        // TBD: Mobile logos
        String os_icon = "src/test/resources/icons/os_icons/"+os+".png";
        String os_icon_html_tag ="<img src=\""+os_icon+"\" alt=\""+browser+"\" width=\"35\" height=\"35\">";
        String browser_icon = "src/test/resources/icons/browser_icons/"+browser+".png";
        String browser_icon_html_tag ="<img src=\""+browser_icon+"\" alt=\""+browser+"\" width=\"35\" height=\"35\">";

        test=extent.createTest( os_icon_html_tag+browser_icon_html_tag+BuildName+"->"+sessionName);
        test.fail("<b>"+sessionName +"</b> is marked FAILED<br>"+all_details);
        total_failed++;

        test1=extent1.createTest( BuildName+"->"+sessionName);
        test1.fail("<b>"+sessionName +"</b> is marked FAILED<br>"+all_details);
    }

    public void onTestUnmarked(String ProjName, String BuildName,String sessionName, String os, String browser) {
        String os_icon = "src/test/resources/icons/os_icons/"+os+".png";
        String os_icon_html_tag ="<img src=\""+os_icon+"\" alt=\""+browser+"\" width=\"35\" height=\"35\">";
        String browser_icon = "src/test/resources/icons/browser_icons/"+browser+".png";
        String browser_icon_html_tag ="<img src=\""+browser_icon+"\" alt=\""+browser+"\" width=\"35\" height=\"35\">";

        test=extent.createTest( os_icon_html_tag+browser_icon_html_tag+BuildName+"->"+sessionName);
        test.log(Status.SKIP, "<b>"+sessionName+"</b> is marked UNMARKED<br>"+all_details);
        total_skipped++;

    }


    // Updates all the fields in the test report

    public void updateAllInfo(String session_id, BrowserStackAPI bsApi, String test_status, String test_reason) throws IOException, URISyntaxException {
        String[] fields = {"os","os_version","browser_version","browser","status","build_name","project_name","browser_url","public_url","video_url"};

        bsApi.markTestStatus(session_id, test_status, test_reason);

        String sessionName=bsApi.getValue(bsApi.getSessionDetails(session_id), "name");
        String ProjName=bsApi.getValue(bsApi.getSessionDetails(session_id), "project_name");
        String BuildName=bsApi.getValue(bsApi.getSessionDetails(session_id), "build_name");
        String os=bsApi.getValue(bsApi.getSessionDetails(session_id), "os").toLowerCase();
        String browser=bsApi.getValue(bsApi.getSessionDetails(session_id), "browser").toLowerCase();
        for (String field: fields){

            // handles the video logs link
            if(field.contains("video")){
                all_details+="<br><video width=\"400\" controls><source src="+bsApi.getValue(bsApi.getSessionDetails(session_id), field)+"></video>";
            }
            // generates links in html for the other log urls
            else if(field.contains("url")){

                all_details+="<b>"+field+"</b>: <a href="+bsApi.getValue(bsApi.getSessionDetails(session_id), field)+">link to logs</a><br>";
            }
            // all other feilds
            else{
                all_details+="<b>"+field+"</b>: "+bsApi.getValue(bsApi.getSessionDetails(session_id), field)+"<br>";
            }

        }

        // failed
        if(test_status=="FAILED" || test_status=="failed"){
            onTestFailure(ProjName,BuildName,sessionName,os,browser);
        } // passed
        else if(test_status=="PASSED" || test_status=="passed"){
            onTestPass(ProjName,BuildName,sessionName,os,browser);
        } // unmarked
        else{
            onTestUnmarked(ProjName,BuildName,sessionName,os,browser);
        }

    }

    public static void onFinish() {

        extent.flush();
        extent1.flush();

    }



}
