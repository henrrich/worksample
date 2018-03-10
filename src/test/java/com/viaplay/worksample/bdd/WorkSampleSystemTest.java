package com.viaplay.worksample.bdd;

import com.viaplay.worksample.WorksampleApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = WorksampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
public class WorkSampleSystemTest {

    @Autowired
    protected TestRestTemplate restTemplate;
}
