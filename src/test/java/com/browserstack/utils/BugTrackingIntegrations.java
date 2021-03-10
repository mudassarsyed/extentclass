package com.browserstack.utils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


// WORK IN PROGRESS
public class BugTrackingIntegrations {


    // slack incoming webhooks
    public static boolean notifySlackOnFailure(String url, String OS, String Browser,String ProjName,String BuildName,String TestName){
        String basic_info = "\\nos       : "+OS       +
                "\\nbrowser  : "+Browser  +
                "\\nproject  : "+ProjName +
                "\\nbuild    : "+BuildName+
                "\\ntest     : "+TestName ;

        String user_name = "bstack-notification";
        String icon_emoji = ":x:";
        String text_blocks = " [" +
                "    {" +
                "      \"type\": \"section\"," +
                "      \"text\": {" +
                "        \"type\": \"mrkdwn\"," +
                "        \"text\": \"Test Failure!:\"" +
                "      }" +
                "    }," +
                "    {" +
                "      \"type\": \"section\"," +
                "      \"block_id\": \"section567\"," +
                "      \"text\": {" +
                "        \"type\": \"mrkdwn\"," +
                "        \"text\": \""+basic_info+"\"" +
                "      }," +
                "      \"accessory\": {" +
                "        \"type\": \"image\"," +
                "        \"image_url\": \"https://d2h1nbmw1jjnl.cloudfront.net/company_directory_entries/company_logos/000/000/328/original/bstack_2x.png?1582638320\"," +
                "        \"alt_text\": \"BrowserStack Logo\"" +
                "      }" +
                "    }, " +
                "    {" +
                "       \"type\": \"actions\"," +
                "       \"elements\": [" +
                "       {" +
                "           \"type\": \"button\"," +
                "           \"text\": {" +
                "               \"type\": \"plain_text\"," +
                "               \"text\": \"Go to Dashboard\"," +
                "               \"emoji\": true" +
                "           }," +
                "           \"value\": \"click_me_123\"," +
                "           \"url\": \""+url +"\","+
                "           \"action_id\": \"actionId-0\"" +
                "       }" +
                "     ]" +
                "   }" +
                "  ]";

        String slack_webhook_url = "https://hooks.slack.com/services/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";
        System.out.println(text_blocks);
        String payload = "payload={" +
                "\"icon_emoji\":\""+icon_emoji+ "\"," +
                "\"username\"  :\""+user_name+  "\"," +
                "\"blocks\"    :"  +text_blocks+
                "}";

        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(slack_webhook_url);
        request.setEntity(entity);

        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.getStatusLine().getStatusCode());

        return false;
    }

    public static boolean notifySlackOnAllTestCompletion(int test_passed, int test_failed, int test_skipped){

        int total_tests = test_passed + test_failed + test_skipped;
        String basic_info =
                "\\ntests failed  : "+test_failed+"/"+ total_tests+
                        "\\ntests passed  : "+test_passed+"/"+ total_tests+
                        "\\ntests unmarked: "+test_skipped+"/"+ total_tests;
        basic_info="";
        String user_name = "bstack-notification";
        String url = "http://localhost:63342/sample/My%20First%20Build.html?_ijt=sfr1uni9duvlghd3gcru7agvbi";
        String icon_emoji = ":x:";
        String text_blocks = " [" +
                "    {" +
                "      \"type\": \"section\"," +
                "      \"text\": {" +
                "        \"type\": \"mrkdwn\"," +
                "        \"text\": \"Build completed!:\"" +
                "      }" +
                "    }," +
                "    {" +
                "      \"type\": \"section\"," +
                "      \"block_id\": \"section567\"," +
                "      \"text\": {" +
                "        \"type\": \"mrkdwn\"," +
                "        \"text\": \""+basic_info+"\"" +
                "      }," +
                "      \"accessory\": {" +
                "        \"type\": \"image\"," +
                "        \"image_url\": \"https://d2h1nbmw1jjnl.cloudfront.net/company_directory_entries/company_logos/000/000/328/original/bstack_2x.png?1582638320\"," +
                "        \"alt_text\": \"BrowserStack Logo\"" +
                "      }" +
                "    }, " +
                "    {" +
                "       \"type\": \"actions\"," +
                "       \"elements\": [" +
                "       {" +
                "           \"type\": \"button\"," +
                "           \"text\": {" +
                "               \"type\": \"plain_text\"," +
                "               \"text\": \"Go to Dashboard\"," +
                "               \"emoji\": true" +
                "           }," +
                "           \"value\": \"click_me_123\"," +
                "           \"url\": \""+url +"\","+
                "           \"action_id\": \"actionId-0\"" +
                "       }" +
                "     ]" +
                "   }" +
                "  ]";

        String slack_webhook_url = "https://hooks.slack.com/services/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";
        System.out.println(text_blocks);
        String payload = "payload={" +
                "\"icon_emoji\":\""+icon_emoji+ "\"," +
                "\"username\"  :\""+user_name+  "\"," +
                "\"blocks\"    :"  +text_blocks+
                "}";

        StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_FORM_URLENCODED);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(slack_webhook_url);
        request.setEntity(entity);

        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.getStatusLine().getStatusCode());

        return false;
    }


    public static  String readFile(String file_name){
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file_name));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
        return contentBuilder.toString();
    }

    // from https://jaxenter.com/java-app-emails-smtp-server-164144.html

    // WORK IN PROGRESS
    public static void notifyGmailOnAllTestCompletion(int test_passed, int test_failed, int test_skipped){
        //Get properties object


        int total_tests = test_passed+test_failed+test_skipped;


        String msg = readFile("src/test/resources/templates/email_template.html");
        msg = msg.replace("#TEST_PASSED", String.valueOf(test_passed));
        msg = msg.replace("#TEST_FAILED", String.valueOf(test_failed));
        msg = msg.replace("#TEST_TOTAL", String.valueOf(total_tests));
        final String from="build.update.madhav@gmail.com";
        final String password="madhavwagle1";
        String to="mudassar@browserstack.com";
        String sub="TestNG mail integration test";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        //get Session
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from,password);
                    }
                });
        //compose message

        String msg1 = readFile("My First Build-failed-tests.html");


        try {
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
            message.setSubject(sub);
            message.setText(msg);
            message.setContent(msg,"text/html");

            //send message
            Transport.send(message);
            System.out.println("message sent successfully");
        } catch (MessagingException e) {throw new RuntimeException(e);}

    }


}
