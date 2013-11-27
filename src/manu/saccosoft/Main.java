package manu.saccosoft;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

public class Main extends Activity implements Runnable {
	public boolean state = false;
	final private static int DIALOG_WITHDRAW = 1;
	final private static int DIALOG_BAL = 2;
	final private static int DIALOG_DEP = 3;
	final private static int DIALOG_MINI = 4;
	protected static final String TAG = "TAG";
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;
	Button mPrint, mDisc,exit;
	public String result2="";
	BluetoothAdapter mBluetoothAdapter;
	public UUID applicationUUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public ProgressDialog mBluetoothConnectProgressDialog;
	public BluetoothSocket mBluetoothSocket;
	BluetoothDevice mBluetoothDevice;
	final Context context = this;
	public String hold ="";
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.main_menu, menu);
		return true;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		AlertDialog dialogDetails = null;

		switch (id) {
		case DIALOG_WITHDRAW:{
			Log.i("Menu", "DIALOG_WITHDRAW");
			LayoutInflater inflater = LayoutInflater.from(this);
			View dialogview = inflater.inflate(R.layout.activity_withdraw, null);
			AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
			dialogbuilder.setTitle("Withdraw");
			dialogbuilder.setView(dialogview);
			dialogDetails = dialogbuilder.create();
			break;
		}
		
		case DIALOG_BAL:{
			Log.i("Menu", "DIALOG_BAL");
			LayoutInflater inflater = LayoutInflater.from(this);
			View dialogview = inflater.inflate(R.layout.activity_balance, null);
			AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
			dialogbuilder.setTitle("Balance");
			dialogbuilder.setView(dialogview);
			dialogDetails = dialogbuilder.create();
			break;
		}
		case DIALOG_DEP:{
			Log.i("Menu", "DIALOG_DEP");
			LayoutInflater inflater = LayoutInflater.from(this);
			View dialogview = inflater.inflate(R.layout.activity_deposit, null);
			AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
			dialogbuilder.setTitle("Deposit");
			dialogbuilder.setView(dialogview);
			dialogDetails = dialogbuilder.create();
			break;
		}
		case DIALOG_MINI:{
			Log.i("Menu", "DIALOG_MINI");
			LayoutInflater inflater = LayoutInflater.from(this);
			View dialogview = inflater.inflate(R.layout.activity_mini, null);
			AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
			dialogbuilder.setTitle("Ministatement");
			dialogbuilder.setView(dialogview);
			dialogDetails = dialogbuilder.create();
			break;
		}
		}
		return dialogDetails;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		
		switch (id) {
		case DIALOG_WITHDRAW:
		{
			final AlertDialog alertDialog = (AlertDialog) dialog;
			Button wbutton = (Button) alertDialog
					.findViewById(R.id.btnWithdraw);
			final WebserviceActivity wsa = new WebserviceActivity();
			final Utility util = new Utility();
			final PrinterHelper ph =new PrinterHelper();
			Toast.makeText(Main.this, "Withdraw", Toast.LENGTH_SHORT).show();
			final EditText idz = (EditText) alertDialog
					.findViewById(R.id.national_id);
			final EditText account = (EditText) alertDialog
					.findViewById(R.id.account);
			
			final EditText amount = (EditText) alertDialog
					.findViewById(R.id.amount);

			wbutton.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					if (idz.length() < 1) {
						idz.setError("id needed");
						}
					else if (account.length() < 1) {
						account.setError("account needed");
						
					} else if (amount.length() < 1) {
						amount.setError("amount needed");
					} else if (Double.parseDouble(amount.getText().toString())<100) {
						amount.setError("Should be more than KSH 100");
					}
					
					else {
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								context);
						alertDialogBuilder
								.setCancelable(false)
								.setTitle("Confirm Withdraw Details")
								.setIcon(R.drawable.info_icon)
								.setMessage(
										"Account:\n"+account.getText().toString()+"\nID:\n"+idz.getText().toString()+"\nAmount: \nKSH "+util.amountFormat(amount.getText().toString()))
								.setPositiveButton("Confirm",
										new DialogInterface.OnClickListener() {
									
											public void onClick(DialogInterface dialog,
													int id) {
					new Thread(new Runnable() {
						ProgressDialog dialog = ProgressDialog.show(
								Main.this, "Network Activity",
								"Withdrawal... Please wait...", true);
						public void run() {
							final String refNo =  util.refNo();
							String result2 = wsa.withdraw(idz.getText().toString(),account.getText().toString(),amount.getText().toString(),refNo,"withdraw");
							dialog.dismiss();
							alertDialog.dismiss();

                            wsa.info("Withdrawal Response",result2);
							
							if (result2.contains("00")) {
								if (mBluetoothSocket != null) {
									final String actual = result2.substring(0, result2.indexOf("+"));
									final String avail = result2.substring(result2.indexOf("+")+1, result2.indexOf("x"));
									final String date = result2.substring(result2.indexOf("x")+1, result2.length());
								Thread t = new Thread() {
									public void run() {
										try {
											
											hold=ph.withdrawPrint2(account.getText().toString(), util.amountFormat(amount.getText().toString()),avail,actual,Utility.user, date, refNo);
											toPrint(ph.withdrawPrint(account.getText().toString(), util.amountFormat(amount.getText().toString()),avail,actual,Utility.user, date, refNo));
										} catch (Exception e) {
											Log.e("Main", "Exe ", e);
										}
									}
								};
								t.start();
								
								}
								runOnUiThread(new Runnable() {
									  public void run() {
										  if (mBluetoothSocket != null) {
										  okayDialog2("Withdrawal Successful","Successfully withdrawn\n"+
									  "KSH "+util.amountFormat(amount.getText().toString())+"\nFrom: "+account.getText().toString()+"" +
									  "\nPress Ok to print Customer copy",hold);
										  hold="";
										  } else{
											  okayDialog("Withdrawal Successful","Successfully withdrawn\n"+
													  "KSH "+util.amountFormat(amount.getText().toString())+"\nFrom: "+account.getText().toString());
										  }
									  }
									});
								
							}
							else if (result2.equals("14")) {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Withdrawal FAILED","Please confirm account/ID and try again\n"+
									  "FAILED account:\n"+account.getText().toString()+
									  "\nFAILED ID:\n"+idz.getText().toString());
									  }
									});
							}
							else if (result2.equals("16")) {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Withdrawal FAILED","Not Enough Funds \n"+
									  "FAILED account:\n"+account.getText().toString()+
									  "\nExeeding Amount:\nKSH "+util.amountFormat(amount.getText().toString()));
									  }
									});
							}
							
							else if (result2.equals("18")) {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Withdrawal FAILED","Switch is Down\n"+
									  "Please Contact System Admin\n");
									  }
									});
							}
							
							else if (result2.equals("404")) {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Withdrawal FAILED","Network Error 404 try again\n"+
												  "FAILED account: "+account.getText().toString());
									  }
									});
								
							}
							else  {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Withdrawal FAILED","Network timed out try again\n"+
												  "FAILED account: "+account.getText().toString());
									  }
									});
								
							}
						}
					}).start();
				
											}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										Toast.makeText(Main.this, "Canceled for details check", 2000).show();
										
									}
								});

				AlertDialog alertDialog = alertDialogBuilder.create();

				alertDialog.show();
		
		}
		}
	});
			idz.setText("");account.setText("");amount.setText("");
			break;
		}
		
		case DIALOG_BAL:
		{
			final AlertDialog alertDialog = (AlertDialog) dialog;
			Button bbutton = (Button) alertDialog
					.findViewById(R.id.btnBal);
			final WebserviceActivity wsa = new WebserviceActivity();
			final Utility util = new Utility();
			final PrinterHelper ph =new PrinterHelper();
			Toast.makeText(Main.this, "Balance", Toast.LENGTH_SHORT).show();
			
			final EditText idz = (EditText) alertDialog
					.findViewById(R.id.national_id);
			final EditText account = (EditText) alertDialog
					.findViewById(R.id.account);
			

			bbutton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (idz.length() < 1) {
						idz.setError("id needed");
						}
					else if (account.length() < 1) {
						account.setError("account needed");
					
					} else {
						
					new Thread(new Runnable() {
						ProgressDialog dialog = ProgressDialog.show(
								Main.this, "Network Activity",
								"Balance... Please wait...", true);
						public void run() {
							final String refNo =  util.refNo();
							String result2 = wsa.balMini(idz.getText().toString(), account.getText().toString(), refNo,"balance");
							dialog.dismiss();
							alertDialog.dismiss();
							
							if (result2.contains("x")) {
								final String actual = result2.substring(0, result2.indexOf("+"));
								final String avail = result2.substring(result2.indexOf("+")+1, result2.indexOf("x"));
								if (mBluetoothSocket != null) {
									final String date = result2.substring(result2.indexOf("x")+1, result2.length());
								Thread t = new Thread() {
									public void run() {
										try {
											toPrint(ph.balancePrint(account.getText().toString(), avail,actual,Utility.user, date, refNo));
										} catch (Exception e) {
											Log.e("Main", "Exe ", e);
										}
									}
								};
								t.start();
								
								}
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Balance inquery",
									  "Actual balance     : "+actual +" KSH\n" +
									  "Availbale balance: "+avail +" KSH");
										  
									  }
									});
								
							}
							else if (result2.equals("14")) {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Balance FAILED","Withdrawal Balance \nPlease confirm account and try again\n"+
									  "FAILED account: "+account.getText().toString()+
									  " FAILED ID: "+idz.getText().toString());
									  }
									});
							}
							
							else if (result2.equals("18")) {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Balance FAILED","Switch is Down\n"+
									  "Please Contact System Admin\n");
									  }
									});
							}
							
							else if (result2.equals("404")) {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Balance FAILED","Network Error 404 try again\n"+
												  "FAILED account: "+account.getText().toString());
									  }
									});
								
							}
							else  {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Balance FAILED","Network timed out try again\n"+
												  "FAILED account: "+account.getText().toString());
									  }
									});
								
							}
						}
					}).start();
				
				}
				}
			});
			idz.setText("");account.setText("");
			break;
		}		
		case DIALOG_MINI:
		{
			final AlertDialog alertDialog = (AlertDialog) dialog;
			Button bbutton = (Button) alertDialog
					.findViewById(R.id.btnMini);
			final WebserviceActivity wsa = new WebserviceActivity();
			final Utility util = new Utility();
			final PrinterHelper ph =new PrinterHelper();
			Toast.makeText(Main.this, "Ministatement", Toast.LENGTH_SHORT).show();
			final EditText idz = (EditText) alertDialog
					.findViewById(R.id.national_id);
			final EditText account = (EditText) alertDialog
					.findViewById(R.id.account);
			
			bbutton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (idz.length() < 1) {
						idz.setError("id needed");
						}
					else if (account.length() < 1) {
						account.setError("account needed");
					
					} else {
					new Thread(new Runnable() {
						ProgressDialog dialog = ProgressDialog.show(
								Main.this, "Network Activity",
								"Ministatement... Please wait...", true);
						public void run() {
							final String refNo =  util.refNo();
							
							final String result2 = wsa.balMini(idz.getText().toString(), account.getText().toString(), refNo,"ministatement");
							dialog.dismiss();
							alertDialog.dismiss();
							
							if (result2.contains("x")) {
								if (mBluetoothSocket != null) {
								Thread t = new Thread() {
									public void run() {
										try {
											toPrint(ph.miniPrint(account.getText().toString(), result2,Utility.user, refNo));
										} catch (Exception e) {
											Log.e("Main", "Exe ", e);
										}
									}
								};
								t.start();
								
								}
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Ministatement inquery",
												  util.formatmini(result2));
										  
									  }
									});
								
							}
							else if (result2.equals("14")) {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Ministatement FAILED","\nPlease confirm account and try again\n"+
									  "FAILED account: "+account.getText().toString()+
									  " FAILED ID: "+idz.getText().toString());
									  }
									});
							}
							else if (result2.equals("18")) {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Ministatement FAILED","Switch is Down\n"+
									  "Please Contact System Admin\n");
									  }
									});
							}
							else if (result2.equals("404")) {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Ministatement FAILED","Network Error 404 try again\n"+
												  "FAILED account: "+account.getText().toString());
									  }
									});
								
							}
							else  {
								runOnUiThread(new Runnable() {
									  public void run() {
										  okayDialog("Ministatement FAILED","Network timed out try again\n"+
												  "FAILED account: "+account.getText().toString());
									  }
									});
								
							}
						}
					}).start();
				
				}
				}
			});
			idz.setText("");account.setText("");
			break;
		}		
		case DIALOG_DEP:
		{
			final AlertDialog alertDialog = (AlertDialog) dialog;
			Button dbutton = (Button) alertDialog
					.findViewById(R.id.btnDeposit);
			final WebserviceActivity wsa = new WebserviceActivity();
			final Utility util = new Utility();
			final PrinterHelper ph =new PrinterHelper();
			Toast.makeText(Main.this, "Deposit", Toast.LENGTH_SHORT).show();
		
			final EditText amount = (EditText) alertDialog
					.findViewById(R.id.amount);
			final EditText account = (EditText) alertDialog
					.findViewById(R.id.account);
			
			
			dbutton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (account.length() < 1) {
						account.setError("account needed");
						}
					else if (amount.length() < 1) {
						amount.setError("amount needed");
					
					} else {
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								context);

						alertDialogBuilder
								.setCancelable(false)
								.setTitle("Confirm Deposit Details")
								.setIcon(R.drawable.info_icon)
								.setMessage(
										"Account:\n"+account.getText().toString()+"\nAmount: \nKSH "+util.amountFormat(amount.getText().toString()))
								.setPositiveButton("Confirm",
										new DialogInterface.OnClickListener() {
									
											public void onClick(DialogInterface dialog,
													int id) {
												new Thread(new Runnable() {
													ProgressDialog dialog = ProgressDialog.show(
															Main.this, "Network Activity",
															"Deposit... Please wait...", true);
													public void run() {
														final String refNo =  util.refNo();
														String result2 = wsa.deposit(account.getText().toString(),amount.getText().toString(),refNo,"deposit");
														dialog.dismiss();
														String result = result2.substring(0, 2);
														alertDialog.dismiss();
														if (result.equals("00")) {
															if (mBluetoothSocket != null) {
																final String date = result2.substring(3, result2.length());
															Thread t = new Thread() {
																public void run() {
																	try {
																		hold =ph.depositPrint2(account.getText().toString(), util.amountFormat(amount.getText().toString()), Utility.user, date, refNo);
																		toPrint(ph.depositPrint(account.getText().toString(), util.amountFormat(amount.getText().toString()), Utility.user, date, refNo));
																	
																	} catch (Exception e) {
																		Log.e("Main", "Exe ", e);
																	}
																}
															};
															t.start();
															
															}
															runOnUiThread(new Runnable() {
																  public void run() {
																	  if (mBluetoothSocket != null) {
																	  okayDialog2("Deposit Successful","Successfully Deposited\n"+
																	  "KSH "+util.amountFormat(amount.getText().toString())+"\nTo: "+account.getText().toString()+"" +
																	  		"\nPress Ok to print Customer copy",hold);
																	  hold="";
														
																  }
																  
																  else{
																	  okayDialog("Deposit Successful","Successfully Deposited\n"+
																			  "KSH "+util.amountFormat(amount.getText().toString())+"\nTo: "+account.getText().toString());
																  }
																  }
																});
															
														}
														else if (result.equals("14")) {
															runOnUiThread(new Runnable() {
																  public void run() {
																	  okayDialog("Deposit FAILED","Please confirm account and try again\n"+
																  "FAILED account: "+account.getText().toString()+"\n"+
																  " FAILED ID: "+amount.getText().toString());
																  }
																});
														}
														
														else if (result.equals("18")) {
															runOnUiThread(new Runnable() {
																  public void run() {
																	  okayDialog("Deposit FAILED","Switch is Down\n"+
																  "Please Contact System Admin\n");
																  }
																});
														}
														
														else if (result2.equals("404")) {
															runOnUiThread(new Runnable() {
																  public void run() {
																	  okayDialog("Deposit FAILED","Network Error 404 try again\n"+
																			  "FAILED account: "+account.getText().toString());
																  }
																});
															
														}
														else  {
															runOnUiThread(new Runnable() {
																  public void run() {
																	  okayDialog("Deposit FAILED","Network timed out try again\n"+
																			  "FAILED account: "+account.getText().toString());
																  }
																});
															
														}
													}
												}).start();
												
											}
										})
								.setNegativeButton("Cancel",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int id) {
												
												
												Toast.makeText(Main.this, "Canceled for details check", 2000).show();
												
											}
										});

						AlertDialog alertDialog = alertDialogBuilder.create();

						alertDialog.show();
				
				}
				}
			});
			amount.setText("");account.setText("");
			break;
		}
		
	}	
	}
	public void withdraw(int activity){
		if(activity==1){
			showDialog(DIALOG_DEP);
		}
	    else if(activity==2){
			showDialog(DIALOG_WITHDRAW);
		}
		else if(activity==3){
			showDialog(DIALOG_BAL);
		}
		else if(activity==4){
			showDialog(DIALOG_MINI);
		}
	}
		
	public void connectB() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (mBluetoothAdapter == null) {
			Toast.makeText(Main.this, "Bleutoth Checked", 2000).show();
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				ListPairedDevices();
				Intent connectIntent = new Intent(Main.this,
						DeviceListActivity.class);
				startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
			}
		}
	}

	public void print() {
		exit = (Button) findViewById(R.id.exit);
		exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View mView) {
           	 
           	 finish();
            }
        });

		mPrint = (Button) findViewById(R.id.mPrint);
		mPrint.setOnClickListener(new View.OnClickListener() {
			public void onClick(View mView) {
				if (mBluetoothSocket == null) {
					Toast.makeText(Main.this,
							"Please select printer\nand try again",
							Toast.LENGTH_SHORT).show();
					connectB();
				}
				Thread t = new Thread() {
					public void run() {
						try {
							toPrint(PrinterHelper.testPrint(Utility.user));
						} catch (Exception e) {
							Log.e("Main", "Exe ", e);
						}
					}
				};
				t.start();
			}
		});

	}

	@Override
	public void onCreate(Bundle mSavedInstanceState) {
		super.onCreate(mSavedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		print();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_deposit) {
            if (mBluetoothSocket == null) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                alertDialogBuilder
                        .setCancelable(true)
                        .setTitle("Printer Error")
                        .setIcon(R.drawable.info_icon)
                        .setMessage(
                                "Please connect to a printer\n"
                                        + "if you skip recipts cant be printed")
                        .setPositiveButton("Connect",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Toast.makeText(
                                                Main.this,
                                                "Please select printer\nand try again",
                                                Toast.LENGTH_SHORT).show();

                                        connectB();
                                    }
                                })
                        .setNegativeButton("Skip",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        withdraw(1);
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();

            } else {

                withdraw(1);
            }
            return true;
        } else if (i == R.id.menu_withdraw) {
            if (mBluetoothSocket == null) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                alertDialogBuilder
                        .setCancelable(true)
                        .setTitle("Printer Error")
                        .setIcon(R.drawable.info_icon)
                        .setMessage(
                                "Please connect to a printer\n"
                                        + "if you skip recipts cant be printed")
                        .setPositiveButton("Connect",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Toast.makeText(
                                                Main.this,
                                                "Please select printer\nand try again",
                                                Toast.LENGTH_SHORT).show();
                                        connectB();
                                    }
                                })
                        .setNegativeButton("Skip",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        withdraw(2);
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            } else {

                withdraw(2);
            }
            return true;
        } else if (i == R.id.menu_balance) {
            if (mBluetoothSocket == null) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                alertDialogBuilder
                        .setCancelable(true)
                        .setTitle("Printer Error")
                        .setIcon(R.drawable.info_icon)
                        .setMessage(
                                "Please connect to a printer\n"
                                        + "if you skip recipts cant be printed")
                        .setPositiveButton("Connect",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Toast.makeText(
                                                Main.this,
                                                "Please select printer\nand try again",
                                                Toast.LENGTH_SHORT).show();
                                        connectB();
                                    }
                                })
                        .setNegativeButton("Skip",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        withdraw(3);
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            } else {

                withdraw(3);
            }
            return true;
        } else if (i == R.id.menu_mini) {
            if (mBluetoothSocket == null) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                alertDialogBuilder
                        .setCancelable(true)
                        .setTitle("Printer Error")
                        .setIcon(R.drawable.info_icon)
                        .setMessage(
                                "Please connect to a printer\n"
                                        + "if you skip recipts cant be printed")
                        .setPositiveButton("Connect",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Toast.makeText(
                                                Main.this,
                                                "Please select printer\nand try again",
                                                Toast.LENGTH_SHORT).show();
                                        connectB();
                                    }
                                })
                        .setNegativeButton("Skip",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        withdraw(4);
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            } else {

                withdraw(4);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

	}

	@Override
	public void onBackPressed() {
		try {
			if (mBluetoothSocket != null)
				mBluetoothSocket.close();
		} catch (Exception e) {
			Log.e("Tag", "Exe ", e);
		}
		setResult(RESULT_CANCELED);
		finish();
	}
	public void onActivityResult(int mRequestCode, int mResultCode,
			Intent mDataIntent) {
		super.onActivityResult(mRequestCode, mResultCode, mDataIntent);

		switch (mRequestCode) {
		case REQUEST_CONNECT_DEVICE:
			if (mResultCode == Activity.RESULT_OK) {
				Bundle mExtra = mDataIntent.getExtras();
				String mDeviceAddress = mExtra.getString("DeviceAddress");
				Log.v(TAG, "Coming incoming address " + mDeviceAddress);
				if(state = false)
				new Thread(new Runnable() {
					public void run() {
							try {
								Thread.sleep(15000);
								
								runOnUiThread(new Runnable() {
									  public void run() {
										  Toast.makeText(Main.this, "Printer out of range", 2500).show();	
										  
										  Intent i = new Intent(
													getApplicationContext(), Main.class);
											startActivity(i);
											finish();
									  }
									});
								
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							  if(mBluetoothAdapter==null){
							
							  }
						
						
					}
				}).start();
				mBluetoothDevice = mBluetoothAdapter
						.getRemoteDevice(mDeviceAddress);
				
				mBluetoothConnectProgressDialog = ProgressDialog.show(this,
						"Connecting...", mBluetoothDevice.getName() + " : "
								+ mBluetoothDevice.getAddress(), true, false);

				if(mBluetoothDevice.equals(false)){
					Intent i = new Intent(
							getApplicationContext(), Main.class);
					startActivity(i);
					finish();
				}
				
				Thread mBlutoothConnectThread = new Thread(this);
				mBlutoothConnectThread.start();			
				
			}
			break;

		case REQUEST_ENABLE_BT:
			if (mResultCode == Activity.RESULT_OK) {
				ListPairedDevices();
				Intent connectIntent = new Intent(Main.this,
						DeviceListActivity.class);
				startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE);
			} else {
				Toast.makeText(Main.this, "Message", 2000).show();
			}
			break;
		}
	}

	public void ListPairedDevices() {
		Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter
				.getBondedDevices();
		if (mPairedDevices.size() > 0) {
			for (BluetoothDevice mDevice : mPairedDevices) {
				Log.v(TAG, "PairedDevices: " + mDevice.getName() + "  "
						+ mDevice.getAddress());
			}
		}
	}

	public void run() {
		try {
			
			mBluetoothSocket = mBluetoothDevice
					.createRfcommSocketToServiceRecord(applicationUUID);
			mBluetoothAdapter.cancelDiscovery();
			mBluetoothSocket.connect();
			mHandler.sendEmptyMessage(0);
			
		} catch (IOException eConnectException) {
			Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
			closeSocket(mBluetoothSocket);
			
			Intent i = new Intent(
					getApplicationContext(), Main.class);
			startActivity(i);
			finish();
			runOnUiThread(new Runnable() {
				  public void run() {
					  Toast.makeText(Main.this, "Printer out of range", 2500).show();	
					  
					  Intent i = new Intent(
								getApplicationContext(), Main.class);
						startActivity(i);
						finish();
				  }
				});
			return;
		}
	}

	public void closeSocket(BluetoothSocket nOpenSocket) {
		try {
			nOpenSocket.close();
			Log.d(TAG, "SocketClosed");
		} catch (IOException ex) {
			Log.d(TAG, "CouldNotCloseSocket");
		}
	}

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mBluetoothConnectProgressDialog.dismiss();
			
			state= true;
			Toast.makeText(Main.this, "DeviceConnected", 5000).show();
		}
	};

	public static byte intToByteArray(int value) {
		byte[] b = ByteBuffer.allocate(4).putInt(value).array();

		for (int k = 0; k < b.length; k++) {
			System.out.println("Selva  [" + k + "] = " + "0x"
					+ UnicodeFormatter.byteToHex(b[k]));
		}

		return b[3];
	}

	public byte[] sel(int val) {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.putInt(val);
		buffer.flip();
		return buffer.array();
	}

	public void toPrint(String streamIn) throws IOException {
		OutputStream os = mBluetoothSocket.getOutputStream();
		os.write(streamIn.getBytes());
		int gs = 29;
		os.write(intToByteArray(gs));
		int h = 104;
		os.write(intToByteArray(h));
		int n = 162;
		os.write(intToByteArray(n));

		// Setting Width
		int gs_width = 29;
		os.write(intToByteArray(gs_width));
		int w = 119;
		os.write(intToByteArray(w));
		int n_width = 2;
		os.write(intToByteArray(n_width));

		// Print BarCode
		int gs1 = 29;
		os.write(intToByteArray(gs1));
		int k = 107;
		os.write(intToByteArray(k));
		int m = 73;
		os.write(intToByteArray(m));

		String barCodeVal = "SSF02A028060000005";
		System.out.println("Barcode Length : " + barCodeVal.length());
		int n1 = barCodeVal.length();
		os.write(intToByteArray(n1));

		for (int i = 0; i < barCodeVal.length(); i++) {
			os.write((barCodeVal.charAt(i) + "").getBytes());
		}

	}
	
	public void okayDialog(String title,String msg){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

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
	
	
	public void okayDialog2(String title,String msg,final String printText){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		alertDialogBuilder
				.setCancelable(false)
				.setTitle(title)
				.setIcon(R.drawable.info_icon)
				.setMessage(msg)
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
									
									try {
										toPrint(printText);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}
	
	
}