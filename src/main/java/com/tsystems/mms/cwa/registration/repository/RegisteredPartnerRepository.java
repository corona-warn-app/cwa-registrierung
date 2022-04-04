package com.tsystems.mms.cwa.registration.repository;

import com.tsystems.mms.cwa.registration.model.RegisteredPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegisteredPartnerRepository extends JpaRepository<RegisteredPartner, String> {

    RegisteredPartner findByEmail(String email);

    RegisteredPartner findByToken(String token);

    List<RegisteredPartner> findByApprovedIsNotNullAndExportedIsNull();
}
