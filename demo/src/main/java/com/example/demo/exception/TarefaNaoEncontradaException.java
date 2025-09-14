package com.example.demo.exception;

public class TarefaNaoEncontradaException extends RuntimeException{
    public TarefaNaoEncontradaException(String message){
        super(message);
    }
}
