package ee.sk.hwcrypto.demo.validation;

import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.Signature;
import ee.sk.digidoc.SignedDoc;
import ee.sk.digidoc.factory.DigiDocFactory;
import ee.sk.hwcrypto.demo.signature.FileSigner;
import ee.sk.hwcrypto.demo.validation.model.SignatureValidationResult;
import ee.sk.utils.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DocumentValidator {

    private static final Logger log = LoggerFactory.getLogger(FileSigner.class);

    private static final String JDIGIDOC_CONF_FILE = "/jdigidoc.cfg";

    private final Object lock = new Object();

    @SuppressWarnings("unchecked")
    public Map<String, SignatureValidationResult> validate(String fileName, byte[] documentBytes) throws DigiDocException {
        DigiDocFactory digiDocFactory = ConfigManager.instance().getDigiDocFactory();
        List<DigiDocException> signedDocInitializationErrors = new ArrayList<>();
        SignedDoc signedDoc = digiDocFactory.readSignedDocFromStreamOfType(new ByteArrayInputStream(documentBytes), fileName.endsWith(".bdoc"), signedDocInitializationErrors);

        Map<String, SignatureValidationResult> result = new HashMap<>();

        for (Signature signature : getSignatures(signedDoc)) {
            List<DigiDocException> signatureValidationErrors = signature.validate();

            // This line is needed in order to fix nullpointer for verification of elliptic curve signatures
            // when the document is not read into JDigiDoc from bytes, but rather verified right after creating the signature
            signature.getSignedInfo().setOrigXml(signature.calculateSignedInfoXML());

            List<DigiDocException> signatureVerificationErrors = signature.verify(signedDoc, true, true);

            List<DigiDocException> signatureErrors = new ArrayList<>(signatureValidationErrors);
            signatureErrors.addAll(signatureVerificationErrors);

            SignatureValidationResult signatureValidationResult = composeSignatureValidationResult(signatureErrors);
            result.put(signature.getId(), signatureValidationResult);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<Signature> getSignatures(SignedDoc signedDoc) {
        if (signedDoc.getSignatures() == null) {
            return new ArrayList<>();
        }
        return signedDoc.getSignatures();
    }

    private SignatureValidationResult composeSignatureValidationResult(List<DigiDocException> signatureErrors) {;
        if (signatureErrors.isEmpty()) {
            return new SignatureValidationResult(true);
        } else {
            SignatureValidationResult signatureValidationResult = new SignatureValidationResult(false);
            for (DigiDocException error : signatureErrors) {
                signatureValidationResult.addError(error.getMessage());
            }
            return signatureValidationResult;
        }
    }

} 
