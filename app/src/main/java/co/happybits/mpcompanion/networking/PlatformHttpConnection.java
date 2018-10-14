package co.happybits.mpcompanion.networking;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.ByteString;

public class PlatformHttpConnection {

    private String _uri;
    private PlatformHttpSession _session;

    public static final String OPTIONS = "OPTIONS";
    public static final String GET = "GET";
    public static final String HEAD = "HEAD";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String TRACE = "TRACE";

    public PlatformHttpConnection(String uri) {
        this(uri, PlatformHttpSession.sharedSession());
    }

    public PlatformHttpConnection(String uri, PlatformHttpSession session) {
        _uri = uri;
        _session = session;
    }

    public Response executeRawRequest(String method, HashMap<String, String> headers, ByteBuffer body) throws IOException {
        OkHttpClient client = _session.getClient();
        ByteString bodyString = body != null ? ByteString.of(body) : ByteString.EMPTY;
        RequestBody requestBody = RequestBody.create(null,bodyString);
        Request request = createRequest(_uri, method, headers, requestBody);
        return client.newCall(request).execute();
    }

    public Response executeRequest(String method, HashMap<String, String> headers, JSONObject jsonBody) throws IOException {

        byte[] body = jsonBody.toString().getBytes("UTF-8");
        return executeRawRequest(method, headers, ByteBuffer.wrap(body));
    }

    public Response executeRequest(String method, HashMap<String, String> headers) throws IOException {
        return executeRawRequest(method, headers, null);
    }

    public Response executeRequest(String method) throws IOException {
        return executeRequest(method, null);
    }

    private static Request createRequest(String uri, String method, HashMap<String, String> headers, RequestBody requestBody) {
        Request.Builder builder = new Request.Builder().url(uri);

        // Can't pass a null body to method() for GET.
        if (method.equals(GET)) {
            builder = builder.get();
        } else {
            builder = builder.method(method, requestBody);
        }

        // Can't pass null headers to Headers.of() or headers().
        if (headers != null) {
            builder = builder.headers(Headers.of(headers));
        }

        return builder.build();
    }

    private static void addHeaders(Headers headers, HashMap<String, String> map) {
        for (String name : headers.names()) {
            map.put(name, headers.values(name).get(0));
        }
    }
}
