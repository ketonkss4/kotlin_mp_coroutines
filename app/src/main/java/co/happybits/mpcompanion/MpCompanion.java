package co.happybits.mpcompanion;

import android.app.Application;
import android.os.Build;
import android.util.Base64;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.UUID;

import co.happybits.hbmx.BaseIntf;
import co.happybits.hbmx.BuildFlavor;
import co.happybits.hbmx.Hbmx;
import co.happybits.hbmx.PlatformImpl;
import co.happybits.hbmx.mp.AppCallbackIntf;
import co.happybits.hbmx.mp.ApplicationIntf;
import co.happybits.hbmx.tasks.BaseImpl;

public class MpCompanion extends Application {

    private static volatile MpCompanion _instance;

    public MpCompanion getInstance() {
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;

        String dataDir = getFilesDir() + File.separator + "hbmx";
        new File(dataDir).mkdir();

        co.happybits.hbmx.Environment hbmxEnvironment = new co.happybits.hbmx.Environment(
            BuildFlavor.DEV,
            true,
            "mp",
            "1.0.0",
            "10000",
            "android",
            Integer.toString(Build.VERSION.SDK_INT),
            dataDir,
            createXID(),
            Hbmx.LIBRARY_NAME);

        BaseImpl base = new BaseImpl();
        PlatformImpl platform = new PlatformImpl(this);
        Hbmx.initialize(this, hbmxEnvironment, base, platform);

        ApplicationIntf.createSingletonsPreDB(new AppCallbackIntf() {
            @Override
            public void onRestartNeeded(String postRestartAlert) {}

            @Override
            public void onSoftResetData() {}

            @Override
            public void onHardResetData() {}
        });

        ApplicationIntf.initSingletonsPostDB();
        ApplicationIntf.initFinishedForAppOpen();
    }

    private static String toXID(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        // Return only the frist 22 characters.
        return Base64.encodeToString(buffer.array(), Base64.URL_SAFE).substring(0, 22);
    }

    private static String createXID() {
        return toXID(UUID.randomUUID());
    }
}
