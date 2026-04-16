package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.VendorRequest;
import com.buildsmart.vendor.dto.VendorResponse;

import java.util.List;

public interface VendorService {

    VendorResponse createVendor(VendorRequest request);

    List<VendorResponse> getAllVendors();
    VendorResponse getVendorById(String vendorId);

    List<VendorResponse> deleteVendorsById(String vendorId);


    VendorResponse updateVendor(String vendorId, VendorRequest request);

    VendorResponse updateVendorStatus(String vendorId, String status);




}