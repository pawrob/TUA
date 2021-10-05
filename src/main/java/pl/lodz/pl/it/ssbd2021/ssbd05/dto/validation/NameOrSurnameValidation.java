package pl.lodz.pl.it.ssbd2021.ssbd05.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Walidator imienia lub nazwiska
 */
public class NameOrSurnameValidation implements ConstraintValidator<NameOrSurname, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Pattern p = Pattern.compile("^[a-z\\sżźćńółęąś]+$");
        return p.matcher(value.toLowerCase()).matches();
    }
}
