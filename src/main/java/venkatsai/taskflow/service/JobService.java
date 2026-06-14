package venkatsai.taskflow.service;

import tools.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import venkatsai.taskflow.dto.JobRequest;
import venkatsai.taskflow.dto.JobResponse;
import venkatsai.taskflow.entity.JobEntity;
import venkatsai.taskflow.entity.JobStatus;
import venkatsai.taskflow.entity.JobType;
import venkatsai.taskflow.exception.JobNotFoundException;
import venkatsai.taskflow.repository.JobRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final QueueService queueService;

    public JobService(JobRepository jobRepository, QueueService queueService) {
        this.jobRepository = jobRepository;
        this.queueService = queueService;
    }

    public JobResponse createJob(JobRequest req) throws InterruptedException {
        JobType jobType = req.getType();
        JsonNode payLoad = req.getPayLoad();
        String jobId = UUID.randomUUID().toString();
        JobEntity job = JobEntity.builder()
                .payLoad(payLoad.toString())
                .createdAt(LocalDateTime.now())
                .id(jobId)
                .type(jobType)
                .status(JobStatus.PENDING)
                .build();
        jobRepository.save(job);
        queueService.put(job);
        return JobResponse.builder()
                .jobId(jobId)
                .status(String.valueOf(JobStatus.PENDING))
                .build();
    }

    public JobResponse getJob(String id) {
        Optional<JobEntity> job = jobRepository.findById(id);
        if(job.isEmpty()){
            throw new JobNotFoundException("Job Not Found");
        }
        return JobResponse.builder()
                .jobId(job.get().getId())
                .status(String.valueOf(job.get().getStatus()))
                .errorMessage(job.get().getErrorMessage())
                .build();
    }
}
