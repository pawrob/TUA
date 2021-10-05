package pl.lodz.pl.it.ssbd2021.ssbd05.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Walidator adresu IP
 */
public class IpAddressValidation implements ConstraintValidator<IpAddress, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Pattern p = Pattern.compile("^(([0-2]?[0-9]?[0-9])?\\s?[.]){3}([0-2]?[0-9]?[0-9])?\\s?$");
        return p.matcher(value.toLowerCase()).matches();
    }
}
