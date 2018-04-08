package pl.edu.agh.ki.mwo.persistence;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import pl.edu.agh.ki.mwo.model.School;
import pl.edu.agh.ki.mwo.model.SchoolClass;

public class DatabaseConnector {
	
	protected static DatabaseConnector instance = null;
	
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
		String hql = "FROM SchoolClass S WHERE S.id=" + schoolClassId;
		Query query = session.createQuery(hql);
		List<SchoolClass> results = query.list();
		Transaction transaction = session.beginTransaction();
		for (SchoolClass s : results) {
			session.delete(s);
		}
		transaction.commit();
	}
	
	public void addSchoolClass(SchoolClass schoolClass) {
		Transaction transaction = session.beginTransaction();
		session.save(schoolClass);
		transaction.commit();
	}

}
