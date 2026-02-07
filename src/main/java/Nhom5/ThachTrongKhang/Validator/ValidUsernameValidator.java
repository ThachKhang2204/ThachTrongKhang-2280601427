package Nhom5.ThachTrongKhang.Validator;

import Nhom5.ThachTrongKhang.services.UserService;
import Nhom5.ThachTrongKhang.Validator.annotation.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {
    private final UserService userService;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return userService.findByUsername(username).isEmpty();
    }
}
