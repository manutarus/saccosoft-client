package manu.saccosoft;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Home extends Activity {
	Button button,button2;
	final Context context = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		addListenerOnButton();
	}
	
	public boolean checkConnectivity() {
		final WebserviceActivity wsa = new WebserviceActivity();
		try {
			InetAddress.getByName("google.com").isReachable(3);
			return true;
			} catch (UnknownHostException e){
				wsa.logger(e);
			return false;
			} catch (IOException e){
				wsa.logger(e);
			return false;
			}
    }

	public void addListenerOnButton() {
		
		final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		final EditText username = (EditText) findViewById(R.id.username);
		final EditText password = (EditText) findViewById(R.id.password);
		//String result;
		button = (Button) findViewById(R.id.btnLogin);
		button2 = (Button) findViewById(R.id.btnExit);
		final WebserviceActivity wsa = new WebserviceActivity();
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//if (checkConnectivity()) {
					if (username.length() < 1) {
						username.setError("username needed");
					} else if (password.length() < 1) {
						password.setError("password needed");
					} else {

						new Thread(new Runnable() {
							ProgressDialog dialog = ProgressDialog.show(
									Home.this, "Network Activity",
									"Login... Please wait...", true);
							
							public void run() {
								
								String result = wsa.login(username.getText()
										.toString(), password.getText()
										.toString(), "login");
								dialog.dismiss();
								if (result.equals("00")) {
									wsa.info("device_id",tm.getDeviceId());
									Utility.user = username.getText().toString();
									Intent i = new Intent(
											getApplicationContext(), Main.class);
									startActivity(i);
									finish();
								}
								else if (result.equals("14")) {
									runOnUiThread(new Runnable() {
										  public void run() {
											  okayDialog("Login FAILED","Wrong username or password\n"+
													  "FAILED username: "+username.getText().toString());
										  }
										});
								}
								
								else if (result.equals("18")) {
									runOnUiThread(new Runnable() {
										  public void run() {
											  okayDialog("Login FAILED","Switch is Down\n"+
													  "Please Contact System Admin\n");
										  }
										});
								}
								else  {
									runOnUiThread(new Runnable() {
										  public void run() {
											  if (!checkConnectivity()) {
														  okayDialog("Network Error","Please Enable Internet\nConnecton" +
														  		"On the Device"+
																  "FAILED username: "+username.getText().toString());
													  }
											  else{
											  okayDialog("Login FAILED","Network Error 404 try again\n"+
													  "FAILED account: "+username.getText().toString());
											  }
										  }
										});
								}
							}
						}).start();

					}
			}

		});
		button2.setOnClickListener(new View.OnClickListener() {
             public void onClick(View mView) {
            	 
            	 finish();
             }
         });
		
	}

	public void okayDialog(String title,String msg){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder
				.setCancelable(false)
				.setTitle(title)
				.setIcon(R.drawable.info_icon)
				.setMessage(msg)
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								
							}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}

}