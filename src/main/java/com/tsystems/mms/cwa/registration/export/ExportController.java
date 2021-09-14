package com.tsystems.mms.cwa.registration.export;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.tsystems.mms.cwa.registration.model.Partner;
import com.tsystems.mms.cwa.registration.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller implementation to handle requests from backend system and export new registered partners and documents.
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
@Controller
public class ExportController {

    private final RegistrationService registrationService;
    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    public ExportController(RegistrationService registrationService, AutowireCapableBeanFactory beanFactory) {
        this.registrationService = registrationService;
        this.beanFactory = beanFactory;
    }

    /**
     * Exports all newly created test center/partner registrations from given <code>since</code>-Parameter (@see DateTimeFormat.ISO.DATE_TIME).
     *
     * @param since
     * @param response
     * @throws IOException
     * @throws CsvRequiredFieldEmptyException
     * @throws CsvDataTypeMismatchException
     */
    @GetMapping(path = "export", produces = "text/csv;charset=UTF-8")
    public void exportPartner(@RequestParam(value = "since", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime since,
                              HttpServletResponse response) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-hhmm");
        String filename = String.format("testCenter_%s.csv", LocalDateTime.now().format(formatter));

        var testCenter = registrationService.exportPartners(since);

        if (testCenter.iterator().hasNext()) {
            response.setContentType("text/csv");
            response.setCharacterEncoding("UTF-8");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

            var writer = new EscapingCsvWriter(response.getWriter(), ';', '"', '"', "\r\n");

            // print BOM
            response.getWriter().print("\uFEFF");
            HeaderColumnNameWithPositionMappingStrategy<Partner> mappingStrategy = new HeaderColumnNameWithPositionMappingStrategy<>(this.beanFactory);
            mappingStrategy.setType(Partner.class);
            //create a csv writer
            StatefulBeanToCsv<Partner> csvWriter = new StatefulBeanToCsvBuilder<Partner>(writer)
                    .withMappingStrategy(mappingStrategy)
                    .withEscapechar('"')
                    .withQuotechar('"')
                    .withSeparator(';')
                    .withOrderedResults(false)
                    .build();
            csvWriter.write(testCenter.iterator());
        } else {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }
}
