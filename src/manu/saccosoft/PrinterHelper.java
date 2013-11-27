package manu.saccosoft;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

public class PrinterHelper {

	public static String header() {
		return "\n     Chepsol Tea Growers"
				+ "\nSavings & Credit Co-operative \n          society \n"
				+ "-------------------------------" + "\n\n";
	}
	public static String footer() {
		return "\n         With Thanks. \n"
		+ "-------------------------------\n"
		+ "\n \n \n \n";
	}

	public String depositPrint(String account, String amount,
			String user, String date,String refNo) {
		String format = "";
		format = header();
		format = format + "Account No. "+account+"\n";
		format = format + "-------------------------------\n";
		format = format + "Deposit:" + "    " + amount+" KSH\n";
		format = format + "-------------------------------\n";
		format = format + "Ref No .    "+refNo+"\n";
		format = format + "-------------------------------\n";
		format = format + "Served By:  "+user+"\n \n";
		format = format + "Customer Sign ................\n";
		format = format + "          Agent Copy\n";
		format = format + "  "+date+"\n";
		format = format +footer();
		return format;

	}
	
	public String depositPrint2(String account, String amount,
			String user, String date,String refNo) {
		String format = "";
		format = header();
		format = format + "Account No. "+account+"\n";
		format = format + "-------------------------------\n";
		format = format + "Deposit:" + "    " + amount+" KSH\n";
		format = format + "-------------------------------\n";
		format = format + "Ref No .    "+refNo+"\n";
		format = format + "-------------------------------\n";
		format = format + "Served By:  "+user+"\n \n";
		format = format + "Agent Sign ................\n";
		format = format + "          Customer copy\n";
		format = format + "  "+date+"\n";
		format = format +footer();
		return format;
		

	}
	
	public String balancePrint(String account,
			String avail,String actual,String user, String date,String refNo) {
		String format = "";
		format = header();
		format = format + "Account No. "+account+"\n";
		format = format + "-------------------------------\n";
		format = format + "Available bal:" + " " + avail+" KSH\n";
		format = format + "Actual bal   :" + " " + actual+" KSH\n";
		format = format + "-------------------------------\n";
		format = format + "Ref No .    "+refNo+"\n";
		format = format + "-------------------------------\n";
		format = format + "Served By:  "+user+"\n \n";
		format = format + "Signature ................\n";
		
		format = format + "  "+date+"\n";
		format = format +footer();
		return format;

	}
	
	
	
	public String miniPrint(String account,String mini,String user,String refNo) {
		final Utility util = new Utility();
			Vector<String> minis = new Vector<String>();
			minis = new Vector<String>(Arrays.asList(mini.split("x")));
			Iterator itr = minis.iterator();
			String date = null;
			String hold="";
			String str;
			String format ="";
			format += header();
			format = format + "Account No. "+account+"\n";
			format = format + "-------------------------------\n";
			format +="Date       Type    KSH\n";
			while (itr.hasNext()) {
				str = ((String) itr.next());
				if (str.contains("*")) {
					int type = nthOccurrence(str, '*',0 )+1;
					int amount = nthOccurrence(str, '*', 1) + 1;
					format += str.substring(0,str.indexOf("*"))
							+ " "
							+  str.substring(type).substring(0,str.substring(type).indexOf("*"))
							+"    "
					+util.amountFormat((str.substring(amount).substring(0,
							str.substring(amount).indexOf("*")).substring(0, str.substring(amount).substring(0,
							str.substring(amount).indexOf("*")).length()-3)))
							+(str.substring(amount).substring(0,
									str.substring(amount).indexOf("*")).substring(str.substring(amount).substring(0,
											str.substring(amount).indexOf("*")).length()-3))+"\n";
					
				}
				else{
					date =str;
				}
			}
			format = format + "-------------------------------\n";
			format += "(CR-Deposit) (DR-Withdrawal)\n";
			format = format + "-------------------------------\n";
			format = format + "Ref No .    "+refNo+"\n";
			format = format + "-------------------------------\n";
			format = format + "Served By:  "+user+"\n \n";
			format = format + "Signature ................\n";
			
			format = format + "  "+date+"\n";
			format = format +footer();
			
			return format;
		}
	
	
	
	public static int nthOccurrence(String str, char c, int n) {
	    int pos = str.indexOf(c, 0);
	    while (n-- > 0 && pos != -1)
	        pos = str.indexOf(c, pos+1);
	    return pos;
	}
	
	
	
	public String withdrawPrint(String account, String amount,
			String avail,String actual,String user, String date,String refNo) {
		String format = "";
		format = header();
		format = format + "Account No. "+account+"\n";
		format = format + "-------------------------------\n";
		format = format + "Withdrawn:" + "     " + amount+" KSH\n";
		format = format + "-------------------------------\n";
		format = format + "Ref No .    "+refNo+"\n";
		format = format + "-------------------------------\n";
		format = format + "Served By:  "+user+"\n \n";
		format = format + "Customer Sign ................\n";
		format = format + "          Agent Copy\n";
		format = format + "  "+date+"\n";
		format = format +footer();
		return format;

	}
	
	public String withdrawPrint2(String account, String amount,
			String avail,String actual,String user, String date,String refNo) {
		String format = "";
		format = header();
		format = format + "Account No. "+account+"\n";
		format = format + "-------------------------------\n";
		format = format + "Withdrawn:" + "     " + amount+" KSH\n";
		format = format + "Available bal:" + " " + avail+" KSH\n";
		format = format + "Actual bal:" + "    " + actual+" KSH\n";
		format = format + "-------------------------------\n";
		format = format + "Ref No .    "+refNo+"\n";
		format = format + "-------------------------------\n";
		format = format + "Served By:  "+user+"\n \n";
		format = format + "Agent Sign ................\n";
		format = format + "          Customer copy\n";
		format = format + "  "+date+"\n";
		format = format +footer();
		return format;

	}
	
	
	public static String testPrint(String user) {
		Utility utility = new Utility();
		String format = "";
		format = header();
		format = format + "Tested by: "+user+"\n";
		format = format + "-------------------------------\n";
		format = format + "Ref: "+utility.refNo()+"\n \n";
		format = format +footer();
		return format;

	}

}
