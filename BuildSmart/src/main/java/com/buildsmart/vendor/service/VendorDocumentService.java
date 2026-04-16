package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.ApprovalRequest;
import com.buildsmart.vendor.dto.VendorDocumentRequestDTO;
import com.buildsmart.vendor.dto.VendorDocumentResponseDTO;
import com.buildsmart.vendor.entity.VendorDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VendorDocumentService {

    List<VendorDocumentResponseDTO> getAllDocuments();

    VendorDocumentResponseDTO getDocumentById(Long id);

    List<VendorDocumentResponseDTO> getDocumentsByContract(Long contractId);

    List<VendorDocumentResponseDTO> getDocumentsByStatus(
            VendorDocument.DocumentStatus status);

    List<VendorDocumentResponseDTO> getDocumentsByType(
            VendorDocument.DocumentType type);

    VendorDocumentResponseDTO uploadDocument(
            MultipartFile file,
            VendorDocumentRequestDTO request
    ) throws IOException;

    VendorDocumentResponseDTO processApproval(
            Long id,
            ApprovalRequest request
    );

    void deleteDocument(Long id);
}
