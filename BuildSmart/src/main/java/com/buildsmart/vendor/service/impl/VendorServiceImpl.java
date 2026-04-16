package com.buildsmart.vendor.service.impl;

import com.buildsmart.common.exception.DuplicateResourceException;
import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.loggers.ApplicationLogger;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.vendor.entity.Vendor;
import com.buildsmart.vendor.dto.VendorRequest;
import com.buildsmart.vendor.dto.VendorResponse;
import com.buildsmart.vendor.repository.VendorRepository;
import com.buildsmart.vendor.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    @Transactional
    public VendorResponse createVendor(VendorRequest request) {
        ApplicationLogger.log.info("Creating vendor");

        if (vendorRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Vendor already exists: " + request.name());
        }

        Vendor last = vendorRepository.findTopByOrderByVendorIdDesc();

        Vendor vendor = new Vendor();
        vendor.setVendorId(
                IdGeneratorUtil.nextVendorId(last == null ? null : last.getVendorId()));
        vendor.setName(request.name());
        vendor.setContactInfo(request.contactInfo());
        vendor.setStatus(request.status());

        return toResponse(vendorRepository.save(vendor));
    }


    @Override
    @Transactional(readOnly = true)
    public List<VendorResponse> getAllVendors() {
        ApplicationLogger.log.info("Fetching all vendors");

        return vendorRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public VendorResponse getVendorById(String vendorId) {
        ApplicationLogger.log.info("Fetching vendor with id: {}", vendorId);

        Vendor vendor = vendorRepository
                .findByVendorId(vendorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Vendor not found with id: " + vendorId));

        return toResponse(vendor);
    }

    @Override
    @Transactional
    public List<VendorResponse> deleteVendorsById(String vendorId) {
        ApplicationLogger.log.info("Deleting vendor with id: {}", vendorId);

        Vendor vendor = vendorRepository
                .findByVendorId(vendorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Vendor not found with id: " + vendorId));

        vendorRepository.delete(vendor);

        return List.of(toResponse(vendor));
    }


    @Override
    @Transactional
    public VendorResponse updateVendor(String vendorId, VendorRequest request) {
        ApplicationLogger.log.info("Updating vendor with id: {}", vendorId);

        Vendor vendor = vendorRepository
                .findByVendorId(vendorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Vendor not found with id: " + vendorId));

        vendor.setName(request.name());
        vendor.setContactInfo(request.contactInfo());
        vendor.setStatus(request.status());


        return toResponse(vendorRepository.save(vendor));
    }

    @Override
    @Transactional
    public VendorResponse updateVendorStatus(String vendorId, String status) {
        ApplicationLogger.log.info("Updating vendor status: {} -> {}", vendorId, status);

        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vendor not found: " + vendorId));

        vendor.setStatus(status);
        return toResponse(vendorRepository.save(vendor));
    }





    private VendorResponse toResponse(Vendor vendor) {
        return VendorResponse.of(
                vendor.getVendorId(),
                vendor.getName(),
                vendor.getContactInfo(),
                vendor.getStatus()
        );
    }
}