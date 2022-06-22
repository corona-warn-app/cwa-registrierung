package com.tsystems.mms.cwa.registration.cancellation.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobSummaryRepository extends JpaRepository<JobSummary, String> {
}
