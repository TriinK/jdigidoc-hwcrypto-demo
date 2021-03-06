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
package ee.sk.hwcrypto.demo.signature.model;

public class Result {

    public static final String OK = "ok";
    public static final String ERROR_GENERATING_HASH = "error_generating_hash";
    public static final String ERROR_UPLOADING = "error_uploading_file";
    public static final String ERROR_SIGNING = "error_signing_file";

    private String result;

    public Result() {
    }

    public Result(String result) {
        this.result = result;
    }

    public static Result resultOk() {
        return new Result(OK);
    }

    public static Result resultUploadingError() {
        return new Result(ERROR_UPLOADING);
    }

    public static Result resultSigningError() {
        return new Result(ERROR_SIGNING);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
