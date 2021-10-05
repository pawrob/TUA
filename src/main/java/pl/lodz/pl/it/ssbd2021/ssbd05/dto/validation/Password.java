package pl.lodz.pl.it.ssbd2021.ssbd05.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Interfejs który stanowi anotacje możliwą do użycia na polach do validacji.
 * Reguły sprawdzające zostały zaimplementowane w PasswordValidation.class.
 * W przypadku niepoprawnej walidacji komunikat błędu stanowi tekst zawarty w message().
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidation.class)
public @interface Password {
    String message() default "Password is not valid";

    Class<?>[] groups() default {};

    public abstract Class<? extends Payload>[] payload() default {};
}
