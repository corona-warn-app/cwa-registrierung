package com.tsystems.mms.cwa.registration.cancellation.adapter.http;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.opencsv.exceptions.CsvValidationException;
import com.tsystems.mms.cwa.registration.cancellation.application.CancellationsService;
import com.tsystems.mms.cwa.registration.cancellation.domain.Job;
import com.tsystems.mms.cwa.registration.cancellation.domain.JobEntry;
import com.tsystems.mms.cwa.registration.cancellation.domain.JobEntryRepository;
import com.tsystems.mms.cwa.registration.cancellation.domain.JobRepository;
import com.tsystems.mms.cwa.registration.cancellation.domain.JobSummaryRepository;
import com.tsystems.mms.cwa.registration.export.EscapingCsvWriter;
import com.tsystems.mms.cwa.registration.export.HeaderColumnNameWithPositionMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
public class JobsController {

    private final Logger log = LoggerFactory.getLogger(JobsController.class);

    private final CancellationsService cancellationsService;
    private final JobRepository jobRepository;
    private final JobEntryRepository jobEntryRepository;
    private final JobSummaryRepository jobSummaryRepository;

    private final AutowireCapableBeanFactory beanFactory;

    private interface DateParser {
        LocalDateTime parse(String value);
    }

    private final List<DateParser> DATA_PARSERS = Arrays.asList(
            value -> LocalDateTime.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.GERMANY)),
            value -> LocalDateTime.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMANY)).withSecond(59),
            value -> LocalDateTime.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss", Locale.GERMANY)),
            value -> LocalDateTime.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.GERMANY)).withSecond(59),
            value -> LocalDate.parse(value, DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY)).atTime(23, 59, 59)
    );

    public JobsController(CancellationsService cancellationsService, JobRepository jobRepository, JobEntryRepository jobEntryRepository, JobSummaryRepository jobSummaryRepository, AutowireCapableBeanFactory beanFactory) {
        this.cancellationsService = cancellationsService;
        this.jobRepository = jobRepository;
        this.jobEntryRepository = jobEntryRepository;
        this.jobSummaryRepository = jobSummaryRepository;
        this.beanFactory = beanFactory;
    }

    @GetMapping("/cancellations")
    public String cancellations(Model model) {
        model.addAttribute("summaries",
                jobSummaryRepository.findAll(Sort.by("created"))
                        .stream()
                        .map(jobSummary -> new JobSummaryDto(
                                jobSummary,
                                cancellationsService.isJobRunning(jobSummary.getUuid()
                                )))
                        .collect(Collectors.toList()));
        return "cancellations/jobs";
    }

    @GetMapping("/cancellations/start/{jobId}")
    public String startJob(@PathVariable String jobId) {
        final var job = jobRepository.findById(jobId);
        if (job.isPresent()) {
            if (!cancellationsService.isJobRunning(jobId)) {
                cancellationsService.startJob(job.get());
            }
        }
        return "redirect:/cancellations";
    }

    @GetMapping("/cancellations/stop/{jobId}")
    public String stopJob(@PathVariable String jobId) {
        final var job = jobRepository.findById(jobId);
        if (job.isPresent()) {
            if (cancellationsService.isJobRunning(jobId)) {
                cancellationsService.stopJob(jobId);
            }
        }
        return "redirect:/cancellations";
    }

    @Transactional(readOnly = true)
    @GetMapping("/cancellations/export/{jobId}")
    public void exportJob(@PathVariable String jobId, HttpServletResponse response) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        final var job = jobRepository.findById(jobId);
        if (job.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attechment; filename=" + job.get().getFilename());
        response.setHeader(HttpHeaders.CONTENT_TYPE, "text/csv");
        final var writer = new EscapingCsvWriter(response.getWriter(), ';', '"', '"', "\r\n");
        //response.getWriter().print("\uFEFF"); // print BOM

        final var mappingStrategy = new HeaderColumnNameWithPositionMappingStrategy<JobEntry>(this.beanFactory);
        mappingStrategy.setType(JobEntry.class);
        //create a csv writer
        final var csvWriter = new StatefulBeanToCsvBuilder<JobEntry>(writer)
                .withMappingStrategy(mappingStrategy)
                .withEscapechar('"')
                .withQuotechar('"')
                .withSeparator(';')
                .withOrderedResults(false)
                .build();
        csvWriter.write(job.get().getEntries());
    }

    @GetMapping("/cancellations/delete/{jobId}")
    public String deleteJob(@PathVariable String jobId) {
        final var job = jobRepository.findById(jobId);
        job.ifPresent(jobRepository::delete);
        return "redirect:/cancellations";
    }

    @Transactional
    @PostMapping("/cancellations/upload")
    public String uploadJob(@RequestParam("file") MultipartFile file,
                            @RequestParam("partnerType") String partnerType,
                            @RequestParam("subject") String subject,
                            @RequestParam(value = "cancelInPortal", defaultValue = "false") boolean cancelInPortal,
                            @RequestParam(value = "sendEmail", defaultValue = "false") boolean sendEmail,
                            @RequestParam("additionalAttachment") String additionalAttachment,
                            @RequestParam("bcc") String bcc,
                            Model model) throws IOException, CsvValidationException {
        final var parser = new CSVParserBuilder()
                .withSeparator(';')
                .withIgnoreQuotations(false)
                .build();

        final var csvReader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream()))
                .withSkipLines(1)
                .withCSVParser(parser)
                .build();

        final var job = new Job();
        job.setCreated(LocalDateTime.now());
        job.setFilename(file.getOriginalFilename());
        job.setPartnerType(partnerType);
        job.setEntries(new ArrayList<>());
        job.setBcc(bcc);
        job.setCancelInPortal(cancelInPortal);
        job.setAdditionalAttachment(additionalAttachment);
        job.setSubject(subject);
        job.setSendEmail(sendEmail);
        jobRepository.save(job);

        try {
            String[] tokens;
            while ((tokens = csvReader.readNext()) != null) {
                final var entry = new JobEntry();
                entry.setJob(job);
                entry.setCreated(LocalDateTime.now());
                entry.setPartnerId(tokens[0]);
                entry.setReceiver(tokens[8]);
                entry.setAttachmentFilename(tokens[0] + ".pdf");

                for (Iterator<DateParser> iterator = DATA_PARSERS.iterator(); iterator.hasNext(); ) {
                    DateParser dateParser = iterator.next();
                    try {
                        entry.setFinalDeletionRequest(dateParser.parse(tokens[11]));
                        break;
                    } catch (DateTimeParseException e) {
                        if (!iterator.hasNext()) {
                            throw e;
                        }
                    }
                }

                jobEntryRepository.save(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/cancellations";
    }
}

