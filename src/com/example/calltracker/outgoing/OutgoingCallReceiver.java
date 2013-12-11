package com.example.calltracker.outgoing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class OutgoingCallReceiver extends BroadcastReceiver {
	TelephonyManager telephony;

	public void onReceive(Context context, Intent intent) {
//		Toast.makeText(context, "ONRECIEVE CALLED", Toast.LENGTH_LONG).show();
		MyPhoneStateListener phoneListener = new MyPhoneStateListener(context);
		telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	public void onDestroy() {
		telephony.listen(null, PhoneStateListener.LISTEN_NONE);
	}

	

}
