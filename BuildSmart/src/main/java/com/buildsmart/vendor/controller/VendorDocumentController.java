package com.buildsmart.vendor.controller;

import com.buildsmart.vendor.dto.*;
import com.buildsmart.vendor.entity.VendorDocument;
import com.buildsmart.vendor.service.FileStorageService;
import com.buildsmart.vendor.service.VendorDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class VendorDocumentController {

    private final VendorDocumentService documentService;
    private final FileStorageService fileStorageService;

    /* ============================
       GET DOCUMENTS
       ============================ */

    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorDocumentResponseDTO>>> getAll(
            @RequestParam(required = false) Long contractId,
            @RequestParam(required = false) VendorDocument.DocumentStatus status,
            @RequestParam(required = false) VendorDocument.DocumentType documentType) {

        List<VendorDocumentResponseDTO> result;

        if (contractId != null) {
            result = documentService.getDocumentsByContract(contractId);
        } else if (status != null) {
            result = documentService.getDocumentsByStatus(status);
        } else if (documentType != null) {
            result = documentService.getDocumentsByType(documentType);
        } else {
            result = documentService.getAllDocuments();
        }

        return ResponseEntity.ok(
                ApiResponse.success(result, "Documents retrieved")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorDocumentResponseDTO>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        documentService.getDocumentById(id),
                        "Document retrieved"
                )
        );
    }

    /* ============================
       UPLOAD DOCUMENT
       ============================ */

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<VendorDocumentResponseDTO>> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute VendorDocumentRequestDTO request
    ) throws IOException {

        VendorDocumentResponseDTO response =
                documentService.uploadDocument(file, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        response,
                        "Document uploaded and submitted for approval"
                )
        );
    }

    /* ============================
       APPROVAL FLOW
       ============================ */

    @PostMapping("/{id}/approval")
    public ResponseEntity<ApiResponse<VendorDocumentResponseDTO>> processApproval(
            @PathVariable Long id,
            @RequestBody ApprovalRequest request) {

        VendorDocumentResponseDTO response =
                documentService.processApproval(id, request);

        String message = request.isApproved()
                ? "Document approved"
                : "Document rejected";

        return ResponseEntity.ok(
                ApiResponse.success(response, message)
        );
    }

    /* ============================
       DOWNLOAD DOCUMENT
       ============================ */

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id) throws MalformedURLException {

        VendorDocumentResponseDTO doc =
                documentService.getDocumentById(id);

        Path filePath =
                fileStorageService.getFilePath(doc.getFilePath());

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType =
                doc.getFileType() != null
                        ? doc.getFileType()
                        : "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getDocumentName() + "\""
                )
                .body(resource);
    }

    /* ============================
       DELETE DOCUMENT
       ============================ */

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        documentService.deleteDocument(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Document deleted")
        );
    }
}
