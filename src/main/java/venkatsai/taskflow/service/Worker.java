package venkatsai.taskflow.service;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import venkatsai.taskflow.config.RabbitMQConfig;
import venkatsai.taskflow.entity.JobEntity;
import venkatsai.taskflow.entity.JobStatus;
import venkatsai.taskflow.exception.JobNotFoundException;
import venkatsai.taskflow.repository.JobRepository;
import venkatsai.taskflow.service.handler.JobHandler;
import venkatsai.taskflow.service.handler.factory.JobHandlerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class Worker {
    private final JobRepository jobRepository;
    private final JobHandlerFactory jobHandlerFactory;

    public Worker(JobRepository jobRepository, JobHandlerFactory jobHandlerFactory) {
        this.jobRepository = jobRepository;
        this.jobHandlerFactory = jobHandlerFactory;
    }

    @RabbitListener(queues = "messaging-queue")
    public void process(String id){
        Optional<JobEntity> jobEntity = jobRepository.findById(id);
        JobEntity job = null;
        if(jobEntity.isEmpty()){
            throw new RuntimeException("Cannot Find Job");
        }
        try {
                //Claim Job
                job = claimJob(jobEntity.get());

                //Handle Job
                JobHandler handler = jobHandlerFactory.getJobHandler(job.getType());
                handler.handle(job);
                System.out.println("Thread: " + Thread.currentThread().getName() + " Job Id: " + job.getId());

                //Save Status
                completeJob(job);

            } catch (Exception exception) {
                //Fail Job
                failJob(job, exception);
            }
    }

    public JobEntity claimJob(JobEntity jobEntity){
        Optional<JobEntity> freshJob = jobRepository.findById(jobEntity.getId());
        if (jobRepository.claimJob(jobEntity.getId(), JobStatus.PENDING, JobStatus.PROCESSING) == 1) {
            if (freshJob.isEmpty()) {
                throw new JobNotFoundException("Job Not Found");
            }
            freshJob.get().setPickedAt(LocalDateTime.now());
            jobRepository.save(freshJob.get());
        }
        return freshJob.orElseThrow();
    }

    public void completeJob(JobEntity job){
        job.setStatus(JobStatus.COMPLETED);
        job.setCompletedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    public void failJob(JobEntity job, Exception exception){
        job.setErrorMessage(exception.getMessage());
        if (job.getRetries() >= 3) {
            job.setStatus(JobStatus.FAILED);
        } else {
            job.setRetries(job.getRetries() + 1);
            job.setStatus(JobStatus.PENDING);
        }
        job.setFailedAt(LocalDateTime.now());
        jobRepository.save(job);
    }
}