package venkatsai.taskflow.service.handler;

import org.springframework.stereotype.Component;
import venkatsai.taskflow.entity.JobEntity;
import venkatsai.taskflow.entity.JobType;

@Component
public interface JobHandler {
    JobType getJobType();
    void handle(JobEntity jobEntity) throws InterruptedException;
}
