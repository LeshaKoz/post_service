package faang.school.postservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PositiveValidator implements ConstraintValidator<Positive, Long> {
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // или false, в зависимости от требований
        }
        return value > 0;
    }
}
