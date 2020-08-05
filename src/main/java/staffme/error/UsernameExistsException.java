package staffme.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Username exists")
public class UsernameExistsException extends RuntimeException{

    public UsernameExistsException(String message) {
        super(message);
    }
}
