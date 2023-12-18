package com.challenge.challenge.exception;

import java.util.Date;

public class UserAlreadyExist extends RuntimeException{
    private String message;
    private Date date;

    public UserAlreadyExist (String message){
        this.message=message;
        this.date=new Date();
    }
}
