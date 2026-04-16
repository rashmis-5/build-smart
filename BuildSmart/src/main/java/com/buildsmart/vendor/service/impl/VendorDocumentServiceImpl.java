package com.buildsmart.vendor.service.impl;

import com.buildsmart.vendor.dto.ApprovalRequest;
import com.buildsmart.vendor.dto.VendorDocumentRequestDTO;
import com.buildsmart.vendor.dto.VendorDocumentResponseDTO;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.vendor.entity.Contract;
import com.buildsmart.vendor.entity.VendorDocument;
import com.buildsmart.vendor.repository.ContractRepository;
import com.buildsmart.vendor.repository.VendorDocumentRepository;
import com.buildsmart.vendor.service.FileStorageService;
import com.buildsmart.vendor.service.VendorDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorDocumentServiceImpl implements VendorDocumentService {

    private final VendorDocumentRepository documentRepository;
    private final ContractRepository contractRepository;
    private final FileStorageService fileStorageService;

    /* =============================
       READ OPERATIONS
       ============================= */

    @Override
    public List<VendorDocumentResponseDTO> getAllDocuments() {
        List<VendorDocument> documents = documentRepository.findAll();
        List<VendorDocumentResponseDTO> response = new ArrayList<>();

        for (VendorDocument doc : documents) {
            response.add(toResponseDTO(doc));
        }
        return response;
    }

    @Override
    public VendorDocumentResponseDTO getDocumentById(Long id) {
        return toResponseDTO(findById(id));
    }

    @Override
    public List<VendorDocumentResponseDTO> getDocumentsByContract(Long contractId) {
        List<VendorDocument> documents =
                documentRepository.findByContractContractId(contractId);

        List<VendorDocumentResponseDTO> response = new ArrayList<>();
        for (VendorDocument doc : documents) {
            response.add(toResponseDTO(doc));
        }
        return response;
    }

    @Override
    public List<VendorDocumentResponseDTO> getDocumentsByStatus(
            VendorDocument.DocumentStatus status) {

        List<VendorDocument> documents = documentRepository.findByStatus(status);
        List<VendorDocumentResponseDTO> response = new ArrayList<>();

        for (VendorDocument doc : documents) {
            response.add(toResponseDTO(doc));
        }
        return response;
    }

    @Override
    public List<VendorDocumentResponseDTO> getDocumentsByType(
            VendorDocument.DocumentType type) {

        List<VendorDocument> documents = documentRepository.findByDocumentType(type);
        List<VendorDocumentResponseDTO> response = new ArrayList<>();

        for (VendorDocument doc : documents) {
            response.add(toResponseDTO(doc));
        }
        return response;
    }

    /* =============================
       UPLOAD
       ============================= */

    @Override
    public VendorDocumentResponseDTO uploadDocument(
            MultipartFile file,
            VendorDocumentRequestDTO request) throws IOException {

        Contract contract = contractRepository.findById(request.getContractId()).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contract",
                                request.getContractId()
                        ));

        String filePath = fileStorageService.storeFile(
                file,
                "documents/" + request.getDocumentType().name().toLowerCase()
        );

        VendorDocument document = VendorDocument.builder()
                .contract(contract)
                .documentName(file.getOriginalFilename())
                .filePath(filePath)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .documentType(request.getDocumentType())
                .status(VendorDocument.DocumentStatus.SUBMITTED)
                .uploadedBy(request.getUploadedBy())
                .uploadedAt(LocalDateTime.now())
                .description(request.getDescription())
                .build();

        return toResponseDTO(documentRepository.save(document));
    }

    /* =============================
       APPROVAL
       ============================= */

    @Override
    public VendorDocumentResponseDTO processApproval(
            Long id,
            ApprovalRequest request) {

        VendorDocument doc = findById(id);

        if (doc.getStatus() != VendorDocument.DocumentStatus.SUBMITTED &&
                doc.getStatus() != VendorDocument.DocumentStatus.UNDER_REVIEW) {
            throw new IllegalArgumentException(
                    "Document must be SUBMITTED or UNDER_REVIEW for approval"
            );
        }

        if (request.isApproved()) {
            doc.setStatus(VendorDocument.DocumentStatus.APPROVED);
            doc.setApprovedBy(request.getApprovedBy());
            doc.setApprovedAt(LocalDateTime.now());
        } else {
            doc.setStatus(VendorDocument.DocumentStatus.REJECTED);
            doc.setRejectionReason(request.getRejectionReason());
        }

        return toResponseDTO(documentRepository.save(doc));
    }

    /* =============================
       DELETE
       ============================= */

    @Override
    public void deleteDocument(Long id) {
        VendorDocument doc = findById(id);
        fileStorageService.deleteFile(doc.getFilePath());
        documentRepository.delete(doc);
    }

    /* =============================
       INTERNAL HELPERS
       ============================= */

    private VendorDocument findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document", id));
    }

    private VendorDocumentResponseDTO toResponseDTO(VendorDocument d) {
        return VendorDocumentResponseDTO.builder()
                .documentId(d.getDocumentId())
                .contractId(Long.valueOf(d.getContract().getContractId()))
                .documentName(d.getDocumentName())
                .filePath(d.getFilePath())
                .fileType(d.getFileType())
                .fileSize(d.getFileSize())
                .documentType(d.getDocumentType())
                .status(d.getStatus())
                .uploadedBy(d.getUploadedBy())
                .uploadedAt(d.getUploadedAt())
                .approvedBy(d.getApprovedBy())
                .approvedAt(d.getApprovedAt())
                .rejectionReason(d.getRejectionReason())
                .description(d.getDescription())
                .build();
    }
}
