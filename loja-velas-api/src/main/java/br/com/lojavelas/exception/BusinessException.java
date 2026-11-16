package br.com.lojavelas.exception;

/** Violação de regra de negócio (HTTP 422). */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensagem) {
        super(mensagem);
    }
}
