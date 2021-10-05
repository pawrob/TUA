package pl.lodz.pl.it.ssbd2021.ssbd05.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Walidator poziomu dostępu
 */
public class AccessLevelValidation implements ConstraintValidator<AccessLevel, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return (value.equalsIgnoreCase("ENTERTAINER") ||
                value.equalsIgnoreCase("CLIENT") ||
                value.equalsIgnoreCase("MANAGEMENT"));
    }
}
