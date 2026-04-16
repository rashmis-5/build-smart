package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.InvoiceRequest;
import com.buildsmart.vendor.dto.InvoiceResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InvoiceService {


    @Transactional
    InvoiceResponse createInvoice(InvoiceRequest request);

    List<InvoiceResponse> getAllInvoices();

    List<InvoiceResponse> getInvoicesByContractId(String contractId);

    InvoiceResponse updateInvoice(String invoiceId, InvoiceRequest request);

    InvoiceResponse updateInvoiceStatus(String invoiceId, String status);

    void deleteInvoice(String invoiceId);

}