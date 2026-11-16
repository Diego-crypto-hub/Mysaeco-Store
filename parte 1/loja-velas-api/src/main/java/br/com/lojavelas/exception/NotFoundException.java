package br.com.lojavelas.exception;

/** Recurso não encontrado (HTTP 404). */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String recurso, Object id) {
        super(recurso + " não encontrado(a): " + id);
    }
}
