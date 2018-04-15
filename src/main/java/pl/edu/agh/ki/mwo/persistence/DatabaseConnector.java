package pl.edu.agh.ki.mwo.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.agh.ki.mwo.model.School;
import pl.edu.agh.ki.mwo.model.SchoolClass;
import pl.edu.agh.ki.mwo.model.Student;

public class DatabaseConnector {
	protected static DatabaseConnector instance = null;
	final Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);
	
	public static DatabaseConnector getInstance() {
		if (instance == null) {
			instance = new DatabaseConnector();
		}
		return instance;
	}
	Session session;

	protected DatabaseConnector() {
		session = HibernateUtil.getSessionFactory().openSession();
	}
	
	public void teardown() {
		session.close();
		HibernateUtil.shutdown();
		instance = null;
	}
	
	public Iterable<School> getSchools() {
		String hql = "FROM School";
		Query query = session.createQuery(hql);
		List schools = query.list();
		return schools;
	}
	
	public void addSchool(School school) {
		Transaction transaction = session.beginTransaction();
		session.save(school);
		transaction.commit();
	}
	
	public void deleteSchool(String schoolId) {
		String hql = "FROM School S WHERE S.id=" + schoolId;
		Query query = session.createQuery(hql);
		List<School> results = query.list();
		Transaction transaction = session.beginTransaction();
		for (School s : results) {
			session.delete(s);
		}
		transaction.commit();
	}
	
	public Iterable<SchoolClass> getSchoolClasses() {
		String hql = "FROM SchoolClass";
		Query query = session.createQuery(hql);
		List schoolClasses = query.list();
		return schoolClasses;
	}
	
	public void deleteSchoolClass(String schoolClassId) {
		String hql = "SELECT s FROM School s INNER JOIN s.classes classes WHERE classes.id = " + schoolClassId;
		Query query = session.createQuery(hql);
		School school = (School)query.uniqueResult();
		SchoolClass schoolClass = school.getClasses().stream().filter(n->n.getId()==Long.parseLong(schoolClassId)).findFirst().get();
		if (schoolClass != null) {
			school.getClasses().remove(schoolClass);
			Transaction transaction = session.beginTransaction();
			session.save(school);
			session.delete(schoolClass);
			transaction.commit();
			logger.info(String.format("SchoolClass %d %s removed from the database", schoolClass.getId(), schoolClass.getProfile()));
		} else {
			logger.info("failed attempt to remove schoolClass");
		}
	}
	
	public void addSchoolClass(SchoolClass schoolClass, int schoolId) {
		String hql = "FROM School S WHERE S.id=" + schoolId;
		Query query = session.createQuery(hql);
		School school = (School)query.uniqueResult();
		school.getClasses().add(schoolClass);
		Transaction transaction = session.beginTransaction();
		session.save(school);
		transaction.commit();
	}

	public Iterable<Student> getStudents() {
		String hql = "FROM Student";
		Query query = session.createQuery(hql);
		List students = query.list();
		return students;
	}

	public void deleteStudent(String studentId) {
		String hql = "SELECT s FROM SchoolClass s INNER JOIN s.students students WHERE students.id = " + studentId;
		Query query = session.createQuery(hql);
		SchoolClass schoolClass = (SchoolClass) query.uniqueResult();
		Student student = schoolClass.getStudents().stream().filter(n->n.getId()==Long.parseLong(studentId)).findFirst().get();
		if (student != null) {
			schoolClass.getStudents().remove(student);
			Transaction transaction = session.beginTransaction();
			session.save(schoolClass);
			session.delete(student);
			transaction.commit();
			logger.info(String.format("Student %d %s %s removed from the database", student.getId(), student.getName(), student.getSurname()));
		} else {
			logger.info("failed attempt to remove student");
		}
	}

	public void addStudent(Student student, int schoolClassId) {
		String hql = "FROM SchoolClass S WHERE S.id=" + schoolClassId;
		Query query = session.createQuery(hql);
		SchoolClass schoolClass = (SchoolClass)query.uniqueResult();
		schoolClass.getStudents().add(student);
		Transaction transaction = session.beginTransaction();
		session.save(schoolClass);
		transaction.commit();
	}

	public Map<Object, Object> getClassesWithSchools() {
		String hql = "select distinct concat(c.profile, ' na ', s.name) from School s inner join s.classes c";
		String hqlId = "select distinct c.id from School s inner join s.classes c";
		Query query = session.createQuery(hql);
		Query queryId = session.createQuery(hqlId);
		List schoolClassList = query.list();
		List schoolClassIdList = queryId.list();
		Map<Object, Object> combinedSchoolClassList = new HashMap<>();
		for (int i=0; i < schoolClassList.size(); i++) {
			combinedSchoolClassList.put(schoolClassIdList.get(i), schoolClassList.get(i));
		}
		return combinedSchoolClassList;
	}
}
