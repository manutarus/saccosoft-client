package manu.saccosoft;

import android.app.Activity;
import android.util.Log;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WebserviceActivity extends Activity {
	private final String NAMESPACE = "http://ws";
	private final String URL = "http://41.215.23.242:8080/SaccoSoft/services/Transactions?wsdl";

	// private final String URL =
	// "http://192.168.43.41:8080/SaccoSoft/services/Transactions?wsdl";

	public String balMini(String id, String account, String refNo, String method) {
		final String SOAP_ACTION = "http://ws/" + method;
		SoapObject request = new SoapObject(NAMESPACE, method);

		PropertyInfo idPI = new PropertyInfo();
		idPI.setName("id");
		idPI.setValue(id);
		idPI.setType(double.class);
		request.addProperty(idPI);

		PropertyInfo accountPI = new PropertyInfo();
		accountPI.setName("account");
		accountPI.setValue(account);
		accountPI.setType(double.class);
		request.addProperty(accountPI);

		PropertyInfo refNoPI = new PropertyInfo();
		refNoPI.setName("refNo");
		refNoPI.setValue(refNo);
		refNoPI.setType(double.class);
		request.addProperty(refNoPI);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			info("balMini WS",response.toString());
			return response.toString();

		} catch (Exception e) {
			logger(e);
		}

		return "404";
	}

	public void logger(Exception e) {
		Log.i("Header", "-----------");
		e.printStackTrace();
		Log.i("Footer", "-----------");
	}

	public void info(String tag, String info) {
		Log.i("Header", "-----------");
		Log.i(tag, info);
		Log.i("Footer", "-----------");
	}

	public String login(String username, String password, String method) {
		final String SOAP_ACTION = "http://ws/" + method;
		SoapObject request = new SoapObject(NAMESPACE, method);

		PropertyInfo usernamePI = new PropertyInfo();
		usernamePI.setName("username");
		usernamePI.setValue(username);
		usernamePI.setType(double.class);
		request.addProperty(usernamePI);

		PropertyInfo passwordPI = new PropertyInfo();
		passwordPI.setName("password");
		passwordPI.setValue(password);
		passwordPI.setType(double.class);
		request.addProperty(passwordPI);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			info("login WS",response.toString());
			return response.toString();

		} catch (Exception e) {
			logger(e);
		}

		return "404";
	}

	public String withdraw(String id, String account, String amount,
			String refNo, String method) {
		final String SOAP_ACTION = "http://ws/" + method;
		SoapObject request = new SoapObject(NAMESPACE, method);

		PropertyInfo idPI = new PropertyInfo();
		idPI.setName("id");
		idPI.setValue(id);
		idPI.setType(double.class);
		request.addProperty(idPI);

		PropertyInfo accountPI = new PropertyInfo();
		accountPI.setName("account");
		accountPI.setValue(account);
		accountPI.setType(double.class);
		request.addProperty(accountPI);

		PropertyInfo amountPI = new PropertyInfo();
		amountPI.setName("amount");
		amountPI.setValue(amount);
		amountPI.setType(double.class);
		request.addProperty(amountPI);

		PropertyInfo refNoPI = new PropertyInfo();
		refNoPI.setName("refNo");
		refNoPI.setValue(refNo);
		refNoPI.setType(double.class);
		request.addProperty(refNoPI);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			info("Withdraw WS",response.toString());
			return response.toString();

		} catch (Exception e) {
			logger(e);
		}

		return "404";
	}

	public String deposit(String account, String amount, String refNo,
			String method) {
		final String SOAP_ACTION = "http://ws/" + method;
		SoapObject request = new SoapObject(NAMESPACE, method);

		PropertyInfo accountPI = new PropertyInfo();
		accountPI.setName("account");
		accountPI.setValue(account);
		accountPI.setType(double.class);
		request.addProperty(accountPI);

		PropertyInfo passwordPI = new PropertyInfo();
		passwordPI.setName("amount");
		passwordPI.setValue(amount);
		passwordPI.setType(double.class);
		request.addProperty(passwordPI);

		PropertyInfo refNoPI = new PropertyInfo();
		refNoPI.setName("refNo");
		refNoPI.setValue(refNo);
		refNoPI.setType(double.class);
		request.addProperty(refNoPI);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

		try {
			androidHttpTransport.call(SOAP_ACTION, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			info("Deposit WS",response.toString());
			return response.toString();

		} catch (Exception e) {
			logger(e);
		}
		return "404";
	}

	public boolean haveNetworkConnection() {
		try {
			InetAddress.getByName("google.com").isReachable(3);
			return true;
			} catch (UnknownHostException e){
				logger(e);
			return false;
			} catch (IOException e){
				logger(e);
			return false;
			}

		
	}

}