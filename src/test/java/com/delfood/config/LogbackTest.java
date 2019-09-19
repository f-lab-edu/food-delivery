package com.delfood.config;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
@RunWith(SpringRunner.class)
public class LogbackTest {

	@Test
	public void logLevelTest() {
		log.trace("Hello world. Trace Level");
	    log.debug("Hello world. Debug Level"); 
	    log.info("Hello world. Info Level");
	    log.warn("Hello world. Warn Level");
	    log.error("Hello world. Error Level");
	}

}
