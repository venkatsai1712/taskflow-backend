package venkatsai.taskflow.service.worker;

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

public abstract class BaseWorker {
    protected final JobRepository jobRepository;
    protected final JobHandlerFactory jobHandlerFactory;

    protected BaseWorker(JobRepository jobRepository, JobHandlerFactory jobHandlerFactory) {
        this.jobRepository = jobRepository;
        this.jobHandlerFactory = jobHandlerFactory;
    }

    @Scheduled(fixedDelay = 10000)
    public void run() {
        List<JobEntity> jobs = jobRepository.findByStatus(JobStatus.PENDING);
        for(JobEntity job : jobs){
            try {
                if(jobRepository.claimJob(job.getId(),JobStatus.PENDING,JobStatus.PROCESSING) == 1) {
                    //Claim Job
                    Optional<JobEntity> freshJob = jobRepository.findById(job.getId());
                    if(freshJob.isEmpty()){
                        throw new JobNotFoundException("Job Not Found");
                    }
                    job = freshJob.get();
                    job.setPickedAt(LocalDateTime.now());
                    jobRepository.save(job);

                    //Handle Job
                    JobHandler handler = jobHandlerFactory.getJobHandler(job.getType());
                    handler.handle(job);
                    System.out.println("Thread: "+Thread.currentThread().getName() +" Job Id: "+job.getId());

                    //Save Status
                    job.setStatus(JobStatus.COMPLETED);
                    job.setCompletedAt(LocalDateTime.now());
                    jobRepository.save(job);
                }
            }catch (Exception exception){
                job.setErrorMessage(exception.getMessage());
                if(job.getRetries() >= 3) {
                    job.setStatus(JobStatus.FAILED);
                }else{
                    job.setRetries(job.getRetries() + 1);
                    job.setStatus(JobStatus.PENDING);
                }
                job.setFailedAt(LocalDateTime.now());
                jobRepository.save(job);
            }
        }
    }
}