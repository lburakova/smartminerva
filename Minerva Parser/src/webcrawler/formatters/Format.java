package webcrawler.formatters;
/**
 * 
 * @author Michael
 *
 */
public class Format {
	/**
	 * 
	 * @param date
	 * @return A String[4] with each entry representing Starting Month, Starting Day, End Month, and End Day, respectively.
	 */
	public static String[] dateFormat(String date) {
		if(date.equals("-"))
			return new String[]{null,null,null,null};
		if (date.equals("TBA"))
			return new String[]{null,null,null,null};
		// MStart, DStart, MEnd, DEnd
		String T[] = date.split("[\\-\\/]");
		return new String[] { 
				T[0],
				T[1],
				T[2],
				T[3]};
	}

	/**
	 * 
	 * @param days
	 * @return A Binary String of 7 digits representing the days of the week where conditions apply or "TBA". Starts with Sunday.
	 */
	public static String dayFormat(String days) {
		char T[] = new char[] { '0', '0', '0', '0', '0', '0', '0' };
		if (days.equals("TBA"))
			return null;

		for (int i = 0; i < days.length(); i++) {
			switch (days.charAt(i)) {
			// Sunday to Saturday
			case 'U':
				T[0] = '1';
				break;
			case 'M':
				T[1] = '1';
				break;
			case 'T':
				T[2] = '1';
				break;
			case 'W':
				T[3] = '1';
				break;
			case 'R':
				T[4] = '1';
				break;
			case 'F':
				T[5] = '1';
				break;
			case 'S':
				T[6] = '1';
				break;
			}
		}
		return new String(T);
	}
	
	/**
	 * Format time to seconds from midnight.
	 * @param time
	 * @return The time in seconds.
	 */
	public static String[] timeFormat(String time){
		if (time.equals("TBA")){
			return new String[]{null, null};
		}
		String[] T = time.split("[-]");
		
		int start = toSeconds(T[0]);
		int end = toSeconds(T[1]);
		
		return new String[]{String.valueOf(start),String.valueOf(end)};
	}
	
	public static int toSeconds(String time){
		int t = 0;
		int hour = 0;
		int minute = 0;
		
		// Midday Offset
		if (time.contains("PM"))
			t+=(12*3600);
		if (time.contains("12:"))
			t-=(12*3600);
		// Remove AM/PM declaration
		time = time.split("[\\s]")[0];
		
		// Split into hours and minutes
		String T[] = time.split("[:]");
		
		// Return formatted as seconds
		hour = Integer.valueOf(T[0]).intValue();
		minute = Integer.valueOf(T[1]).intValue();
		
		return t + (hour * 3600) + (minute * 60);
	}
}
