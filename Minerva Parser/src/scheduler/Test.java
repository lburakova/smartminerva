package scheduler;

import java.util.Scanner;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Database.initialize();

		Database.setSemester("201309");

		ScheduleManager.init();

		Scanner input = new Scanner(System.in);
		String str = null;
		while (!str.equals("stop")) {
				str = input.nextLine();
				ScheduleManager.addCourse(str);
		}
		input.close();
	}
}
