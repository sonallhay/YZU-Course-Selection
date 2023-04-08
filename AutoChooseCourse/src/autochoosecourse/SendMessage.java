package autochoosecourse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class SendMessage {

    private final String eventName;
    private final String key;

    public SendMessage(String eventName, String key) {
        this.eventName = eventName;
        this.key = key;
    }

    public void trigger() throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("https://maker.ifttt.com/trigger/" + eventName + "/with/key/" + key);
        httpClient.execute(request);
    }

    public void trigger(String values) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("https://maker.ifttt.com/trigger/" + eventName + "/with/key/" + key);
        StringEntity params = new StringEntity(buildJson(values), "UTF-8");
        request.addHeader("content-type", "application/json");
        request.setEntity(params);
        httpClient.execute(request);
    }

    public void trigger(String values, int state) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost("https://maker.ifttt.com/trigger/" + eventName + "/with/key/" + key);
        if (state == 0) {
            StringEntity params = new StringEntity(buildJson(values), "UTF-8");
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            httpClient.execute(request);
        } else {
            httpClient.execute(request);
        }

    }

    private String buildJson(String values) throws UnsupportedEncodingException {        
        return "{\"value1\":\"" + values + "\"}";
    }

}