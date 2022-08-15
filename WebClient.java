package Uralsib;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.OptionalLong;

import static java.lang.String.format;
import static java.lang.System.err;
import static java.lang.System.out;

public class WebClient {

    private static final String HEADER_RANGE = "Range";
    private static final String RANGE_FORMAT = "bytes=%d-%d";
    private static final String HEADER_CONTENT_LENGTH = "content-length";
    private static final String HTTP_HEAD = "HEAD";
    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final int HTTP_PARTIAL_CONTENT = 206;

    private final HttpClient httpClient;
    private int maxAttempts;

    public WebClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.maxAttempts = DEFAULT_MAX_ATTEMPTS;
    }

    public WebClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private long contentLength(final String uri)
            throws URISyntaxException, IOException, InterruptedException {

        HttpRequest headRequest = HttpRequest
                .newBuilder(new URI(uri))
                .method(HTTP_HEAD, HttpRequest.BodyPublishers.noBody())
                .version(HttpClient.Version.HTTP_2)
                .build();

        HttpResponse<String> httpResponse = httpClient.send(headRequest, HttpResponse.BodyHandlers.ofString());

        OptionalLong contentLength = httpResponse
                .headers().firstValueAsLong(HEADER_CONTENT_LENGTH);

        return contentLength.orElse(0L);
    }

    public Uralsib.WebClient.Response download(final String uri, Long firstBytePos, Long  lastBytePos)
            throws URISyntaxException, IOException, InterruptedException {

        HttpRequest request = HttpRequest
                .newBuilder(new URI(uri))
                .header(HEADER_RANGE, format(RANGE_FORMAT, firstBytePos, lastBytePos))
                .GET()
                .version(HttpClient.Version.HTTP_2)
                .build();

        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

        return new Uralsib.WebClient.Response(new BufferedInputStream(response.body()), response.statusCode(), response.headers());
    }

    public void download(final String uri,String fileName)
            throws URISyntaxException, IOException, InterruptedException {

        URL url = new URL(uri);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");

        Long chunkSize = (Long)  (conn.getContentLengthLong()/100);
        final Long expectedLength = (Long ) conn.getContentLengthLong();
        Long  firstBytePos = Long.valueOf(0);
        Long  lastBytePos = (Long ) (chunkSize - 1);


        int downloadedLength = 0;

        int attempts = 1;
        int i=0;
        try ( FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            while (downloadedLength < expectedLength && attempts < maxAttempts) {

                Uralsib.WebClient.Response response;

                try {
                    response = download(uri, firstBytePos, lastBytePos);
                } catch (IOException e) {
                    attempts++;
                    err.println(format("I/O error has occurred. %s", e));
                    out.println(format("Going to do %d attempt", attempts));
                    continue;
                }

                try (response.inputStream) {
                    byte[] chunkedBytes = response.inputStream.readAllBytes();

                    downloadedLength += chunkedBytes.length;

                    if (isPartial(response)) {
                        firstBytePos = lastBytePos + 1;
                        lastBytePos = Math.min(lastBytePos + chunkSize, expectedLength - 1);
                        fileOutputStream.write(chunkedBytes, 0, chunkedBytes.length);
                        i++;
                        System.out.println("Percent done: "+ i);
                    }
                } catch (IOException e) {
                    attempts++;
                    err.println(format("I/O error has occurred. %s", e));
                    out.println(format("Going to do %d attempt", attempts));
                    continue;
                }

                attempts = 1; // reset attempts counter
            }


        } catch (Exception e) {
            System.out.println(e);
        }




        if (attempts >= maxAttempts) {
            err.println("A file could not be downloaded. Number of attempts are exceeded.");
        }

    }

    private boolean isPartial(Uralsib.WebClient.Response response) {
        return response.status == HTTP_PARTIAL_CONTENT;
    }

    public int maxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public static class Response {
        final BufferedInputStream inputStream;
        final int status;
        final HttpHeaders headers;

        public Response(BufferedInputStream inputStream, int status, HttpHeaders headers) {
            this.inputStream = inputStream;
            this.status = status;
            this.headers = headers;
        }
    }
}