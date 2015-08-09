package yellr.net.yellr_android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import yellr.net.yellr_android.services.NewAssignmentNotifyService;

public class BootStartServiceIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, NewAssignmentNotifyService.class);
            context.startService(pushIntent);
        }
    }
}