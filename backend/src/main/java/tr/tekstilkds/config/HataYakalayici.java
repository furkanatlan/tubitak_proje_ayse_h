package tr.tekstilkds.config;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HataYakalayici {

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail bulunamadi(EntityNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail gecersiz(MethodArgumentNotValidException e) {
        var detay = e.getBindingResult().getFieldErrors().stream()
                .map(h -> h.getField() + ": " + h.getDefaultMessage())
                .toList();
        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Geçersiz istek");
        problem.setProperty("hatalar", detay);
        return problem;
    }
}
