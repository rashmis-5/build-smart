package com.buildsmart.vendor.service.impl;

import com.buildsmart.common.exception.ResourceNotFoundException;
import com.buildsmart.common.loggers.ApplicationLogger;
import com.buildsmart.common.util.IdGeneratorUtil;
import com.buildsmart.vendor.entity.Contract;
import com.buildsmart.vendor.entity.Vendor;
import com.buildsmart.projectmanager.entity.Project;
import com.buildsmart.projectmanager.repository.ProjectRepository;
import com.buildsmart.vendor.dto.ContractRequest;
import com.buildsmart.vendor.dto.ContractResponse;
import com.buildsmart.vendor.repository.ContractRepository;
import com.buildsmart.vendor.repository.VendorRepository;
import com.buildsmart.vendor.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final VendorRepository vendorRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public ContractResponse createContract(ContractRequest request) {
        ApplicationLogger.log.info("Creating contract");

        Vendor vendor = vendorRepository.findById(request.vendorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vendor not found: " + request.vendorId()));

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found: " + request.projectId()));

        Contract last = contractRepository.findTopByOrderByContractIdDesc();

        Contract contract = new Contract();
        contract.setContractId(
                IdGeneratorUtil.nextContractId(last == null ? null : last.getContractId()));
        contract.setVendor(vendor);
        contract.setProject(project);
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setValue(request.value());
        contract.setStatus("ACTIVE");

        return toResponse(contractRepository.save(contract));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractResponse> getContractsByProjectProjectId(String projectId) {
        return contractRepository
                .findByProjectProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractResponse> getAllContracts() {
        ApplicationLogger.log.info("Fetching all contracts");

        return contractRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ContractResponse updateContract(String contractId, ContractRequest request) {
        ApplicationLogger.log.info("Updating contract with id: {}", contractId);

        Contract contract = contractRepository.findByContractId(contractId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contract not found: " + contractId));

        Vendor vendor = vendorRepository.findById(request.vendorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vendor not found: " + request.vendorId()));

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found: " + request.projectId()));

        contract.setVendor(vendor);
        contract.setProject(project);
        contract.setStartDate(request.startDate());
        contract.setEndDate(request.endDate());
        contract.setValue(request.value());
        // status preserved unless explicitly changed later

        return toResponse(contractRepository.save(contract));
    }

    @Override
    @Transactional
    public ContractResponse updateContractStatus(String contractId, String status) {
        ApplicationLogger.log.info("Updating contract status: {} -> {}", contractId, status);

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contract not found: " + contractId));

        contract.setStatus(status);
        return toResponse(contractRepository.save(contract));
    }

    @Override
    @Transactional
    public ContractResponse deleteContract(String contractId) {
        ApplicationLogger.log.info("Deleting contract with id: {}", contractId);

        Contract contract = contractRepository.findByContractId(contractId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contract not found: " + contractId));

        contractRepository.delete(contract);

        return toResponse(contract);
    }


    private ContractResponse toResponse(Contract contract) {
        return ContractResponse.of(
                contract.getContractId(),
                contract.getVendor().getVendorId(),
                contract.getProject().getProjectId(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getValue(),
                contract.getStatus()
        );
    }
}