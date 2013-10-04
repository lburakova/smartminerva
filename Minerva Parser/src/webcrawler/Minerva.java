package webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import webcrawler.constants.Faculty;
import webcrawler.constants.PostValues;
import webcrawler.constants.Terms;
import webcrawler.formatters.Format;
import au.com.bytecode.opencsv.CSVParser;

/**
 * 
 * @author Michael
 * 
 */
public class Minerva {

	// Class Constants
	private static final String DOCUMENT_URI = "https://horizon.mcgill.ca/rm-PBAN1/bwckgens.csv";
	private static String currentTerm = null;

	private static Statement statement;
	private static PreparedStatement p;
	private static Connection connection = null;

	private static long totalDownloadTime = 0;

	public static void main(String[] args) {
		long time = System.currentTimeMillis();

		Terms.updateTerms();

		try {
			initialize();
			for (String term : Terms.getTerms()) {
				currentTerm = term;
				createTable();

				writeCoursesToDatabase();

				statement.execute("DELETE FROM COURSES_" + currentTerm
						+ " WHERE CRN='null'");

			}

			closeDB();
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Operation Complete: Total: "
				+ (System.currentTimeMillis() - time) + " ms. \nDownloading: "
				+ totalDownloadTime);
	}

	/**
	 * Download all courses and write to database.
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws SQLException
	 */
	private static void writeCoursesToDatabase()
			throws ClientProtocolException, IOException, SQLException {

		// Create a new Http Client
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(DOCUMENT_URI);
		HttpResponse response;
		HttpEntity entity;

		System.out.println(currentTerm);
		BufferedReader pInput = null;
		List<NameValuePair> defaults;

		for (Faculty faculty : Faculty.values()) {
			long time = System.currentTimeMillis();
			// Faculty faculty = Faculty.Desautels_Faculty_Management;
			defaults = PostValues.getValues(faculty.value(), currentTerm);
			// Set up request
			post.setEntity(new UrlEncodedFormEntity(defaults, Consts.UTF_8));
			// Send the request
			response = client.execute(post);
			// Get the CSV response
			entity = response.getEntity();
			totalDownloadTime += (System.currentTimeMillis() - time);
			// CRN,Subject,Course,Section,Type,Credits,Title,Days,TimeStart,
			// TimeEnd,Instructor,MStart, DStart, MEnd, DEnd,Location
			pInput = new BufferedReader(new InputStreamReader(
					entity.getContent()));

			String[] entries = new String[16];
			String line = null;
			CSVParser parser = new CSVParser();
			String[] T = null;

			while ((line = pInput.readLine()) != null) {
				// Filter lines
				T = lineFilter(parser.parseLine(line), entries);
				// Insert into database
				insert(currentTerm, T);
			}

			// Proof of response.
			System.out.println(faculty.getName());
		}
		pInput.close();
		disjoinTimePeriods();
	}

	public static void initialize() throws SQLException {
		// Get db drivers
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// Connect database
		try {
			connection = DriverManager
					.getConnection("jdbc:sqlite:assets\\Courses" + ".sqlite");
			// Enable Transaction Grouping
			connection.setAutoCommit(false);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
	}

	public static void createTable() throws SQLException {
		// Destroy any existing table.
		statement.executeUpdate("DROP TABLE IF EXISTS COURSES_" + currentTerm
				+ ";");

		statement
				.executeUpdate("CREATE TABLE COURSES_"
						+ currentTerm
						+ " (T_ID INTEGER PRIMARY KEY AUTOINCREMENT,CRN INT,"
						+ "SUBJECT TEXT,"
						+ "COURSE BLOB,SECTION INT,"
						+ "TYPE TEXT,CREDITS INT,TITLE TEXT,"
						+ "DAYS TEXT,TIMESTART INT,TIMEEND INT,INSTRUCTOR TEXT,MONTHSTART,DAYSTART,MONTHEND,DAYEND,"
						+ "LOCATION TEXT);");

		statement.executeUpdate("DROP TABLE IF EXISTS TIMES_" + currentTerm
				+ ";");
		statement
				.executeUpdate("CREATE TABLE TIMES_"
						+ currentTerm
						+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, DAYS, TIMESTART, TIMEEND, MONTHSTART, DAYSTART, MONTHEND, DAYEND); ");

	}

	/**
	 * Take a table of columns: CRN, SUBJECT, COURSE, SECTION, TYPE, CREDITS,
	 * TITLE, DAYS, MONTHSTART, DAYSTART, MONTHEND, DAYEND, LOCATION and split
	 * into Course Info and a Times table. Course Info: CRN, SUBJECT, COURSE,
	 * SECTION, TYPE, CREDITS, TITLE, TIMEID, LOCATION Times: _id, DAYS,
	 * MONTHSTART, DAYSTART, MONTHEND, DAYEND
	 * 
	 * @throws SQLException
	 */
	private static void disjoinTimePeriods() throws SQLException {
		Statement s = connection.createStatement();

		s.addBatch("INSERT INTO TIMES_"
				+ currentTerm
				+ " (DAYS,TIMESTART,TIMEEND,MONTHSTART,DAYSTART,MONTHEND,DAYEND) SELECT DAYS,TIMESTART,TIMEEND,MONTHSTART,DAYSTART,MONTHEND,DAYEND FROM COURSES_"
				+ currentTerm
				+ " GROUP BY DAYS,TIMESTART,TIMEEND,MONTHSTART,DAYSTART,MONTHEND,DAYEND; ");
		// For verification purposes only
		s.addBatch("DROP TABLE IF EXISTS TEMPCOURSES; ");
		s.addBatch("CREATE table TEMPCOURSES AS SELECT CRN,SUBJECT,COURSE,SECTION,TYPE,CREDITS,TITLE, INSTRUCTOR,_id AS TIMEID, LOCATION FROM COURSES_"
				+ currentTerm
				+ " JOIN "
				+ "TIMES_"
				+ currentTerm
				+ " ON "
				+ "COURSES_"
				+ currentTerm
				+ ".DAYS="
				+ "TIMES_"
				+ currentTerm
				+ ".DAYS AND "
				+ "COURSES_"
				+ currentTerm
				+ ".TIMESTART="
				+ "TIMES_"
				+ currentTerm
				+ ".TIMESTART AND "
				+ "COURSES_"
				+ currentTerm
				+ ".TIMEEND="
				+ "TIMES_"
				+ currentTerm
				+ ".TIMEEND AND "
				+ "COURSES_"
				+ currentTerm
				+ ".MONTHSTART="
				+ "TIMES_"
				+ currentTerm
				+ ".MONTHSTART AND "
				+ "COURSES_"
				+ currentTerm
				+ ".DAYSTART="
				+ "TIMES_"
				+ currentTerm
				+ ".DAYSTART AND "
				+ "COURSES_"
				+ currentTerm
				+ ".MONTHEND="
				+ "TIMES_"
				+ currentTerm
				+ ".MONTHEND AND "
				+ "COURSES_"
				+ currentTerm
				+ ".DAYEND="
				+ "TIMES_"
				+ currentTerm
				+ ".DAYEND; ");
		s.addBatch("DROP TABLE IF EXISTS COURSES_" + currentTerm + ";");
		s.addBatch("CREATE TABLE COURSES_" + currentTerm
				+ " AS SELECT * FROM TEMPCOURSES; ");
		s.addBatch("DROP TABLE IF EXISTS TEMPCOURSES; ");
		s.executeBatch();
		s.clearBatch();

	}

	/**
	 * Filter lines to match delimiters, and format them.
	 * 
	 * @param T
	 * @param entries
	 * @return Properly formatted database entry.
	 */
	private static String[] lineFilter(String[] T, String[] entries) {
		// Look for CRN number between 1 and 5 digits
		if (T[0].matches("\\d{1,5}")) {
			// Course Info
			// Get proper Date/Time formating
			String[] timeInterval = Format.timeFormat(T[8]);
			String[] dateInterval = Format.dateFormat(T[14]);
			// Completely new row.
			entries = new String[] { T[0], T[1], T[2], T[3], T[4], T[5], T[6],
					Format.dayFormat(T[7]), timeInterval[0], timeInterval[1],
					tbaCheck(T[13]), dateInterval[0], dateInterval[1],
					dateInterval[2], dateInterval[3], tbaCheck(T[15]) };
			return entries;
		} else
		// Section Title
		if (T[0].matches("[\\w]{3,}")) {
			// Ignore
			return null;
		} else
		// Look for nested optional sections
		if (T[0].isEmpty()) {
			// Append to last entry
			// Get proper Date/Time formating
			String[] timeInterval = Format.timeFormat(T[8]);
			String[] dateInterval = Format.dateFormat(T[14]);

			// Reuse information from last complete row.
			entries[7] = Format.dayFormat(T[7]);
			entries[8] = timeInterval[0];
			entries[9] = timeInterval[1];
			entries[10] = tbaCheck(T[13]);
			entries[11] = dateInterval[0];
			entries[12] = dateInterval[1];
			entries[13] = dateInterval[2];
			entries[14] = dateInterval[3];
			entries[15] = tbaCheck(T[15]);

			return entries;
		} else
		// Look for table headers, ignore them.
		if (T[0].contains("CRN")) {
			// Ignore
			return null;
		}
		return null;
	}

	/**
	 * Insert String[] into database.
	 * 
	 * @param entries
	 * @throws SQLException
	 */
	private static void insert(String term, String[] entries)
			throws SQLException {
		if (entries == null)
			return;

		// Prepare an INSERT statement update all fields except ID.
		p = connection.prepareStatement("INSERT INTO COURSES_" + currentTerm
				+ " VALUES (null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

		for (int i = 1; i <= 16; i++) {
			if (entries[i - 1] != null)
				p.setString(i, entries[i - 1]);
		}

		// Execute Statement
		p.execute();
	}

	private static String tbaCheck(String s) {
		if (s.equals("TBA"))
			return null;
		else
			return s;
	}

	public static void closeDB() throws SQLException {
		connection.commit();
		connection.setAutoCommit(true);
		connection.createStatement().executeUpdate("VACUUM");
		statement.close();
		connection.close();
	}
}
