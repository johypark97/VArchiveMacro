package com.github.johypark97.varchivemacro.macro.common.programdata.infra.utility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Sha256FileHash {
    public static final String HASH_ALGORITHM = "SHA-256";

    public static String calculate(Path path) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(HASH_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        try (InputStream stream = new BufferedInputStream(Files.newInputStream(path));
                DigestInputStream x = new DigestInputStream(stream, digest)) {
            while (x.read() != -1)
                ;
        }

        return HexFormat.of().formatHex(digest.digest());
    }
}
