package ee.sk.hwcrypto.demo.validation.model;

import java.util.ArrayList;
import java.util.List;

public class SignatureValidationResult {

    private boolean valid;
    private List<String> errors = new ArrayList<>();

    public SignatureValidationResult(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        errors.add(error);
    }

}
