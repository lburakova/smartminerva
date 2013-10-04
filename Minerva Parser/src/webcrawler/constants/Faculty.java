package webcrawler.constants;

/**
 * 
 * @author Michael
 *
 */
public enum Faculty {
	Desautels_Faculty_Management("Desautels Faculty of Management","MG"),
	Faculty_of_Agric_Environ_Sci("Faculty of Agricultural and Environmental Sciences","AG"),
	Faculty_of_Arts("Faculty of Arts","AR"),
	Faculty_of_Dentistry("Faculty of Dentistry","DE"),
	Faculty_of_Education("Faculty of Education","ED"),
	Faculty_of_Engineering("Faculty of Engineering","EN"),
	Faculty_of_Law("Faculty of Law","LW"),
	Faculty_of_Medicine("Faculty of Medicine","MD"),
	Faculty_of_Religious_Studies("Faculty of Religious Studies","RS"),
	Faculty_of_Science("Faculty of Science","SC"),
	Graduate_Studies("Graduate Studies","GR"),
	Ingram_School_of_Nursing("Ingram School of Nursing","NU"),
	Post_Grad_Medicine_Dentistry("Post-Grad Medicine & Dentistry","GM"),
	Postdoctoral_Fellows("Postdoctoral Fellows","PD"),
	School_of_Continuing_Studies("School of Continuing Studies","CE"),
	School_of_Phys_Occ_Therapy("School of Physical and Occupational Therapy","PQ"),
	Schulich_School_of_Music("Schulich School of Music","MU");
	
	private final String value;
	private final String name;
	
	
	private Faculty(String name, String value){
		this.value = value;
		this.name = name;
	}
	
	/**
	 * @return The two letter school/faculty code.
	 */
	public String value(){
		return value;
	}
	
	/**
	 * @return The name of the school/faculty in plaintext.
	 */
	public String getName(){
		return name;
	}
	
	public String toString(){
		return name + ":" + value;
	}
}
