package venkatsai.taskflow.service.worker;

import org.springframework.stereotype.Component;
import venkatsai.taskflow.repository.JobRepository;
import venkatsai.taskflow.service.handler.factory.JobHandlerFactory;

@Component
public class WorkerA extends BaseWorker {

    public WorkerA(JobRepository jobRepository, JobHandlerFactory jobHandlerFactory) {
        super(jobRepository, jobHandlerFactory);
    }
}