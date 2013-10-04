package webcrawler.constants;

import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class PostValues {
	private final static NameValuePair[] DEFAULTS = new BasicNameValuePair[] {
			new BasicNameValuePair("sel_crse", ""),
			new BasicNameValuePair("sel_title", ""),
			new BasicNameValuePair("begin_hh", "0"),
			new BasicNameValuePair("begin_mi", "0"),
			new BasicNameValuePair("begin_ap", "a"),
			new BasicNameValuePair("end_hh", "0"),
			new BasicNameValuePair("end_mi", "0"),
			new BasicNameValuePair("end_ap", "a"),
			new BasicNameValuePair("sel_dunt_code", ""),
			new BasicNameValuePair("sel_dunt_unit", ""),
			new BasicNameValuePair("sel_from_cred", ""),
			new BasicNameValuePair("sel_to_cred", ""),
			// Faculty Selector
			null,
			new BasicNameValuePair("call_value_in", "UNSECURED"),
			new BasicNameValuePair("display_mode_in", "LIST"),
			new BasicNameValuePair("search_mode_in", ""),
			// Changing terms
			null,
			new BasicNameValuePair("sel_subj", "dummy"),
			new BasicNameValuePair("sel_day", "dummy"),
			new BasicNameValuePair("sel_ptrm", "dummy"),
			new BasicNameValuePair("sel_ptrm", "%"),
			new BasicNameValuePair("sel_camp", "dummy"),
			new BasicNameValuePair("sel_schd", "dummy"),
			new BasicNameValuePair("sel_schd", "%"),
			new BasicNameValuePair("sel_sess", "dummy"),
			new BasicNameValuePair("sel_instr", "dummy"),
			new BasicNameValuePair("sel_instr", "%"),
			new BasicNameValuePair("sel_attr", "dummy"),
			new BasicNameValuePair("sel_attr", "%"),
			new BasicNameValuePair("crn", "dummy"),
			new BasicNameValuePair("rsts", "dummy"),
			new BasicNameValuePair("sel_levl", "dummy"),
			new BasicNameValuePair("sel_levl", "%"),
			new BasicNameValuePair("sel_insm", "dummy") };

	public static List<NameValuePair> getValues(String facultyCode, String termCode) {
		// Array Indexes 12,16
		DEFAULTS[12] = 	new BasicNameValuePair("sel_coll", facultyCode);
		DEFAULTS[16] = new BasicNameValuePair("term_in", termCode);
				
		return Arrays.asList(DEFAULTS);
	}

}
