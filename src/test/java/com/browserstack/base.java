package com.browserstack;

import com.browserstack.local.Local;
import com.browserstack.utils.BugTrackingIntegrations;
import java.io.FileReader;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;



public class base {
    public WebDriver driver;
    private Local l;
    String username,accessKey;


    @BeforeMethod(alwaysRun = true)
    @org.testng.annotations.Parameters(value = { "config", "environment" })
    @SuppressWarnings("unchecked")
    public void setUp(String config_file, String environment) throws Exception {
        com.browserstack.utils.ExtentReportListner.onTestStart("My First Build");

        JSONParser parser = new JSONParser();
        JSONObject config = (JSONObject) parser.parse(new FileReader("src/test/resources/conf/" + config_file));
        JSONObject envs = (JSONObject) config.get("environments");

        DesiredCapabilities capabilities = new DesiredCapabilities();

        Map<String, String> envCapabilities = (Map<String, String>) envs.get(environment);
        Iterator it = envCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
        }

        Map<String, String> commonCapabilities = (Map<String, String>) config.get("capabilities");
        it = commonCapabilities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (capabilities.getCapability(pair.getKey().toString()) == null) {
                capabilities.setCapability(pair.getKey().toString(), pair.getValue().toString());
            }
        }

        username = System.getenv("BROWSERSTACK_USERNAME");
        if (username == null) {
            username = (String) config.get("user");
        }

        accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
        if (accessKey == null) {
            accessKey = (String) config.get("key");
        }

        if (capabilities.getCapability("browserstack.local") != null
                && capabilities.getCapability("browserstack.local") == "true") {
            l = new Local();
            Map<String, String> options = new HashMap<String, String>();
            options.put("key", accessKey);
            l.start(options);
        }


        driver = new RemoteWebDriver(
                new URL("http://" + username + ":" + accessKey + "@" + config.get("server") + "/wd/hub"), capabilities);

    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result, ITestContext context) throws Exception {

        SessionId sessionid = ((RemoteWebDriver) driver).getSessionId();
        driver.quit();

        context.setAttribute("i",sessionid +"\n"+(String)context.getAttribute("i"));

        com.browserstack.utils.BrowserStackAPI bsApi= new com.browserstack.utils.BrowserStackAPI(username,accessKey);
        com.browserstack.utils.ExtentReportListner reporter= new com.browserstack.utils.ExtentReportListner();

        String session_id=sessionid.toString();
        String test_status;
        String test_reason;

        // mark test as passsed or failed with reason
        if (result.getStatus() == ITestResult.FAILURE) {
            test_status = "FAILED";
            test_reason = "Fail";
        }
        else{
            test_status = "PASSED";
            test_reason = "Pass";
        }

        reporter.updateAllInfo(session_id,bsApi,test_status,test_reason);

        // calls the extent.flush() method
        com.browserstack.utils.ExtentReportListner.onFinish();

        if (l != null) {
            l.stop();
        }
    }

    // TBD yet
    @AfterSuite
    public void after_suite(ITestContext context)
    {
        int total_passed = context.getAttribute("failed_tests")==null? 0: (int) context.getAttribute("failed_tests");
        int total_failed = context.getAttribute("passed_tests")==null? 0:(int) context.getAttribute("passed_tests");
        int total_skipped = 0;
        //BugTrackingIntegrations.notifySlackOnAllTestCompletion(total_passed, total_failed, total_skipped);



        Collection<ISuiteResult> suiteResults = context.getSuite().getResults().values();
        total_passed=0;
        total_failed=0;
        total_skipped=0;
        for (ISuiteResult suiteResult : suiteResults) {
            for (ITestResult result : suiteResult.getTestContext().getPassedTests().getAllResults()) {
                total_passed += 1;
            }
        }
        for (ISuiteResult suiteResult : suiteResults) {
            for (ITestResult result : suiteResult.getTestContext().getFailedTests().getAllResults()) {
                total_failed+=1;
            }
        }


        System.out.println((String)context.getAttribute("i"));

        BugTrackingIntegrations.notifyGmailOnAllTestCompletion(total_passed, total_failed, total_skipped);
        //BugTrackingIntegrations.notifySlackOnFailure("","","","","","");
        //System.out.println("Last method");

    }



}
