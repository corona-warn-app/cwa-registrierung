package com.tsystems.mms.cwa.registration.cancellation.adapter.http;

import com.tsystems.mms.cwa.registration.cancellation.domain.JobSummary;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobSummaryDto {
    private JobSummary jobSummary;
    private boolean running;
}
