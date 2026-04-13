package com.iict.buet.customer_portal.aspect;

import com.iict.buet.customer_portal.dto.Response;
import com.iict.buet.customer_portal.util.ResponseBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

@Aspect
@Configuration
public class DataValidationAspect {
    @Around("@annotation(com.iict.buet.customer_portal.annotations.DataValidation) && args(..))")
    public Response createValidate(ProceedingJoinPoint joinPoint) {
        Object[] signatureArgs = joinPoint.getArgs();
        BindingResult bindingResult = null;
        for (int i = 0; i < signatureArgs.length; i++) {
            if(signatureArgs[i] instanceof BindingResult){
                bindingResult = (BindingResult) signatureArgs[i];
            }
        }
        if(bindingResult != null && bindingResult.hasErrors()){
            return ResponseBuilder.getFailResponse(bindingResult, "Backend validation failed");
        }
        try {
            Response response = (Response) joinPoint.proceed();
            return response;
        }catch (Throwable throwable) {
            return ResponseBuilder.getFailResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server error");
        }
    }

    @Around("@annotation(com.iict.buet.customer_portal.annotations.DataValidation2) && args(..))")
    public ResponseEntity<Response> createValidate2(ProceedingJoinPoint joinPoint) {
        Object[] signatureArgs = joinPoint.getArgs();
        BindingResult bindingResult = null;
        for (int i = 0; i < signatureArgs.length; i++) {
            if(signatureArgs[i] instanceof BindingResult){
                bindingResult = (BindingResult) signatureArgs[i];
            }
        }
        if (bindingResult != null && bindingResult.hasErrors()){
            return ResponseBuilder.getFailureResponseInEntity(bindingResult, "Backend validation failed");
        }
        try {
            ResponseEntity response = (ResponseEntity) joinPoint.proceed();
            return response;
        } catch (Throwable throwable) {
            return ResponseBuilder.getFailureResponseInEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server error");
        }
    }
}
