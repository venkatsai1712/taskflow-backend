package venkatsai.taskflow.service.handler.factory;

import org.springframework.stereotype.Component;
import venkatsai.taskflow.entity.JobType;
import venkatsai.taskflow.exception.HandlerNotFoundException;
import venkatsai.taskflow.service.handler.JobHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JobHandlerFactory {
    private final Map<JobType, JobHandler> handlers;
    public JobHandlerFactory(List<JobHandler> handlerList){
        this.handlers = handlerList.stream()
                        .collect(Collectors.toMap(
                                JobHandler::getJobType,
                                Function.identity()
                        ));
    }
    public JobHandler getJobHandler(JobType jobType) throws RuntimeException {
        JobHandler handler = handlers.get(jobType);
        if(handler == null){
            throw new HandlerNotFoundException("Handler Not Found");
        }
        return handler;
    }
}
