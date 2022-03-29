/*
 * Corona-Warn-App / cwa-map-registrierung
 *
 * (C) 2020, T-Systems International GmbH
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
