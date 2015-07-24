package com.app;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtilsFlumentAPINginxTest {

    private final Logger LOG = LoggerFactory.getLogger("hc4");

    //time of the requests
    private int loop = 1000;

    //interval between the requests
    private int loopInterval = 20;

    private String[] upstreams = new String[]{"10.153.77.101:8080", "10.153.77.102:8080", "10.153.77.103:8080", "10.153.108.116:8080", "10.153.108.117:8080"};

    private String TEST_URL = "http://10.10.191.134/services/subscribe/list.htm?authcookie=f8crKciDhLBlYm2ZXIhiAdY6g4vJeHPO1Zm3TxKOGJyXj54m4b1&subscribeType=1&page=1&rows=5&sort=latestUpdate&fields=userinfo,verify_info,space,qiyi_vip_info,private_info";

    //private String TEST_URL = "http://10.15.190.251/services/subscribe/list.htm?authcookie=f8crKciDhLBlYm2ZXIhiAdY6g4vJeHPO1Zm3TxKOGJyXj54m4b1&subscribeType=1&page=1&rows=5&sort=latestUpdate&fields=userinfo,verify_info,space,qiyi_vip_info,private_info";

//    private String TEST_URL = "http://%s/services/subscribe/list.htm?authcookie=f8crKciDhLBlYm2ZXIhiAdY6g4vJeHPO1Zm3TxKOGJyXj54m4b1&subscribeType=1&page=1&rows=5&sort=latestUpdate&fields=userinfo,verify_info,space,qiyi_vip_info,private_info";

//    private String TEST_URL = "http://r1.ykimg.com/05100000559E186467BC3D122F0380E6.js?ykRecommend.js";

//    private String TEST_URL = "http://qiyu.qiyi.smart/movie_p13n/tag?area=fox&uid=f2fb7bc8a02e7774a839e301793009e6";

    @Test
    public void testHttpClientPool() throws Exception {
        int numThreads = 1;
        Thread[] threads = new Thread[numThreads];
        for (int j = 0; j < numThreads; j++) {
            Thread t = new Thread() {
                int i = 0;

                @Override
                public void run() {
                    long allStart = System.currentTimeMillis();
                    while (i++ < loop) {
                        long start = System.currentTimeMillis();
                        int k = i % 4;
//                        TEST_URL = String.format(TEST_URL, upstreams[i % 4]);
                        HttpClientFlumentAPI.getHttpClientConnectionManagerInstance().execute(TEST_URL);
                        long a = System.currentTimeMillis() - start;
                        LOG.info("timeSpent: {}, ID: {}", (a >= 1000 ? a + "(>1000)" : a), k);
                        try {
                            Thread.sleep(loopInterval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    LOG.info("Total time spent: " + (System.currentTimeMillis() - allStart));
                }

            };
            threads[j] = t;
            t.start();
        }
        for (int i = 0; i < numThreads; i++) {
            threads[i].join();
        }


    }
}
