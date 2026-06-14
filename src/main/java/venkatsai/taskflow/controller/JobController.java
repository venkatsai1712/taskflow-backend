package venkatsai.taskflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import venkatsai.taskflow.dto.JobRequest;
import venkatsai.taskflow.dto.JobResponse;
import venkatsai.taskflow.service.JobService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class JobController {
    private final JobService jobService;
    public JobController(JobService jobService){
        this.jobService = jobService;
    }
    @PostMapping("/job")
    public ResponseEntity<JobResponse> createJob(@RequestBody JobRequest req) throws InterruptedException {
        return ResponseEntity.status(201).body(jobService.createJob(req));
    }
    @GetMapping("/job/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable String id){
        return ResponseEntity.status(200).body(jobService.getJob(id));
    }
}
