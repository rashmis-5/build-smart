package com.buildsmart.vendor.repository;

import com.buildsmart.vendor.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, String> {

    Vendor findTopByOrderByVendorIdDesc();

    boolean existsByName(String name);



    Optional<Vendor> findByVendorId(String vendorId);
}