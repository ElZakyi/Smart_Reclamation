package com.cihbank.backend.attachment;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/attachment")
public class AttachmentController {
    private final AttachmentService attachmentService;
    public AttachmentController(AttachmentService attachmentService){
        this.attachmentService = attachmentService;
    }
    @PreAuthorize("hasAuthority('VIEW_ATTACHMENT')")
    @GetMapping("/reclamation/{idReclamation}")
    public List<Attachment> getAttachementByReclamationId(@PathVariable Integer idReclamation){
        return attachmentService.getAttachmentByReclamationId(idReclamation);
    }
    @PreAuthorize("hasAuthority('UPLOAD_ATTACHMENT')")
    @PostMapping("/reclamation/{idReclamation}/user/{idUser}")
    public String uploadAttachment(@PathVariable Integer idReclamation, @PathVariable Integer idUser, @RequestParam("file")MultipartFile file) throws IOException {
        attachmentService.uploadattachment(idUser,idReclamation,file);
        return "Attachment uploaded successfully";
    }
    @DeleteMapping("/{idAttachment}")
    @PreAuthorize("hasAuthority('DELETE_ATTACHMENT')")
    public String deleteAttachment(@PathVariable Integer idAttachment){
        attachmentService.deleteAttachment(idAttachment);
        return "Attachment deleted successfully";
    }
}
