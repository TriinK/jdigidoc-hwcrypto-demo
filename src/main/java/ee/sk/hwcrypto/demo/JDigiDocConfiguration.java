package ee.sk.hwcrypto.demo;

import ee.sk.digidoc.DigiDocException;
import ee.sk.utils.ConfigManager;
import org.apache.commons.compress.utils.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;

@Component
public class JDigiDocConfiguration {

    private static final Logger log = LoggerFactory.getLogger(JDigiDocConfiguration.class);
    private static final String JDIGIDOC_CONF_FILE = "/jdigidoc.cfg";
    private static final Object lock = new Object();

    @PostConstruct
    public static void initConfig() throws DigiDocException, IOException, SAXNotSupportedException, SAXNotRecognizedException, ParserConfigurationException {
        synchronized (lock) {
            final File file = File.createTempFile("jdigidoc-", ".cfg");
            try (
                    InputStream inputStream = JDigiDocConfiguration.class.getResourceAsStream(JDIGIDOC_CONF_FILE);
                    OutputStream outputStream = new FileOutputStream(file)
            ) {
                if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                    Security.addProvider(new BouncyCastleProvider());
                }
                log.info("Copying DDOC configuration file: {}", file.getAbsolutePath());
                log.info("jdigidoc.cfg original path: {}", JDigiDocConfiguration.class.getResource(JDIGIDOC_CONF_FILE));

                IOUtils.copy(inputStream, outputStream);
                log.info("jdigidoc.cfg contents: {}", Files.readAllLines(Paths.get(file.getAbsolutePath())));
            } finally {
                log.info("Removing configuration file: {}", file.getAbsolutePath());
                file.deleteOnExit();
            }
            ConfigManager.init(file.getAbsolutePath());
            log.info("DDOC hashcode support in configuration load is: {}", ConfigManager.instance().getProperty("DATAFILE_HASHCODE_MODE"));
        }
    }

} 
