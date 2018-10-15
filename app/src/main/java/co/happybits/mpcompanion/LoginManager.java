package co.happybits.mpcompanion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.UUID;

import co.happybits.mpcompanion.networking.PlatformHttpConnection;
import okhttp3.Response;

import static co.happybits.mpcompanion.networking.ApiConfigKt.API_BASE_URL;

public class LoginManager {

    private static LoginManager _instance;

    private final String _deviceID;
    private final String _secret;

    private boolean _isRegistered;

    private String _apiToken;

    static synchronized LoginManager getInstance() {
        if (_instance == null) {
            _instance = new LoginManager();
        }

        return _instance;
    }

    private LoginManager() {
        Context context = MpCompanion.instance.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        _deviceID = prefs.getString("DeviceID", createXID());
        _secret = prefs.getString("AuthSecret", createXID());
        _isRegistered = prefs.getBoolean("IsRegistered", false);
    }

    boolean login(final String phone, final String countryCode) {

        // phone: the local phone number including area code (e.g. 2065551234)
        // countryCode: the two letter country identifier (e.g. "US")

        if (_isRegistered) {
            return true;
        }

        try {
            sendPhoneCode(phone, countryCode);

            String code = getAutoVerifyPhoneCode(phone, countryCode);

            verifyCode(phone, countryCode, code);

        } catch (IOException ex) {
            // network problem
        }

        return _isRegistered;
    }

    public synchronized String getApiToken() {
        return _apiToken;
    }

    public synchronized boolean isRegistered() {
        return _isRegistered;
    }

    public void auth() throws IOException {

        try {
            JSONObject authParams = new JSONObject()
                .put("device_id", _deviceID)
                .put("secret", _secret)
                .put("app_type", "mp")
                .put("app_version", "1.0.0")
                .put("app_build", 1000)
                .put("platform_type", "android")
                .put("platform_version", 24)
                .put("manufacturer", Build.MANUFACTURER)
                .put("model_name", Build.MODEL)
                .put("flavor", "dev")
                .put("locale", "en_US")
                .put("timezone", "PST");

            PlatformHttpConnection connection = new PlatformHttpConnection(API_BASE_URL + "/auth");
            Response response = connection.executeRequest(PlatformHttpConnection.POST, new HashMap<>(), authParams);
            JSONObject responseJSON = new JSONObject(new String(response.body().bytes()));

            parseAuthResponse(responseJSON);

        } catch (JSONException ex) {
            // empty
        }
    }

    private void sendPhoneCode(final String phone, final String countryCode) throws IOException {

        try {
            JSONObject params = new JSONObject()
                .put("phone", phone)
                .put("country_code", countryCode);

            doAuthenticatedRequest(PlatformHttpConnection.POST, "/auth/send-phone-code", params);

        } catch (JSONException ex) {
            // empty
        }
    }

    private String getAutoVerifyPhoneCode(final String phone, final String countryCode) throws IOException {

        try {
            JSONObject params = new JSONObject()
                .put("phone", phone)
                .put("country_code", countryCode);

            JSONObject responseJSON = doAuthenticatedRequest(PlatformHttpConnection.POST, "/admin/user/phone_code", params);
            return responseJSON.getString("code");

        } catch (JSONException ex) {
            // empty
        }

        return null;
    }

    private boolean verifyCode(final String phone, final String countryCode, final String code) throws IOException {

        try {
            JSONObject params = new JSONObject()
                .put("phone", phone)
                .put("country_code", countryCode)
                .put("verification_code", code);

            JSONObject responseJSON = doAuthenticatedRequest(PlatformHttpConnection.POST, "/auth/verify-phone-code", params);
            parseAuthResponse(responseJSON);

        } catch (JSONException ex) {
            // empty
        }

        return _isRegistered;
    }

    private JSONObject doAuthenticatedRequest(String method, String path, JSONObject params) throws IOException {

        try {
            PlatformHttpConnection connection = new PlatformHttpConnection(API_BASE_URL + path);
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + _apiToken);

            Response response = connection.executeRequest(method, headers, params);
            if (response.code() == 401) {
                auth();
                headers.put("Authorization", "Bearer " + _apiToken);
                response = connection.executeRequest(method, headers, params);
            }
            return new JSONObject(new String(response.body().bytes()));

        } catch (JSONException ex) {
            return new JSONObject();
        }
    }

    private synchronized void parseAuthResponse(JSONObject responseJSON) {
        try {
            _apiToken = responseJSON.getString("api_token");
            _isRegistered = responseJSON.getBoolean("is_registered");
        } catch (JSONException ex) {
            // empty
        }
    }

    /**
     * Creates a new XID based on a random UUID (uuid4).
     */
    public static String createXID() {
        UUID uuid = UUID.randomUUID();

        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        // Return only the first 22 characters.
        return Base64.encodeToString(buffer.array(), Base64.URL_SAFE).substring(0, 22);
    }
}
