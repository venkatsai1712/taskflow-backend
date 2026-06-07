package venkatsai.taskflow.entity;

import tools.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobEntity {
    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private JobType type;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private String payLoad;
    private String workerId;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime pickedAt;
    private LocalDateTime failedAt;
    private String errorMessage;
    private int retries;
}
