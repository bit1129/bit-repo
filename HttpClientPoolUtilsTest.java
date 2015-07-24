package com.app;

import com.app.HttpUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HttpClientPoolUtilsTest {

    private static final String LOG_DIR_NAME = "c:/http.client.logs";
    private static final String LOG_FILE_NAME = "http.client.log";

    //time of the requests
    private int loop = 1000;

    //interval between the requests
    private int loopInterval = 20;

    private String TEST_URL = "http://abc.json";

    @Test
    public void testHttpClientPool() throws Exception {

        int i = 0;
        File logDir = new File(LOG_DIR_NAME);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
        File logFile = new File(logDir, LOG_FILE_NAME);
        if (!logFile.exists()) {
            logFile.createNewFile();
        } else {
            logFile.renameTo(new File(logDir, LOG_FILE_NAME + "." + System.currentTimeMillis()));
            logFile = new File(logDir, LOG_FILE_NAME);
            logFile.createNewFile();
        }
        long allStart = System.currentTimeMillis();
        while (i++ < loop) {
            long start = System.currentTimeMillis();
            HttpUtils.httpInvoke(TEST_URL, null, null);
            long a = System.currentTimeMillis() - start;
            String timeSpent = "" + (a >= 1000 ? a + "(>1000)" : a);
            writeToFile(timeSpent + "\n", logFile);
            Thread.sleep(loopInterval);
        }
        writeToFile("Total time spent: " + (System.currentTimeMillis() - allStart) + "\n", logFile);
    }

    private void writeToFile(String timeSpent, File logFile) throws IOException {
        FileWriter f = new FileWriter(logFile, true);
        f.write(timeSpent);
        f.close();
    }
}
