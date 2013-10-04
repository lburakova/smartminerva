package scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scheduler.structure.Course;
import scheduler.structure.Section;
import scheduler.structure.SectionType;

public class ScheduleManager {
	private static List<Schedule> schedules = new ArrayList<Schedule>();

	public static void init() {
		schedules.add(new Schedule());
	}

	public static void printAll() {
		for (Schedule w : schedules) {
			// Print each in a column
			printSchedules(w);
			// Separate each by three lines
			System.out.println("\n\n\n");
		}
	}

	public static void printSchedules(Schedule w) {
		int[][] schedule = w.getClasses();
		String str = "\t";
		
		System.out.println(w.getCRNs().toString() + "\n\t  U\tM\tT\tW\tR\tF\tS");

		int min = findMin(schedule);
		int max = findMax(schedule);

		int t = 0;
		for (int i = t = min; i < max; i++) {

			t = i / 2;
			str = t + ((i % 2 == 0) ? "" : ":30") + "\t" + " |";

			for (int j = 0; j < 7; j++) {
				str = str + (schedule[j][i] != 0 ? schedule[j][i]+"\t" : "\t");
			}
			System.out.println(str);

		}

	}

	private static int findMin(int[][] schedule) {
		for (int i = 0; i < 48; i++) {
			for (int j = 0; j < 7; j++) {
				if (schedule[j][i] != 0)
					return i;
			}
		}
		return 0;
	}

	private static int findMax(int[][] schedule) {
		for (int i = 47; i >= 0; i--) {
			for (int j = 0; j < 7; j++) {
				if (schedule[j][i] != 0)
					return i + 2;
			}
		}
		return 0;
	}

	public static void addCourse(String courseName) {
		Course c = Database.getCourse(courseName);
		System.out.println("addCourse");
		// Get different combinations of SectionTypes
		List<Integer[]> combinations = getCombinations(listCrn(c));
		ArrayList<Schedule> newList = new ArrayList<Schedule>();

		for (int i = 0; i<schedules.size(); i++) {
			newList.addAll(getNewCluster(schedules.remove(i), combinations));
		}
		schedules = newList;
	}

	private static ArrayList<Schedule> getNewCluster(Schedule schedule,
			List<Integer[]> combinations) {
		
		ArrayList<Schedule> scheduleTemp = new ArrayList<Schedule>();
		
		for (Integer[] ints : combinations) {
			
			Schedule tempSched = new Schedule(schedule);
			
			String strCombos = Arrays.toString(ints).replace('[', '(')
					.replace(']', ')');

			System.out.println("Combos " + strCombos);

			tempSched.addAllSections(Database.getSections(strCombos));
			
			scheduleTemp.add(tempSched);
		}

		return scheduleTemp;
	}
	
	public static void remove(Schedule w){
		schedules.remove(w);
	}

	private static ArrayList<ArrayList<Integer>> listCrn(Course c) {
		ArrayList<ArrayList<Integer>> crnNumbers = new ArrayList<ArrayList<Integer>>();

		for (SectionType mSectionType : c.getSectionType()) {
			ArrayList<Integer> T = new ArrayList<Integer>();
			for (Section mSection : mSectionType) {
				int tempCrn = mSection.getCrn();
				T.add(Integer.valueOf(tempCrn));
			}
			crnNumbers.add(T);
		}
		return crnNumbers;
	}

	private static List<Integer[]> getCombinations(
			ArrayList<ArrayList<Integer>> crnNumbers) {
		Map<Integer, ArrayList<Integer>> data = new LinkedHashMap<Integer, ArrayList<Integer>>();

		int index = 0;

		for (ArrayList<Integer> columns : crnNumbers) {
			data.put(index, columns);
			index++;
		}

		List<Integer[]> product = getCrossProduct(data);
		return product;
	}

	private static List<Integer[]> getCrossProduct(
			Map<Integer, ArrayList<Integer>> lists) {
		List<Integer[]> results = new ArrayList<Integer[]>();
		getCrossProduct(results, lists, 0, new Integer[(lists.size())]);
		return results;
	}

	private static void getCrossProduct(List<Integer[]> results,
			Map<Integer, ArrayList<Integer>> lists, int depth, Integer[] current) {
		for (int i = 0; i < lists.get(depth).size(); i++) {
			current[depth] = lists.get(depth).get(i);
			if (depth < lists.keySet().size() - 1)
				getCrossProduct(results, lists, depth + 1, current);
			else {
				results.add(Arrays.copyOf(current, current.length));
			}
		}
	}

}
