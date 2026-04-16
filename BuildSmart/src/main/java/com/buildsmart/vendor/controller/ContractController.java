package com.buildsmart.vendor.controller;

import com.buildsmart.vendor.dto.ContractRequest;
import com.buildsmart.vendor.dto.ContractResponse;
import com.buildsmart.vendor.service.ContractService;
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
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Tag(name = "Contract APIs", description = "Contract management endpoints")
@PreAuthorize("hasAnyRole('ADMIN','VENDOR')")
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    @Operation(summary = "Create contract")
    @ApiResponse(responseCode = "201", description = "Contract created")
    public ResponseEntity<ContractResponse> createContract(
            @Valid @RequestBody ContractRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(contractService.createContract(request));
    }

    @GetMapping
    @Operation(summary = "Get all contracts")
    @ApiResponse(responseCode = "200", description = "Contracts fetched")
    public ResponseEntity<List<ContractResponse>> getAllContracts() {

        return ResponseEntity.ok(contractService.getAllContracts());
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get contracts by project ID")
    @ApiResponse(responseCode = "200", description = "Contracts fetched")
    public ResponseEntity<List<ContractResponse>> getContractsByProjectId(
            @PathVariable String projectId) {

        return ResponseEntity.ok(
                contractService.getContractsByProjectProjectId(projectId));
    }

    @PutMapping("/{contractId}")
    @Operation(summary = "Update contract")
    @ApiResponse(responseCode = "200", description = "Contract updated")
    public ResponseEntity<ContractResponse> updateContract(
            @PathVariable String contractId,
            @Valid @RequestBody ContractRequest request) {

        return ResponseEntity.ok(
                contractService.updateContract(contractId, request));
    }

    @PatchMapping("/{contractId}/status")
    @Operation(summary = "Update contract status")
    @ApiResponse(responseCode = "200", description = "Contract status updated")
    public ResponseEntity<ContractResponse> updateContractStatus(
            @PathVariable String contractId,
            @RequestParam String status) {
        return ResponseEntity.ok(contractService.updateContractStatus(contractId, status));
    }

    @DeleteMapping("/{contractId}")
    @Operation(summary = "Delete contract")
    @ApiResponse(responseCode = "200", description = "Contract deleted")
    public ResponseEntity<ContractResponse> deleteContract(
            @PathVariable String contractId) {

        return ResponseEntity.ok(
                contractService.deleteContract(contractId));
    }
}