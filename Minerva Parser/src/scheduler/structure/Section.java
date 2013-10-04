package scheduler.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Section implements Iterable<Meeting>{

	private List<Meeting> meetings = new ArrayList<Meeting>();
	private int crn;
	private String courseName;
	private String type;
		
	public Section(int s, String course){
		crn = s;
		setCourseName(course);
	}
	
	public Section(int s, String course, String type){
		crn = s;
		setCourseName(course);
		setType(type);
	}

	/**
	 * 
	 * @param type
	 * @param meetings
	 */
	public Section(String t, Meeting[] m) {
		meetings.addAll(Arrays.asList(m));
	}
	
	public List<Meeting> getMeetings(){
		return meetings;
	}
	
	public int getCrn(){
		return crn;
	}
	
	public void addMeetings(Meeting m){
		meetings.add(m);
	}
	
	@Override
	public Iterator<Meeting> iterator() {
		return meetings.iterator();
	}
	
	@Override
	public String toString(){
		String s = crn + "\n\t\t\t";
		for (Meeting meeting : meetings){
			s = s + meeting + "\n\t\t\t";
		}
		return s;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
