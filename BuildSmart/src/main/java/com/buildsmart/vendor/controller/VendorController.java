package com.buildsmart.vendor.controller;

import com.buildsmart.vendor.dto.VendorRequest;
import com.buildsmart.vendor.dto.VendorResponse;
import com.buildsmart.vendor.service.VendorService;
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
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendor APIs", description = "Vendor management endpoints")
@PreAuthorize("hasAnyRole('ADMIN','VENDOR')")
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    @Operation(summary = "Create vendor")
    @ApiResponse(responseCode = "201", description = "Vendor created")
    public ResponseEntity<VendorResponse> createVendor(
            @Valid @RequestBody VendorRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(vendorService.createVendor(request));
    }

    @GetMapping
    @Operation(summary = "Get all vendors")
    @ApiResponse(responseCode = "200", description = "Vendors fetched")
    public ResponseEntity<List<VendorResponse>> getAllVendors() {
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @GetMapping("/{vendorId}")
    @Operation(summary = "Get vendor by ID")
    @ApiResponse(responseCode = "200", description = "Vendor fetched")
    public ResponseEntity<VendorResponse> getVendorById(
            @PathVariable String vendorId) {

        return ResponseEntity.ok(vendorService.getVendorById(vendorId));
    }


    @PutMapping("/{vendorId}")
    @Operation(summary = "Update vendor")
    @ApiResponse(responseCode = "200", description = "Vendor updated")
    public ResponseEntity<VendorResponse> updateVendor(
            @RequestParam String vendorId,
            @Valid @RequestBody VendorRequest request) {

        return ResponseEntity
                .ok(vendorService.updateVendor(vendorId, request));
    }

    @PatchMapping("/{vendorId}/status")
    @Operation(summary = "Update vendor status")
    @ApiResponse(responseCode = "200", description = "Vendor status updated")
    public ResponseEntity<VendorResponse> updateVendorStatus(
            @PathVariable String vendorId,
            @RequestParam String status) {
        return ResponseEntity.ok(vendorService.updateVendorStatus(vendorId, status));
    }

    @DeleteMapping("/{vendorId}")
    @Operation(summary = "Delete vendor by id")
    @ApiResponse(responseCode = "200", description = "Vendor deleted")
    public ResponseEntity<List<VendorResponse>> deleteVendorsById(
            @RequestParam String vendorId) {

        return ResponseEntity
                .ok(vendorService.deleteVendorsById(vendorId));
    }
}
