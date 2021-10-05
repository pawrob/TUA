package pl.lodz.pl.it.ssbd2021.ssbd05.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Walidator poprawności hasła
 */
public class PasswordValidation implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        Pattern p = Pattern.compile("^(?!.*[\\s])^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$");
        return p.matcher(value).matches();
    }
}
