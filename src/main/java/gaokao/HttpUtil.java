package gaokao;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    public static String get(String url){
        // String url = "http://meta.ha.in.yidian.com:8082/purple/v1/ddl/database/working_dw/table/etl_odw_event/partition/10000000";
        LOGGER.info(url);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response1 = null;
        String responseStr = "";
        try {
            response1 = client.execute(request);
            responseStr = EntityUtils.toString(response1.getEntity());
            LOGGER.debug(responseStr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseStr;
    }

    public static String post(String url, String data){
        LOGGER.debug(url);
        LOGGER.debug(data);
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);
        CloseableHttpResponse response1 = null;
        String responseStr = "";
        StringEntity se = null;
        request.setHeader("Content-Type", "application/json;charset=utf-8");
        se = new StringEntity(data,"utf-8");
        request.setEntity(se);
        try {
            response1 = client.execute(request);
            responseStr = EntityUtils.toString(response1.getEntity());
            LOGGER.debug(responseStr);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(String.format("can not get response from %s with data %s", url, data));
        } finally {
            try {
                if(response1!=null) {
                    response1.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseStr;
    }
}
