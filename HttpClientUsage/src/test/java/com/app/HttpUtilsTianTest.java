package com.app;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpUtilsTianTest {
    private int loop = 1000;
    private int loopInterval = 20;

    private final Logger LOG = LoggerFactory.getLogger("hcTian");

    private String TEST_URL = "http://pss.qiyi.domain/services/subscribe/list.htm?authcookie=f8crKciDhLBlYm2ZXIhiAdY6g4vJeHPO1Zm3TxKOGJyXj54m4b1&subscribeType=1&page=1&rows=5&sort=latestUpdate&fields=userinfo,verify_info,space,qiyi_vip_info,private_info";

    @Test
    public void testHttpClient() throws Exception {
        for (int j = 0; j < 100; j++) {
            Thread t = new Thread() {
                int i = 0;

                @Override
                public void run() {
                    long allStart = System.currentTimeMillis();
                    while (i++ < loop) {
                        long start = System.currentTimeMillis();
                            HttpUtilsTian.get(TEST_URL);

                        long a = System.currentTimeMillis() - start;
                        LOG.info("timeSpent: {}", (a >= 1000 ? a + "(>1000)" : a));
                        try {
                            Thread.sleep(loopInterval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    LOG.info("Total time spent: " + (System.currentTimeMillis() - allStart));
                }

            };
            t.start();
        }
        Thread.sleep(10 * 60 * 1000);
    }
}