package staffme.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED, reason = "Passwords do not match!")
public class PasswordsNotEqualException extends RuntimeException {

    public PasswordsNotEqualException(String message) {
        super(message);
    }

}
