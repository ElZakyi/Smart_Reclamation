package com.cihbank.backend.attachment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Integer> {
     List<Attachment> findByReclamationIdReclamation(Integer idReclamation);
}
