package venkatsai.taskflow.service;

import org.springframework.stereotype.Service;
import venkatsai.taskflow.entity.JobEntity;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class QueueService {
    private final BlockingQueue<JobEntity> queue = new LinkedBlockingQueue<>(5);
    public void put(JobEntity job) throws InterruptedException {
        queue.put(job);
    }
    public JobEntity take() throws InterruptedException {
        return queue.take();
    }
}
