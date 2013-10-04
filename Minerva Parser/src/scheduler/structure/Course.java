package scheduler.structure;

import java.util.ArrayList;

/**
 * 
 * @author danielle
 * 
 *         Class is a whole CRN in one. Every section has a unique CRN and every
 *         meeting in a CRN must be taken togeter.
 * 
 */

public class Course {

	private String courseName;
	private ArrayList<SectionType> section = new ArrayList<SectionType>();

	public Course(String c) {
		courseName = c;
	}

	public ArrayList<SectionType> getSectionType() {
		return section;
	}

	public void addSectionType(SectionType s) {
		section.add(s);
	}

	public String getCourseName() {
		return courseName;
	}

	@Override
	public String toString() {
		String s = courseName + "\n\t";
		for (SectionType sect : section){
			s = s + sect + "\n\t";
		}
		return s;
	}

}
