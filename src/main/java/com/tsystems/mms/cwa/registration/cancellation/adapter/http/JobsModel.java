package com.tsystems.mms.cwa.registration.cancellation.adapter.http;

import com.tsystems.mms.cwa.registration.cancellation.domain.JobSummary;
import lombok.Data;

import java.util.List;

@Data
public class JobsModel {
    private List<JobSummary> jobs;
}
