package com.example.demo.exception;

public class CategoriaNaoEncontradaException extends RuntimeException{
    public CategoriaNaoEncontradaException(String message) {
        super(message);
    }
}
