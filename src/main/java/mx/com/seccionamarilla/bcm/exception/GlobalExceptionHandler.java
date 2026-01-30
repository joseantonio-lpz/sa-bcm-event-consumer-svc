package mx.com.seccionamarilla.bcm.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;


import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import mx.com.seccionamarilla.bcm.model.dto.APIErrorDTO;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Metodo que valida 400 Validación y JSON malformado
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIErrorDTO> handleValidationError(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("Validación fallida: {}", errors);

        return ResponseEntity.badRequest().body(new APIErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errors.toString()
        ));
    }

    /**
     * Metodo que valida 400 Validación y JSON malformado
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIErrorDTO> handleMalformedJson(HttpMessageNotReadableException ex) {
        log.warn("JSON malformado: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new APIErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Malformed JSON",
                "Revisa la sintaxis del cuerpo de la solicitud"
        ));
    }

    /**
     * Metodo que valida 400 Validación y JSON malformado
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIErrorDTO> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Violaciones de constraint: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new APIErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Constraint Violation",
                ex.getMessage()
        ));
    }

    /**
     * 404	Recurso no encontrado	NoSuchElementException
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<APIErrorDTO> handleNotFound(NoSuchElementException ex) {
        log.info("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIErrorDTO(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage()
        ));
    }

    /**
     * 405	Método HTTP inválido	HttpRequestMethodNotSupportedException
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<APIErrorDTO> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        log.warn("Método HTTP no permitido: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(new APIErrorDTO(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method Not Allowed",
                ex.getMessage()
        ));
    }

    /**
     * 500	Errores internos / base datos	DatabaseException, DataAccessException, Exception
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<APIErrorDTO> handleDataAccess(DataAccessException ex) {
        log.error("Error de base de datos: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Database Error",
                ex.getMostSpecificCause().getMessage()
        ));
    }

    /**
     * 500	Errores internos / base datos	DatabaseException, DataAccessException, Exception
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<APIErrorDTO> handleCustomDatabaseException(DatabaseException ex) {
        log.warn("Excepción personalizada capturada: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Custom Database Error",
                ex.getMessage()
        ));
    }

    /**
     * 500	Errores internos / base datos	DatabaseException, DataAccessException, Exception
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIErrorDTO> handleGeneric(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocurrió un error inesperado"
        ));
    }
}
