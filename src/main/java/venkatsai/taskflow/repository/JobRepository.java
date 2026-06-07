package venkatsai.taskflow.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import venkatsai.taskflow.entity.JobEntity;
import venkatsai.taskflow.entity.JobStatus;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<JobEntity,String> {
    List<JobEntity> findByStatus(JobStatus status);
    @Transactional
    @Modifying
    @Query("""
    UPDATE JobEntity job
    SET job.status = :nextStatus
    WHERE job.id = :id
    AND job.status = :currStatus
""")
    int claimJob(String id, JobStatus currStatus, JobStatus nextStatus);
}
