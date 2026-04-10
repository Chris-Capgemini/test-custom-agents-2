package com.poc.api.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

/**
 * JUnit Platform Suite entry point for running all Cucumber scenarios.
 * Use {@code mvn test} to execute the full suite, or pass
 * {@code -Dcucumber.filter.tags} on the command line to run a subset.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty,html:target/cucumber-reports/cucumber.html,json:target/cucumber-reports/cucumber.json")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.poc.api.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @ignored")
public class CucumberTestRunner {
}
