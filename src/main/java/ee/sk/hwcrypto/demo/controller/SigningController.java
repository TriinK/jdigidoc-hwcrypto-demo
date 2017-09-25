/**
 * JDigiDoc Hwcrypto Demo
 *
 * The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ee.sk.hwcrypto.demo.controller;

import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.SignedDoc;
import ee.sk.hwcrypto.demo.model.Digest;
import ee.sk.hwcrypto.demo.model.Result;
import ee.sk.hwcrypto.demo.model.SigningSessionData;
import ee.sk.hwcrypto.demo.signature.FileSigner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;

@RestController
public class SigningController {

    private static final Logger log = LoggerFactory.getLogger(SigningController.class);
    @Autowired
    private SigningSessionData session;
    @Autowired
    private FileSigner signer;

    @RequestMapping(value="/upload", method= RequestMethod.POST)
    public Result handleUpload(@RequestParam MultipartFile file) {
        log.debug("Handling file upload for file "+file.getOriginalFilename());
        try {
            byte[] dataFileBytes = file.getBytes();
            String fileName = file.getOriginalFilename();
            String mimeType = file.getContentType();
            SignedDoc signedDoc = signer.createBDocContainer(dataFileBytes, fileName, mimeType);
            session.setSignedDoc(signedDoc);
            return Result.resultOk();
        } catch (Exception e) {
            log.error("Error creating container: " + e);
            return Result.resultUploadingError();
        }
    }

    @RequestMapping(value="/generateHash", method = RequestMethod.POST)
    public Digest generateHash(@RequestParam String certInHex) {
        log.debug("Generating hash from cert " + StringUtils.left(certInHex, 10) + "...");
        SignedDoc signedDoc = session.getSignedDoc();
        Digest digest = new Digest();
        try {
            byte[] dataToSign = signer.getDataToSign(signedDoc, certInHex);
            String dataToSignInHex = DatatypeConverter.printHexBinary(dataToSign);
            digest.setHex(dataToSignInHex);
            digest.setResult(Result.OK);
        } catch (DigiDocException e) {
            log.error("Error Calculating data to sign", e);
            digest.setResult(Result.ERROR_GENERATING_HASH);
        }
        return digest;
    }

    @RequestMapping(value="/createContainer", method = RequestMethod.POST)
    public Result createContainer(@RequestParam String signatureInHex) {
        try {
            log.debug("Creating container for signature " + StringUtils.left(signatureInHex, 10) + "...");
            SignedDoc signedDoc = session.getSignedDoc();
            signer.signContainer(signedDoc, signatureInHex);
            session.setSignedDoc(signedDoc);
            return Result.resultOk();
        } catch (DigiDocException e) {
            log.error("Error signing container: " + e.getMessage());
            return Result.resultSigningError();
        }
    }

}
