package com.viaplay.worksample.bdd.runner;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/test/resources/bdd/features"},
        format = {"pretty", "html:target/reports/cucumber/html", "json:target/cucumber.json", "junit:target/junit.xml"},
        glue = {"com.viaplay.worksample.bdd.glue"},
        monochrome = true)
public class CucumberTest {
}
