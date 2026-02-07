package Nhom5.ThachTrongKhang.Validator;

import Nhom5.ThachTrongKhang.Validator.annotation.ValidCategoryId;
import Nhom5.ThachTrongKhang.entities.Category;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCategoryIdValidator implements
        ConstraintValidator<ValidCategoryId, Category> {
    @Override
    public boolean isValid(Category category,
            ConstraintValidatorContext context) {
        return category != null && category.getId() != null;
    }
}