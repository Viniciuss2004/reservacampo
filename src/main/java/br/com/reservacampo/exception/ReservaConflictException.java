package br.com.reservacampo.exception;

public class ReservaConflictException extends RuntimeException {

    public ReservaConflictException(String mensagem) {
        super(mensagem);
    }
}

