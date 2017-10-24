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

import ee.sk.digidoc.Signature;
import ee.sk.digidoc.SignedDoc;
import ee.sk.hwcrypto.demo.JDigiDocConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.runners.model.MultipleFailureException.assertEmpty;

public class FileSignerTest {

    private FileSigner fileSigner;

    @Before
    public void setUp() throws Exception {
        fileSigner = new FileSigner();
        JDigiDocConfiguration.initConfig();
    }

    @Test
    public void createContainerFromFile() throws Exception {
        SignedDoc signedDoc = fileSigner.createBDocContainer("Hello".getBytes(), "hello.txt" , "application/text");
        assertEquals(1, signedDoc.getDataFiles().size());
    }

    @Test
    public void gettingDataToSign() throws Exception {
        Pkcs12Signer pkcs12Signer = new Pkcs12Signer("src/test/resources/rsa.p12", "test");
        SignedDoc signedDoc = fileSigner.createBDocContainer("Hello".getBytes(), "hello.txt" , "application/text");
        byte[] digestToSign = fileSigner.getDataToSign(signedDoc, pkcs12Signer.getSigningCertificateInHex());
        assertTrue(digestToSign.length > 0);
    }

    @Test
    public void signDocumentWithRSA() throws Exception {
        Pkcs12Signer pkcs12Signer = new Pkcs12Signer("src/test/resources/rsa.p12", "test");
        SignedDoc signedDoc = fileSigner.createBDocContainer("Hello".getBytes(), "hello.txt" , "application/text");
        byte[] digestToSign = fileSigner.getDataToSign(signedDoc, pkcs12Signer.getSigningCertificateInHex());
        String signatureInHex = pkcs12Signer.signDigest(digestToSign, DigestAlgorithm.SHA256);
        fileSigner.signContainer(signedDoc, signatureInHex);
        assertEquals(1, signedDoc.getSignatures().size());
        assertEmpty(signedDoc.getSignature(0).verify(signedDoc, true, false));
    }

    @Test
    public void signDocumentWithECC() throws Exception {
        Pkcs12Signer pkcs12Signer = new Pkcs12Signer("src/test/resources/ecc_test.p12", "ecc_test");
        SignedDoc signedDoc = fileSigner.createBDocContainer("Hello".getBytes(), "hello.txt" , "application/text");
        byte[] digestToSign = fileSigner.getDataToSign(signedDoc, pkcs12Signer.getSigningCertificateInHex());
        String signatureInHex = pkcs12Signer.signDigest(digestToSign, DigestAlgorithm.SHA256);
        fileSigner.signContainer(signedDoc, signatureInHex);
        assertEquals(1, signedDoc.getSignatures().size());

        Signature signature = signedDoc.getSignature(0);

        // This line is needed in order to fix nullpointer for elliptic curve signature verification
        // when the document is not read into JDigiDoc from bytes, but rather verified right after creating the signature
        signature.getSignedInfo().setOrigXml(signature.calculateSignedInfoXML());

        assertEmpty(signature.verify(signedDoc, true, false));
    }
}
