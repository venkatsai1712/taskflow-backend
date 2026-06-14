package venkatsai.taskflow.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import venkatsai.taskflow.config.RabbitMQConfig;
import venkatsai.taskflow.entity.JobEntity;

@Service
public class QueueService {
    private final RabbitTemplate rabbitTemplate;

    public QueueService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void put(JobEntity job) throws InterruptedException {
        rabbitTemplate.convertAndSend(RabbitMQConfig.JobQueue, job.getId());
    }
}
