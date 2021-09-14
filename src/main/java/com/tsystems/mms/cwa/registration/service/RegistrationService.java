package com.tsystems.mms.cwa.registration.service;

import com.tsystems.mms.cwa.registration.export.ExportRepository;
import com.tsystems.mms.cwa.registration.model.Export;
import com.tsystems.mms.cwa.registration.model.Partner;
import com.tsystems.mms.cwa.registration.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

/**
 * Service class, access and store into repository.
 *
 * @author Robin Lutter (Robin.Lutter@T-Systems.com)
 */
@Service
@Transactional
public class RegistrationService {

    private final PartnerRepository partnerRepository;
    private final ExportRepository exportRepository;

    @Autowired
    public RegistrationService(PartnerRepository repository, ExportRepository exportRepository) {
        this.partnerRepository = repository;
        this.exportRepository = exportRepository;
    }

    public Partner createPartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    public Iterable<Partner> exportPartners(LocalDateTime since) {
        if (since == null) {
            since = exportRepository.findLastExport();
        }

        Principal principal = (Principal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Export export = new Export(principal.getName());

        Iterable<Partner> result = since == null
                ? partnerRepository.findAll()
                : partnerRepository.findNewSince(since);

        exportRepository.save(export);
        return result;
    }
}
