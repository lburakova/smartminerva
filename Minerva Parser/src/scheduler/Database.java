package scheduler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import scheduler.structure.Course;
import scheduler.structure.Meeting;
import scheduler.structure.Section;
import scheduler.structure.SectionType;

public class Database {

	private static Connection c = null;
	private static Statement statement = null;
	private static String semester = null;

	public static void initialize() {
		// Get db drivers
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// Connect database
		try {
			c = DriverManager.getConnection("jdbc:sqlite:assets\\Courses"
					+ ".sqlite");
			// Enable Transaction Grouping
			c.setAutoCommit(false);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			statement = c.createStatement();
			statement.setQueryTimeout(30);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// set timeout to 30 sec.
	}

	public static void setSemester(String sem) {
		semester = sem;
	}

	/**
	 * Use this to retrieve course CRNs.
	 * 
	 * @param courseName
	 *            - XXXX###
	 */
	public static Course getCourse(String courseName) {
		if (c == null)
			throw new RuntimeException("Database Not Initialized");

		courseName = courseName.toUpperCase();

		// COMP250
		if (courseName.matches("\\w{4}\\S{1,5}")) {
			Course course = getCourse(courseName.substring(0, 4),
					courseName.substring(4));
			return course;
		} else
		// Not Valid
		{
			System.out.println("Course Name not Valid");
			return null;
		}
	}

	public static ArrayList<ArrayList<Integer>> getAllCrn(String courseName) {
		if (c == null)
			throw new RuntimeException("Database Not Initialized");

		courseName = courseName.toUpperCase();

		// COMP250
		if (courseName.matches("\\w{4}\\S{1,5}")) {
			ArrayList<ArrayList<Integer>> course = getAllCrn(
					courseName.substring(0, 4), courseName.substring(4));
			return course;
		} else
		// Not Valid
		{
			System.out.println("Course Name not Valid");
			return null;
		}
	}

	private static ArrayList<ArrayList<Integer>> getAllCrn(String courseName,
			String subj) {
		ResultSet rs = null;
		try {

			rs = statement
					.executeQuery("SELECT CRN, SECTION, TYPE FROM COURSES_"
							+ semester + " JOIN TIMES_" + semester
							+ " ON TIMEID=_id WHERE SUBJECT='" + courseName
							+ "' AND COURSE='" + subj + "' ORDER BY TYPE;");

			String sectionName = "";
			ArrayList<ArrayList<Integer>> crnNumbers = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> rows = new ArrayList<Integer>();

			while (rs.next()) {
				if (!sectionName.equals(rs.getString(3))) {
					// Section Type
					sectionName = rs.getString(3);
					rows.add(rs.getInt(1));
				} else {
					crnNumbers.add(rows);
				}

			}
			return crnNumbers;
		} catch (SQLException | ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Course getCourse(String courseName, String subj) {
		System.out.println("Good: " + courseName + " " + subj);

		ResultSet rs = null;
		int position = -1;
		try {

			// Get number of different sections
			ResultSet numRows = statement
					.executeQuery("SELECT COUNT(TYPE) FROM (SELECT TYPE FROM COURSES_"
							+ semester
							+ " WHERE SUBJECT='"
							+ courseName
							+ "' AND COURSE='" + subj + "' GROUP BY TYPE);");
			numRows.next();

			rs = statement
					.executeQuery("SELECT CRN,SECTION,TYPE,CREDITS,DAYS,TIMESTART,TIMEEND FROM COURSES_"
							+ semester
							+ " JOIN TIMES_"
							+ semester
							+ " ON TIMEID=_id WHERE SUBJECT='"
							+ courseName
							+ "' AND COURSE='" + subj + "' ORDER BY TYPE;");

			Course mCourse = new Course(courseName + subj);
			SectionType mSectionType = null;
			Section mSection = null;
			int crn = -1;
			String sectionName = "";

			while (rs.next()) {

				if (!sectionName.equals(rs.getString(3))) {
					// Section Type
					sectionName = rs.getString(3);
					mSectionType = new SectionType(sectionName);
					mCourse.addSectionType(mSectionType);

				}

				if (crn != rs.getInt(1)) {
					crn = rs.getInt(1);
					mSection = new Section(crn,courseName+subj);
					mSectionType.addSection(mSection);
				}

				mSection.addMeetings(new Meeting(rs.getString(5),
						mapStartTime(rs.getInt(6)), mapEndTime(rs.getInt(7))));
			}
			return mCourse;
		} catch (SQLException | ArrayIndexOutOfBoundsException e) {
			System.err.print(position);
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<Section> getSections(String crnNumbers) {
		ResultSet rs = null;

		ArrayList<Section> mSections = new ArrayList<Section>();
		try {
			rs = statement
					.executeQuery("SELECT CRN,SUBJECT,COURSE,TYPE,DAYS,TIMESTART,TIMEEND FROM COURSES_"
							+ semester
							+ " JOIN TIMES_"
							+ semester
							+ " ON TIMEID=_id WHERE CRN IN " + crnNumbers + ";");

			Section mSection = null;
			int crnTemp = -1;

			while (rs.next()) {
				if (crnTemp != rs.getInt(1)) {
					crnTemp = rs.getInt(1);
					mSection = new Section(crnTemp,rs.getString(2)+rs.getString(3),rs.getString(4));
					mSections.add(mSection);
				}
				mSection.addMeetings(new Meeting(rs.getString(5),
						mapStartTime(rs.getInt(6)), mapEndTime(rs.getInt(7))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mSections;
	}

	private static double _5MINUTES_IN_SECS = 300;
	private static double SECONDS_PER_HOUR = 3600;

	/**
	 * Map a time in seconds to one of 48 indexes
	 * 
	 * @param time
	 */
	public static int mapStartTime(int time) {
		double t = time;
		// ie: 30900 == 8:35 ~ 8:30 = 8.5 == 17
		double a = t - _5MINUTES_IN_SECS;
		double b = a / SECONDS_PER_HOUR;
		int c = (int)(b * 2);

		return c;
	}

	/**
	 * Map a time in seconds to one of 48 indexes
	 * 
	 * @param time
	 */
	public static int mapEndTime(int time) {
		double t = time;
		// ie: 33900 + 300 == 9:25 + :05 = 9:30 = 9.5 == 19
		double a = t + _5MINUTES_IN_SECS;
		double b = a / SECONDS_PER_HOUR;
		int c = (int)(b * 2);
		return c;
		
	}

}
