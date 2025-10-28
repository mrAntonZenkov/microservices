package org.example.productservice.exception;

public class ProductKafkaException extends RuntimeException{
    public ProductKafkaException(String message, Throwable cause){
        super(message, cause);
    }
}
