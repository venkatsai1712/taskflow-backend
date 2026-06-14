package venkatsai.taskflow.service;

import org.springframework.scheduling.annotation.Scheduled;
import venkatsai.taskflow.entity.JobEntity;
import venkatsai.taskflow.entity.JobStatus;
import venkatsai.taskflow.exception.JobNotFoundException;
import venkatsai.taskflow.repository.JobRepository;
import venkatsai.taskflow.service.handler.JobHandler;
import venkatsai.taskflow.service.handler.factory.JobHandlerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Worker implements Runnable {
    private final JobRepository jobRepository;
    private final JobHandlerFactory jobHandlerFactory;
    private final QueueService queueService;

    public Worker(JobRepository jobRepository, JobHandlerFactory jobHandlerFactory, QueueService queueService) {
        this.jobRepository = jobRepository;
        this.jobHandlerFactory = jobHandlerFactory;
        this.queueService = queueService;
    }

    @Override
     public void run() {
        while (true) {
            JobEntity job = null;
            try {
                JobEntity jobEntity = queueService.take();
                    //Claim Job
                    job = claimJob(jobEntity);

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