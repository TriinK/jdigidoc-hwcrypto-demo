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
package ee.sk.hwcrypto.demo.signature;

import ee.sk.digidoc.DataFile;
import ee.sk.digidoc.DigiDocException;
import ee.sk.digidoc.Signature;
import ee.sk.digidoc.SignedDoc;
import ee.sk.digidoc.factory.DigiDocGenFactory;
import ee.sk.utils.ConfigManager;
import org.apache.commons.compress.utils.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Service
public class FileSigner {

    private static final Logger log = LoggerFactory.getLogger(FileSigner.class);

    private final Object lock = new Object();

    public SignedDoc createDDocContainer(byte[] dataFileBytes, String fileName, String mimeType) throws DigiDocException {
        SignedDoc signedDoc = new SignedDoc("DIGIDOC-XML", "1.3");
        signedDoc.setProfile(SignedDoc.BDOC_PROFILE_TM);
        DataFile dataFile = new DataFile(signedDoc.getNewDataFileId(), DataFile.CONTENT_EMBEDDED_BASE64, fileName, mimeType, signedDoc);
        dataFile.setBody(dataFileBytes);
        signedDoc.addDataFile(dataFile);
        return signedDoc;
    }

    public SignedDoc createBDocContainer(byte[] dataFileBytes, String fileName, String mimeType) throws DigiDocException {
        SignedDoc signedDoc = new SignedDoc("BDOC", "2.1");
        signedDoc.setProfile(SignedDoc.BDOC_PROFILE_TM);
        DataFile dataFile = new DataFile(fileName, DataFile.CONTENT_BINARY, fileName, mimeType, signedDoc);
        dataFile.setBody(dataFileBytes);
        signedDoc.addDataFile(dataFile);
        return signedDoc;
    }

    public byte[] getDataToSign(SignedDoc signedDocToSign, String certificateInHex) throws DigiDocException {
        synchronized (lock) {
            X509Certificate certificate = getCertificate(certificateInHex);
            Signature signature = DigiDocGenFactory.prepareXadesBES(signedDocToSign, null, certificate, null, null, null, null, null);
            return signature.getSignedInfo().calculateDigest();
        }
    }

    public void signContainer(SignedDoc signedDoc, String signatureInHex) throws DigiDocException {
        byte[] signatureBytes = DatatypeConverter.parseHexBinary(signatureInHex);
        Signature signature = signedDoc.getSignature(0);
        signature.setSignatureValue(signatureBytes);
        signature.getConfirmation();
    }

    private X509Certificate getCertificate(String certificateInHex) {
        byte[] certificateBytes = DatatypeConverter.parseHexBinary(certificateInHex);
        try (InputStream inStream = new ByteArrayInputStream(certificateBytes)) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate)cf.generateCertificate(inStream);
            return certificate;
        } catch (CertificateException | IOException e) {
            log.error("Error reading certificate: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
