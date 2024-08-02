package com.chloe.auth;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestProcess {
    @Autowired
    private RepositoryService repositoryService;

    @Test
    public void deploy() {
        Deployment deploy = repositoryService.createDeployment()
                .name("请假流程")
                .addClasspathResource("process-demo/qingjia.bpmn20.xml")
                .addClasspathResource("process-demo/qingjia.png")
                .deploy();

        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
}
