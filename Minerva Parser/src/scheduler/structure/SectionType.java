package scheduler.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SectionType implements Iterable<Section> {

	private List<Section> sections = new ArrayList<Section>();
	private SectionType next = null;

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @param type
	 */
	public SectionType(String t) {
		type = t;
	}

	public void addSection(Section s) {
		sections.add(s);
	}

	@Override
	public Iterator<Section> iterator() {
		return sections.iterator();
	}

	@Override
	public String toString() {
		String s = type + "\n\t\t";
		for (Section mSection : sections) {
			s = s + mSection + "\n\t\t";
		}

		if (next != null)
			s = s + "\n\t" + next;

		return s;
	}

}
