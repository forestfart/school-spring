package pl.edu.agh.ki.mwo.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pl.edu.agh.ki.mwo.model.Student;
import pl.edu.agh.ki.mwo.persistence.DatabaseConnector;

import javax.servlet.http.HttpSession;

@Controller
public class StudentsController {

    @RequestMapping(value="/Students")
    public String listStudents(Model model, HttpSession session) {
    	if (session.getAttribute("userLogin") == null)
    		return "redirect:/Login";

    	model.addAttribute("students", DatabaseConnector.getInstance().getStudents());
    	
        return "studentsList";
    }
    
    @RequestMapping(value="/CreateStudent", method=RequestMethod.POST)
	public String createStudent(@RequestParam(value="studentName", required = true) String studentName,
									@RequestParam(value="studentSurname", required = true) String studentSurname,
									@RequestParam(value = "studentPesel", required = true) String studentPesel,
									@RequestParam(value = "studentSchoolClass", required = true) int studentSchoolClassId,
									Model model, HttpSession session) {
		if (session.getAttribute("userLogin") == null)
			return "redirect:/Login";

		Student student = new Student();
		student.setName(studentName);
		student.setSurname(studentSurname);
		student.setPesel(studentPesel);

		DatabaseConnector.getInstance().addStudent(student, studentSchoolClassId);
		model.addAttribute("students", DatabaseConnector.getInstance().getStudents());
		model.addAttribute("message", "Nowa student został dodany");

		return "studentsList";
	}
    
    @RequestMapping(value="/DeleteStudent", method=RequestMethod.POST)
    public String deleteStudent(@RequestParam(value="studentId", required=true) String studentId,
    		Model model, HttpSession session) {    	
    	if (session.getAttribute("userLogin") == null)
    		return "redirect:/Login";
    	
    	DatabaseConnector.getInstance().deleteStudent(studentId);
       	model.addAttribute("students", DatabaseConnector.getInstance().getStudents());
    	model.addAttribute("message", "Student został usunięty");
         	
    	return "studentsList";
    }
    
    @RequestMapping(value="/AddStudent")
    public String displayAddStudentForm(Model model, HttpSession session) {
    	if (session.getAttribute("userLogin") == null)
    		return "redirect:/Login";

		model.addAttribute("list", DatabaseConnector.getInstance().getClassesWithSchools());

        return "studentForm";
    }
}