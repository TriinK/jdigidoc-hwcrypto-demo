package ee.sk.hwcrypto.demo.signature;

import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

import java.io.IOException;
import java.math.BigInteger;

public class DsaSignature {
    private final BigInteger r;
    private final BigInteger s;

    public DsaSignature(BigInteger r, BigInteger s) {
        this.r = r;
        this.s = s;
    }

    public static DsaSignature fromAsn1Encoding(byte[] asn1DsaSignature) {
        try {
            ASN1Sequence sequence = (ASN1Sequence) ASN1Primitive.fromByteArray(asn1DsaSignature);
            BigInteger r = ASN1Integer.getInstance(sequence.getObjectAt(0)).getValue();
            BigInteger s = ASN1Integer.getInstance(sequence.getObjectAt(1)).getValue();
            return new DsaSignature(r, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] encodeInCvc(int fieldSizeInBits) {
        byte[] rBytes = toByteArrayWithoutLeadingZero(r);
        byte[] sBytes = toByteArrayWithoutLeadingZero(s);

        int itemLength = fieldSizeInBytes(fieldSizeInBits);

        return ArrayUtils.addAll(padLeftWithZeroes(rBytes, itemLength), padLeftWithZeroes(sBytes, itemLength));
    }

    public static int fieldSizeInBytes(int fieldSizeInBits) {
        return (int) Math.ceil((double) fieldSizeInBits / 8);
    }

    public static byte[] padLeftWithZeroes(byte[] array, int requiredLength) {
        if (array.length >= requiredLength) {
            return array;
        }
        return ArrayUtils.addAll(new byte[requiredLength - array.length], array);
    }

    public static byte[][] splitArrayInTheMiddle(byte[] array) {
        return new byte[][] {ArrayUtils.subarray(array, 0, array.length / 2), ArrayUtils.subarray(array, array.length / 2, array.length)};
    }

    public static byte[] toByteArrayWithoutLeadingZero(BigInteger value) {
        byte[] result = value.toByteArray();

        if (result[0] == 0) {
            return ArrayUtils.subarray(result, 1, result.length);
        }

        return result;
    }
}
