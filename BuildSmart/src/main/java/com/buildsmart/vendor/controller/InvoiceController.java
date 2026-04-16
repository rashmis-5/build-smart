package com.buildsmart.vendor.controller;

import com.buildsmart.vendor.dto.InvoiceRequest;
import com.buildsmart.vendor.dto.InvoiceResponse;
import com.buildsmart.vendor.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice APIs", description = "Invoice management endpoints")
@PreAuthorize("hasAnyRole('ADMIN','VENDOR')")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @Operation(summary = "Create invoice")
    @ApiResponse(responseCode = "201", description = "Invoice created")
    public ResponseEntity<InvoiceResponse> createInvoice(
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(invoiceService.createInvoice(request));
    }

    @GetMapping
    @Operation(summary = "Get all invoices")
    @ApiResponse(responseCode = "200", description = "Invoices fetched")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/contract/{contractId}")
    @Operation(summary = "Get invoices by contract ID")
    @ApiResponse(responseCode = "200", description = "Invoices fetched")
    public ResponseEntity<List<InvoiceResponse>> getInvoicesByContractId(
            @PathVariable String contractId) {
        return ResponseEntity.ok(
                invoiceService.getInvoicesByContractId(contractId));
    }

    @PutMapping("/{invoiceId}")
    @Operation(summary = "Update invoice")
    @ApiResponse(responseCode = "200", description = "Invoice updated")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable String invoiceId,
            @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(
                invoiceService.updateInvoice(invoiceId, request));
    }

    @PatchMapping("/{invoiceId}/status")
    @Operation(summary = "Update invoice status")
    @ApiResponse(responseCode = "200", description = "Invoice status updated")
    public ResponseEntity<InvoiceResponse> updateInvoiceStatus(
            @PathVariable String invoiceId,
            @RequestParam String status) {
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(invoiceId, status));
    }

    @DeleteMapping("/{invoiceId}")
    @Operation(summary = "Delete invoice")
    @ApiResponse(responseCode = "204", description = "Invoice deleted")
    public ResponseEntity<Void> deleteInvoice(
            @PathVariable String invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
        return ResponseEntity.noContent().build();
    }
}