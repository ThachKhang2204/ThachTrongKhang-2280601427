package Nhom5.ThachTrongKhang.Validator.annotation;

import jakarta.validation.Constraint; 
import jakarta.validation.Payload; 
import java.lang.annotation.Retention; 
import java.lang.annotation.Target;

import Nhom5.ThachTrongKhang.Validator.ValidUsernameValidator;

import static java.lang.annotation.ElementType.FIELD; 
import static java.lang.annotation.ElementType.TYPE; 
import static java.lang.annotation.RetentionPolicy.RUNTIME;
@Target({TYPE, FIELD}) 
@Retention(RUNTIME) 
@Constraint(validatedBy = ValidUsernameValidator.class) 
public @interface ValidUsername { 
    String message() default "Username already exists"; 
    Class<?>[] groups() default {}; 
    Class<? extends Payload>[] payload() default {}; 
} 
