package manu.saccosoft;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utility {

	public static String user = "";

	public String refNo() {
		SecureRandom random = new SecureRandom();
		return (new BigInteger(60, random).toString(32).toUpperCase());
	}

	public String amountFormat(String values) {
		NumberFormat nf = NumberFormat.getCurrencyInstance().getInstance();
		return nf.format(new BigDecimal(values));

	}

	public static int nthOccurrence(String str, char c, int n) {
		int pos = str.indexOf(c, 0);
		while (n-- > 0 && pos != -1)
			pos = str.indexOf(c, pos + 1);
		return pos;
	}

	public String formatmini(String mini) {
		Vector<String> minis = new Vector<String>();
		minis = new Vector<String>(Arrays.asList(mini.split("x")));
		Iterator itr = minis.iterator();
		String hold = null, full ="Date        Type    KSH\n";
		String str;
		while (itr.hasNext()) {
			str = ((String) itr.next());
			if (str.contains("*")) {
				int type = nthOccurrence(str, '*',0 )+1;
				int amount = nthOccurrence(str, '*', 1) + 1;
				hold= fdate(str.substring(0,str.indexOf("*")))
						+ "  "
						+  str.substring(type).substring(0,str.substring(type).indexOf("*"))
						+ "  "
				+amountFormat((str.substring(amount).substring(0,
								str.substring(amount).indexOf("*")).substring(0, str.substring(amount).substring(0,
								str.substring(amount).indexOf("*")).length()-3)))
								+(str.substring(amount).substring(0,
										str.substring(amount).indexOf("*")).substring(str.substring(amount).substring(0,
												str.substring(amount).indexOf("*")).length()-3))+"\n";


				full += "\n" + hold;
			}
			// hold = ((String) itr.next()).replaceAll("\\s+", "*");
			// minis.add(hold.substring(0, hold.length()-1));
		}
		return full;
	}
	
	public String fdate(String dateIn){
		Date date = Calendar.getInstance().getTime();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yy");
		String today = formatter.format(date);
		return today;
	}
	
	

}
