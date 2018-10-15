package co.happybits.mpcompanion.networking;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class PlatformHttpSession {

    private static final int IDLE_CONNECTION_COUNT = 5;
    private static final int IDLE_CONNECTION_TIMEOUT_SECONDS = 60;

    private static PlatformHttpSession _sharedSession;
    private OkHttpClient _client;

    public static synchronized PlatformHttpSession sharedSession() {
        if (_sharedSession == null) {
            _sharedSession = new PlatformHttpSession();
        }
        return _sharedSession;
    }

    public PlatformHttpSession() {
        ConnectionPool pool = new ConnectionPool(IDLE_CONNECTION_COUNT, IDLE_CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(pool)
                .addInterceptor(loggingInterceptor)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        // Add a 10 second ping interval to help alleviate SocketTimeoutException
        // https://github.com/square/okhttp/issues/3146
        builder.pingInterval(10, TimeUnit.SECONDS);

        _client = builder.build();
    }

    public OkHttpClient getClient() {
        return _client;
    }
}
