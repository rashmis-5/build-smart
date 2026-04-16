package com.buildsmart.vendor.service;

import com.buildsmart.vendor.dto.DeliveryRequest;
import com.buildsmart.vendor.dto.DeliveryResponse;

import java.util.List;

public interface DeliveryService {

    DeliveryResponse createDelivery(DeliveryRequest request);

    List<DeliveryResponse> getAllDeliveries();

    List<DeliveryResponse> getDeliveriesByContractId(String contractId);

    DeliveryResponse updateDelivery(String deliveryId, DeliveryRequest request);

    void deleteDelivery(String deliveryId);

    DeliveryResponse updateDeliveryStatus(String deliveryId, String status);

}