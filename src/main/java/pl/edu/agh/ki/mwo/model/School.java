package pl.edu.agh.ki.mwo.model;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name="schools")
public class School implements java.io.Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	
	@Column
	private String name;
	
	@Column
	private String address;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="school_id")
	private Set<SchoolClass> classes;

	public School() {
		classes = new HashSet<SchoolClass>();
	}

	public void addClass(SchoolClass newClass) {
		classes.add(newClass);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setClasses(Set<SchoolClass> classes) {
		this.classes = classes;
	}

	public Set<SchoolClass> getClasses() {
		return classes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public String toString() {
		return "School: " + getName() + " (" + getAddress() + ", " + getClasses().size() + " classes)";
}

}
