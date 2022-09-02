import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class RequestSender {
    public final static String KEY = "https://api.nasa.gov/planetary/apod?api_key=0647VTjDDnbdjN8HsTb7e67cKPaeriQadYgY2wty";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(
                        RequestConfig.custom()
                                .setConnectTimeout(5000)
                                .setSocketTimeout(30000)
                                .setRedirectsEnabled(false)
                                .build()
                ).build();
        HttpGet request = new HttpGet(KEY);
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
        CloseableHttpResponse response = httpClient.execute(request);

        ResponseReader responseReader = mapper.readValue(response.getEntity().getContent(), new TypeReference<>() {
        });
        System.out.println(responseReader.toString());

        String fileName = Paths.get(responseReader.getUrl().getPath()).getFileName().toString();
        request.setURI(responseReader.getUrl());
        request.setHeader(HttpHeaders.ACCEPT, ContentType.IMAGE_JPEG.getMimeType());

        response = httpClient.execute(request);

        FileOutputStream outstream = new FileOutputStream(
                new File("out" + File.separator + fileName));
        response.getEntity().writeTo(outstream);

        outstream.close();
        httpClient.close();
    }
}
