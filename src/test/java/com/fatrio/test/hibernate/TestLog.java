package com.fatrio.test.hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.fatrio.main.hibernate.Person;

public class TestLog {
	
	private static final Logger logger = LogManager.getLogger(TestLog.class.getName());
	
	@Test
	public void testLogger() {				
		logger.fatal("fatal!!");
		logger.error("error!!");
		logger.warn("warn!!");
		logger.info("info!!");
		logger.debug("debug!!");
		logger.trace("trace!!");
	}
	
	@Test
	public void testLoggerWithParameters() {
		Person person = new Person();
		person.setEmail("p1@gmail.com");
		person.setFirstName("p1.firstName");
		person.setLastName("p1.lastName");
		
		logger.info("The person: {}", person);
	}

}
