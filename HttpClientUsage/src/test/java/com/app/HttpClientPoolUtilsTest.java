package com.app;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientPoolUtilsTest {

    private final Logger LOG = LoggerFactory.getLogger("hc4");

    //time of the requests
    private int loop = 1000;

    //interval between the requests
    private int loopInterval = 20;

    private String TEST_URL = "http://pss.qiyi.domain/services/subscribe/list.htm?authcookie=f8crKciDhLBlYm2ZXIhiAdY6g4vJeHPO1Zm3TxKOGJyXj54m4b1&subscribeType=1&page=1&rows=5&sort=latestUpdate&fields=userinfo,verify_info,space,qiyi_vip_info,private_info";

    @Test
    public void testHttpClientPool() throws Exception {

        int i = 0;
        long allStart = System.currentTimeMillis();
        while (i++ < loop) {
            long start = System.currentTimeMillis();
            HttpUtils.httpInvoke(TEST_URL, null, null);
            long a = System.currentTimeMillis() - start;
            LOG.info("timeSpent: {}", (a >= 1000 ? a + "(>1000)" : a));
            Thread.sleep(loopInterval);
        }
        LOG.info("Total time spent: " + (System.currentTimeMillis() - allStart));
    }
}
