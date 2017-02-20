package retailworks.in.field.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocationUpdateService extends Service {
    public LocationUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    
}
