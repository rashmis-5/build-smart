package com.buildsmart.vendor.dto;

import com.buildsmart.vendor.entity.VendorDocument;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorDocumentRequestDTO {

    public String contractId;
    private String documentName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private VendorDocument.DocumentType documentType;

    // Optional / business fields
    private String description;
    private String uploadedBy;



}
