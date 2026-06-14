package venkatsai.taskflow.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import venkatsai.taskflow.repository.JobRepository;
import venkatsai.taskflow.service.handler.factory.JobHandlerFactory;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class WorkerManager {
    private final JobRepository jobRepository;
    private final JobHandlerFactory jobHandlerFactory;
    private final QueueService queueService;
    private ExecutorService executor;

    public WorkerManager(JobRepository jobRepository, JobHandlerFactory jobHandlerFactory, QueueService queueService) {
        this.jobRepository = jobRepository;
        this.jobHandlerFactory = jobHandlerFactory;
        this.queueService = queueService;
    }

    @PostConstruct
    public void startWorkers(){
        executor = Executors.newFixedThreadPool(2);
        for(int i : List.of(1,2)){
            executor.submit(new Worker(jobRepository, jobHandlerFactory, queueService));
        }
    }

    @PreDestroy
    public void endWorkers(){
        executor.shutdownNow();
    }
}
