package com.buildsmart.vendor.controller;

import com.buildsmart.vendor.dto.DeliveryRequest;
import com.buildsmart.vendor.dto.DeliveryResponse;
import com.buildsmart.vendor.service.DeliveryService;
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
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Delivery APIs", description = "Delivery tracking endpoints")
@PreAuthorize("hasAnyRole('ADMIN','VENDOR')")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    @Operation(summary = "Create delivery")
    @ApiResponse(responseCode = "201", description = "Delivery created")
    public ResponseEntity<DeliveryResponse> createDelivery(
            @Valid @RequestBody DeliveryRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(deliveryService.createDelivery(request));
    }

    @GetMapping
    @Operation(summary = "Get all deliveries")
    @ApiResponse(responseCode = "200", description = "Deliveries fetched")
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @GetMapping("/contract/{contractId}")
    @Operation(summary = "Get deliveries by contract ID")
    @ApiResponse(responseCode = "200", description = "Deliveries fetched")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByContractId(
            @PathVariable String contractId) {

        return ResponseEntity.ok(
                deliveryService.getDeliveriesByContractId(contractId));
    }

    @PutMapping("/{deliveryId}")
    @Operation(summary = "Update delivery")
    @ApiResponse(responseCode = "200", description = "Delivery updated")
    public ResponseEntity<DeliveryResponse> updateDelivery(
            @PathVariable String deliveryId,
            @Valid @RequestBody DeliveryRequest request) {

        return ResponseEntity.ok(
                deliveryService.updateDelivery(deliveryId, request));
    }

    @PatchMapping("/{deliveryId}/status")
    @Operation(summary = "Update delivery status")
    @ApiResponse(responseCode = "200", description = "Delivery status updated")
    public ResponseEntity<DeliveryResponse> updateDeliveryStatus(
            @PathVariable String deliveryId,
            @RequestParam String status) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(deliveryId, status));
    }

    @DeleteMapping("/{deliveryId}")
    @Operation(summary = "Delete delivery")
    @ApiResponse(responseCode = "204", description = "Delivery deleted")
    public ResponseEntity<Void> deleteDelivery(
            @PathVariable String deliveryId) {

        deliveryService.deleteDelivery(deliveryId);
        return ResponseEntity.noContent().build();
    }
}