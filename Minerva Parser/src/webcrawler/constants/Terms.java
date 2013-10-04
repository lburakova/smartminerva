package webcrawler.constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Terms {
	private static int year;
	private static int month;
	private static List<String> terms = new ArrayList<String>();;
	private static List<String> literalTerms = new ArrayList<String>();

	/**
	 * Add new Terms Remove Past Terms
	 */
	public static void updateTerms() {
		// TODO - Update to only accept EST time zone.
		Calendar c = Calendar.getInstance();
		// Current date
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);

		// Currently Winter
		if (month < 4) {
			// Get Strings for each upcoming semester code
			// ie: 201401 for Winter 2014
			terms.add(year + "0" + 5);
			terms.add(year + "0" + 9);
			literalTerms.add("SUMMER_" + year);
			literalTerms.add("FALL_" + year);
		} else
		// Currently Summer
		if (month < 8) {
			terms.add(year + "0" + 5);
			terms.add(year + "0" + 9);
			terms.add((year + 1) + "0" + 1);
			literalTerms.add("SUMMER_" + year);
			literalTerms.add("FALL_" + year);
			literalTerms.add("WINTER_" + (year + 1));
		}
		// Currently Fall
		else {
			terms.add(year + "0" + 9);
			terms.add((year + 1) + "0" + 1);
			literalTerms.add("FALL_" + year);
			literalTerms.add("WINTER_" + (year + 1));
		}

	}

	public static List<String> getTerms() {
		return terms;
	}

	public static List<String> getLiteralTerms() {
		return literalTerms;
	}

}
