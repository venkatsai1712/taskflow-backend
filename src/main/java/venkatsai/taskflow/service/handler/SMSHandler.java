package venkatsai.taskflow.service.handler;

import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import venkatsai.taskflow.entity.JobEntity;
import venkatsai.taskflow.entity.JobType;

@Component
public class SMSHandler implements JobHandler{
    @Override
    public JobType getJobType() {
        return JobType.SMS;
    }

    @Override
    public void handle(JobEntity jobEntity) {
        JsonNode payLoad = new ObjectMapper().readTree(jobEntity.getPayLoad());
        System.out.println("SMS Sent to "+payLoad.get("number"));
    }
}
