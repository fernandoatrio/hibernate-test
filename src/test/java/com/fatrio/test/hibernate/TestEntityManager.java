package com.fatrio.test.hibernate;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.fatrio.main.hibernate.Person;

public class TestEntityManager {
	
	private static final Logger logger = LogManager.getLogger(TestEntityManager.class.getName());
	
	static EntityManagerFactory factory = Persistence.createEntityManagerFactory( "com.fatrio.test.hibernate.postgresql" );

	@Test
	public void testSelectOnePerson() {
		
		assertThat(factory, notNullValue());
		
		EntityManager entityManager = factory.createEntityManager();
		
		entityManager.getTransaction().begin();		
		CriteriaBuilder criteria = entityManager.getCriteriaBuilder();
		CriteriaQuery<Person> q = criteria.createQuery(Person.class);
		Root<Person> root = q.from(Person.class);
		q.where(criteria.equal(root.get("email"), "p1@gmail.com"));
		
		Person person = entityManager.createQuery(q).getSingleResult();
		
		entityManager.getTransaction().commit();
		
		logger.info(person);
	}

	@Test
	public void testGetPersonReference() {		
		assertThat(factory, notNullValue());
		
		EntityManager entityManager = factory.createEntityManager();
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
		Root<Person> root = query.from(Person.class);
		query = query.select(root.get("id")).where(builder.equal(root.get("email"), "p1@gmail.com"));
		TypedQuery<Integer> typedQuery = entityManager.createQuery(query);
		typedQuery.setFirstResult(0);
		
		Person person = entityManager.getReference(Person.class, typedQuery.getSingleResult());
		
		logger.info(person);
	}
	
	@Test
	public void testSaveSeveralPeople() {
		EntityManager manager = factory.createEntityManager();
		
		Person p1 = new Person();
		p1.setEmail("p1@gmail.com");
		p1.setFirstName("p1.firstName");
		p1.setLastName("p1.lastName");
		
		Person p2 = new Person();
		p2.setEmail("p2@gmail.com");
		p2.setFirstName("p2.firstName");
		p2.setLastName("p2.lastName");
		
		Person p3 = new Person();
		p3.setEmail("p3@gmail.com");
		p3.setFirstName("p3.firstName");
		p3.setLastName("p3.lastName");
		
		manager.getTransaction().begin();
		
		logger.info("* Before closing transaction:");
		List<Person> people = Arrays.asList(p1, p2, p3);
		for (Person person : people) {
			manager.persist(person);
		}
		
		for (Person person : people) {
			logger.info(person);
		}
		
		manager.getTransaction().commit();
		
		logger.info("* After closing transaction:");
		for (Person person : people) {
			logger.info(person);
		}
	}
	
	@Test
	public void testMerge() {
		EntityManager manager = factory.createEntityManager();
		
		Person person = new Person();
		person.setEmail("update.de@gmail.com");
		person.setFirstName("detached.firstName");
		person.setLastName("detached.lastName");
		
		manager.getTransaction().begin();		
		manager.persist(person);		
		logger.info("Attached person: {}", person);
		manager.getTransaction().commit();
		
		EntityManager manager2 = factory.createEntityManager();
		manager2.getTransaction().begin();
		Person detachedPerson = new Person();
		detachedPerson.setId(person.getId());
		detachedPerson.setEmail("UPDATED.DE@gmail.com");
		manager2.merge(detachedPerson);
		manager2.getTransaction().commit();
		
		EntityManager manager3 = factory.createEntityManager();
		logger.info("After merge: {}", manager3.find(Person.class, person.getId()));
	}
	
	@Test
	public void testDeleteAll() {
		EntityManager manager = factory.createEntityManager();
		manager.getTransaction().begin();
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaDelete<Person> delete = builder.createCriteriaDelete(Person.class);
		delete.from(Person.class);
		manager.createQuery(delete).executeUpdate();
		manager.getTransaction().commit();
	}
}
