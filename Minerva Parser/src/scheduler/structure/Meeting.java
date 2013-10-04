package scheduler.structure;

/**
 * 
 * @author danielle A meeting is a class time, tutorial time, etc etc. It
 *         represents the shortest continuous time of class. Has integer
 *         properties start, end, crn. String property day.
 * 
 *         We can use this space as a changelog. I made some changes; I got rid
 *         of the setters, we'll recreate meetings where needed, if these are
 *         modified it could result in errors. Although reusing objs is much
 *         more efficient, we'll worry about that later. I added @Override to
 *         the toString method. I added a constructor for scheduling times which
 *         aren't necessarily courses. -MG.
 */

public class Meeting {
	private int start;
	private int end;
	private String days;

	/**
	 * Default constructor
	 * 
	 * @param crnNum
	 * @param day
	 * @param s
	 * @param e
	 */
	public Meeting(String day, int s, int e) {
		start = s;
		end = e;
		days = day;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getDays() {
		return days;
	}

	@Override
	public String toString() {
		return "Days: " + days + " Start: " + start + " End: " + end;
	}

}
