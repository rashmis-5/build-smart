package com.buildsmart.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with ID: " + id);
    }

    public ResourceNotFoundException(String contract, String contractId) {
    }
}
