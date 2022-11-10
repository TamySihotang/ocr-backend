package com.danamon.exception;

import com.danamon.enums.StatusCode;
import com.danamon.vo.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalDefaultExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger log = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    private static final String ERROR = "ERROR";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> defaultErrorHandler(HttpServletRequest req, Exception e) {

        String urlInfo = req.getMethod() + " " + req.getRequestURL();
        log.error("endpoint :: {}", urlInfo);
        log.error("Caused by:: {}",e.getClass().getName());
        log.error(ERROR, e);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ResultVO restResponseVO = new ResultVO();
        restResponseVO.setResult(e.getMessage());
        restResponseVO.setMessage(StatusCode.ERROR.name());

        return new ResponseEntity<>(restResponseVO, status);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers, @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {

        log.error(ERROR, ex);

        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }

        ResultVO restResponseVO = new ResultVO();
        restResponseVO.setResult(details.toString());
        restResponseVO.setMessage(StatusCode.BAD_REQUEST.name());

        return new ResponseEntity<>(restResponseVO, HttpStatus.BAD_REQUEST);
    }
}
