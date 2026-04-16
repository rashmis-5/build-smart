package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.ContractRequest;
import com.buildsmart.vendor.dto.ContractResponse;

import java.util.List;

public interface ContractService {

    ContractResponse createContract(ContractRequest request);

    List<ContractResponse> getContractsByProjectProjectId(String projectId);


    List<ContractResponse> getAllContracts();

    ContractResponse updateContract(String contractId, ContractRequest request);
    ContractResponse updateContractStatus(String contractId, String status);

    ContractResponse deleteContract(String contractId);

}