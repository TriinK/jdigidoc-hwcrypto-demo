package ee.sk.hwcrypto.demo.validation;

import ee.sk.hwcrypto.demo.JDigiDocConfiguration;
import ee.sk.hwcrypto.demo.validation.model.SignatureValidationResult;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class DocumentValidatorTest {

    private DocumentValidator documentValidator;

    @Before
    public void setUp() throws Exception {
        documentValidator = new DocumentValidator();
        JDigiDocConfiguration.initConfig();
    }

    @Test
    public void validateRSABdocContainer() throws Exception {
        FileInputStream bdocInputStream = new FileInputStream("src/test/resources/valid_rsa.bdoc");
        Map<String, SignatureValidationResult> result = documentValidator.validate("valid_rsa.bdoc", IOUtils.toByteArray(bdocInputStream));
        result.forEach((k, v) -> {
            assertTrue(v.isValid());
            assertTrue(v.getErrors().isEmpty());
        });
    }

    @Test
    public void validateECCBdocContainer() throws Exception {
        FileInputStream bdocInputStream = new FileInputStream("src/test/resources/valid_ecc.bdoc");
        Map<String, SignatureValidationResult> result = documentValidator.validate("valid_ecc.bdoc", IOUtils.toByteArray(bdocInputStream));
        result.forEach((k, v) -> {
            assertTrue(v.isValid());
            assertTrue(v.getErrors().isEmpty());
        });
    }

} 
