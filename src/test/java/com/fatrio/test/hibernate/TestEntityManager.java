package com.fatrio.test.hibernate;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.Test;

import com.fatrio.main.hibernate.Person;

public class TestEntityManager {
	
	static EntityManagerFactory factory = Persistence.createEntityManagerFactory( "com.fatrio.test.hibernate" );

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
		
		System.out.println(person);
	}
	
	@Test
	public void testSaveSeveralPeople() {
		EntityManager manager = factory.createEntityManager();
		manager.setFlushMode(FlushModeType.COMMIT);
		
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
		manager.persist(p1);
		manager.persist(p2);
		manager.persist(p3);

		System.out.println("* Before closing transaction:");
		for (Person p : Arrays.asList(p1, p2, p3)) {
			System.out.println(p);
		}
		
		manager.getTransaction().commit();
		
		System.out.println("* After closing transaction:");
		for (Person p : Arrays.asList(p1, p2, p3)) {
			System.out.println(p);
		}
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
