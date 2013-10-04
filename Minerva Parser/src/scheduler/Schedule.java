package scheduler;

import java.util.ArrayList;
import java.util.List;

import scheduler.structure.Meeting;
import scheduler.structure.Section;

/**
 * 
 * @author danielle
 * 
 *         This class is the whole weekly schedule, with time slots. The output
 *         of the main 2D array is what the user will see.Ø
 */

public class Schedule {

	private int[][] classes = new int[7][48];
	private ArrayList<Integer> allCrnNumbers = new ArrayList<Integer>();
	private ArrayList<String> allCourseNames = new ArrayList<String>();

	/**
	 * Create an empty weekly schedule
	 */
	public Schedule() {
		// Instantiate an empty schedule, for each empty day.Ø
	}

	public Schedule(Schedule w) {
		copyArray(w.getClasses());
		allCrnNumbers = new ArrayList<Integer>();
		allCrnNumbers.addAll(w.getCRNs());
		allCourseNames = new ArrayList<String>();
		allCourseNames.addAll(w.getAllCourseNames());
	}

	private void copyArray(int[][] a) {
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 48; j++)
				classes[i][j] = a[i][j];
	}

	public int[][] getClasses() {
		return classes;
	}

	public boolean isEmpty() {
		return allCrnNumbers.isEmpty();
	}

	public ArrayList<Integer> getCRNs() {
		return allCrnNumbers;
	}

	public void addAllSections(ArrayList<Section> sections) {
		for (Section mSection : sections) {
			addSection(mSection);
		}
	}

	public void addSection(Section mSection) {
		for (Meeting meeting : mSection) {
			// try to add meeting to schedule, if space unavailable
			// throw course conflict.
			String days = meeting.getDays();
			int start = meeting.getStart();
			int end = meeting.getEnd();
			System.out.println(days + " " + start + " " + end);
			for (int i = 0; i < 7; i++) {
				if (days.charAt(i) == '1') {
					for (int j = start; j <= end; j++) {
						if (classes[i][j] == 0) {
							classes[i][j] = mSection.getCrn();

						} else {
							System.err
									.println("Your course overlaps with an existing course!");
						}

					}
				}
			}

		}

		allCourseNames.add(mSection.getCourseName());
		allCrnNumbers.add(mSection.getCrn());
	}

	public ArrayList<String> getAllCourseNames() {
		return allCourseNames;
	}

	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

	public boolean equals(Schedule w){
		return w.allCrnNumbers.equals(this.allCrnNumbers);
	}
}
