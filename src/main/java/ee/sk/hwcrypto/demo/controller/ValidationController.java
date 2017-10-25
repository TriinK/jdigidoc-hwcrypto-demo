package ee.sk.hwcrypto.demo.controller;

import ee.sk.digidoc.DigiDocException;
import ee.sk.hwcrypto.demo.validation.DocumentValidator;
import ee.sk.hwcrypto.demo.validation.model.SignatureValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
public class ValidationController {

    private static final Logger log = LoggerFactory.getLogger(ValidationController.class);

    @Autowired
    DocumentValidator documentValidator;

    @RequestMapping(value="/validate", method=RequestMethod.POST)
    public Map<String, SignatureValidationResult> validateDocument(@RequestParam MultipartFile document) throws IOException, DigiDocException {
        log.debug("Validating document" + document.getOriginalFilename());
        return documentValidator.validate(document.getOriginalFilename(), document.getBytes());
    }

} 
