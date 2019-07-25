package com.uds.urifia.smgenerator.utils;

public class Constants {
    public static final long BACK_OFF_DELAY = 5000;
    public static final long RESEARCH_DELAY = 10000;
    public static final long NGC_DELAY = 600000;
    public static final int ETGCDelay = 20; //minutes
    public static final String GCTaskTAG = "EVENTS_TABLE_GARBAGE_COLLECTION";
    public static String fileContentHead =
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "    <head>\n" +
            "        <meta charset=\"utf-8\"/>" +
            "        <title>Message</title>\n" +
            "        <style type=\"text/css\">\n" +
            "            body {\n" +
            "                width: 100%;" +
            "                font-family: 'Courier New', Courier, monospace;\n" +
            "                font-size: 14px;\n" +
            "                text-align: justify;\n" +
            "            }\n" +
            "        </style>\n" +
            "    </head>\n" +
            "    <body>\n" +
            "        <h4>Message</h4>\n" +
            "        <p>";

    public static String fileContentFoot =
            "        </p>\n" +
            "    </body>\n" +
            "</html>";
}
