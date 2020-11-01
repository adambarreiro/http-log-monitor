package com.adambarreiro.monitor.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ConfigurationContainerTest {

	private ConfigurationContainer configurationContainer;

	@BeforeEach
	public void setup() {
		configurationContainer = ConfigurationContainer.getInstance();
	}

	@Test
	@DisplayName("Overriding a valid configuration option updates its value")
	public void overridingAValidConfigurationOptionUpdatesItsValueTest() {
		configurationContainer.add("-logFile", "myLog.log");
		Assertions.assertEquals("myLog.log", configurationContainer.getLogfile());
	}

	@Test
	@DisplayName("An empty set of arguments just leaves the default values")
	public void anEmptySetOfArgumentsJustLeavesTheDefaultValuesTest() {
		configurationContainer.add();
		Assertions.assertEquals("/tmp/access.log", configurationContainer.getLogfile());
		Assertions.assertEquals(120, configurationContainer.getAlertIntervalSeconds());
		Assertions.assertEquals(10.0f, configurationContainer.getRequestRateAlertThreshold());
		Assertions.assertEquals(10, configurationContainer.getScheduleIntervalSeconds());
	}

	@Test
	@DisplayName("Inserting an invalid option does nothing")
	public void insertingAnInvalidOptionDoesNothingTest() throws Exception {
		Field propertiesField = configurationContainer.getClass().getDeclaredField("properties");
		propertiesField.setAccessible(true);
		Object propertiesInstance = propertiesField.get(configurationContainer);
		Method propertiesSizeMethod = propertiesInstance.getClass().getDeclaredMethod("size");
		configurationContainer.add();
		int size1 = (int) propertiesSizeMethod.invoke(propertiesInstance);
		configurationContainer.add("-foo", "bar");
		configurationContainer.add("notAnOption", "buzz");
		int size2 = (int) propertiesSizeMethod.invoke(propertiesInstance);
		Assertions.assertEquals(size1, size2);
	}

}
