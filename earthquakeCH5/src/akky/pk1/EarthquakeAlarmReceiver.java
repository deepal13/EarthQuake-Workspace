package akky.pk1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class EarthquakeAlarmReceiver extends BroadcastReceiver{
	public static final String ACTION_REFRESH_EARTHQUAKE_ALARM =
			"akky.pk1.ACTION_REFRESH_EARTHQUAKE_ALARM";

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "Onre", Toast.LENGTH_LONG).show();
		
		// TODO Auto-generated method stub
		Intent startIntent = new Intent(context, EarthquakeService.class);
		context.startService(startIntent);	
	}

}
