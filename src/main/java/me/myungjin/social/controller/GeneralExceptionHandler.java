package me.myungjin.social.controller;

import me.myungjin.social.error.DuplicateKeyException;
import me.myungjin.social.error.NotFoundException;
import me.myungjin.social.error.ServiceRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import static me.myungjin.social.controller.ApiResult.ERROR;

@ControllerAdvice
public class GeneralExceptionHandler {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private ResponseEntity<ApiResult<?>> newResponse(Throwable throwable, HttpStatus status) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    return new ResponseEntity<>(ERROR(throwable, status), headers, status);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(value = {IllegalStateException.class, IllegalArgumentException.class, TypeMismatchException.class,
                              MissingServletRequestParameterException.class})
  protected ResponseEntity<ApiResult<?>> badRequestHandler(Exception e){
    return newResponse(e, HttpStatus.BAD_REQUEST);
  }

  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  @ExceptionHandler(HttpMediaTypeException.class)
  protected ResponseEntity<ApiResult<?>> unsupportedMediaTypeHandler(Exception e){
    return newResponse(e, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  protected ResponseEntity<ApiResult<?>> methodNotAllowedHandler(Exception e){
    return newResponse(e, HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(ServiceRuntimeException.class)
  protected ResponseEntity<ApiResult<?>> serviceRuntimeExceptionHandler(Exception e){
    if(e instanceof NotFoundException)
      return newResponse(e, HttpStatus.NOT_FOUND);
    if(e instanceof DuplicateKeyException)
      return newResponse(e, HttpStatus.BAD_REQUEST);
    log.warn("Service exception occured: ({})", e.getMessage(), e);
    return newResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ApiResult<?>> exceptionHandler(Exception e){
    log.error("Unexpected exception occurred: {}", e.getMessage(), e);
    return newResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}