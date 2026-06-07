package venkatsai.taskflow.service.handler;

import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import venkatsai.taskflow.entity.JobEntity;
import venkatsai.taskflow.entity.JobType;

@Component
public class EmailHandler implements JobHandler{
    @Override
    public JobType getJobType() {
        return JobType.EMAIL;
    }

    @Override
    public void handle(JobEntity jobEntity) {
        JsonNode payLoad = new ObjectMapper().readTree(jobEntity.getPayLoad());
        System.out.println("Email Sent to "+payLoad.get("emailId"));
    }
}
