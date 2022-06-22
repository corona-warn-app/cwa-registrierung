package com.tsystems.mms.cwa.registration.cancellation.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobEntryRepository extends JpaRepository<JobEntry, String> {

    @Query("select j from JobEntry j where j.job.uuid = :jobId and j.sent is null and j.message is null")
    JobEntry findPendingEntry(@Param("jobId") String jobId);
}
