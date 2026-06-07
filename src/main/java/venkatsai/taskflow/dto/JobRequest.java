package venkatsai.taskflow.dto;

import tools.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import venkatsai.taskflow.entity.JobType;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {
    private JobType type;
    private JsonNode payLoad;
}
