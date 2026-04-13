package com.iict.buet.customer_portal.service.impl;

import com.iict.buet.customer_portal.util.HttpUrlConnectionUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("smsSender")
public class SmsSender {
    private static final Logger logger = LogManager.getLogger(SmsSender.class.getName());

    public Boolean sendSms(String mobileNo, String message) throws IOException {
        String url = "http://sms.sslwireless.com/pushapi/dynamic/server.php?msisdn=" + URLEncoder.encode(mobileNo, "UTF-8") + "&sms=" + URLEncoder.encode(message, "UTF-8") + "&user=" + URLEncoder.encode("kgdclapi", "UTF-8") + "&pass=" + URLEncoder.encode("A1r3i5f7", "UTF-8") + "&csmsid=" + URLEncoder.encode("123456789", "UTF-8") + "&sid=" + URLEncoder.encode("KGDCL", "UTF-8");
        Map<String, String> params = new HashMap<>();
        params.put("accept", "application/xml");
        HttpURLConnection connection = HttpUrlConnectionUtil.getHttpURLConnection(url, "GET", null, params);
        String content = HttpUrlConnectionUtil.getOkContent(connection);
        if (content.contains("<REFERENCEID>") && content.contains("</REFERENCEID>")) {
            Pattern pattern = Pattern.compile("<REFERENCEID>(.*?)<\\/REFERENCEID>", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                logger.info("SMS send with ref: " + matcher.group(1));
            }
            return true;
        }
        return false;
    }
}
