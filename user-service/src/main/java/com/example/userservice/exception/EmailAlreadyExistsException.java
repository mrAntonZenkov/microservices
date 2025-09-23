package com.example.userservice.exception;

public class EmailAlreadyExistsException extends Exception{
    public EmailAlreadyExistsException(String email){
        super("Email " + email + " is already in use");
    }
}
