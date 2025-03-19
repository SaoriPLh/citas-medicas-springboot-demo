package com.saori.citas_medicas.exceptions;

public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(String message){
        super(message);
    }

}
