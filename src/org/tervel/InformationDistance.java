package org.tervel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * Utility class for computing an approximate information distance between two strings.
 */
public class InformationDistance {

    /**
     * Computes the “entropy” (i.e., compressed size) of the given string {@code s}
     * when compressed with raw-deflate (no zlib header) at BEST_COMPRESSION level,
     * optionally seeded with a preset dictionary.
     *
     * @param s    the input string to compress; will be converted to lowercase UTF-8 bytes
     * @param dict an optional preset dictionary as a byte array, or {@code null} for no dictionary
     * @return the number of bytes in the compressed output
     * @throws RuntimeException if an {@link IOException} occurs during compression
     */
    private static int entropy(String s, byte[] dict) {
        final var deflater = new Deflater(Deflater.BEST_COMPRESSION, true);
        if (null != dict) {
            deflater.setDictionary(dict);
        }
        try (final var baos = new ByteArrayOutputStream();
             final var dos = new DeflaterOutputStream(baos, deflater)) {
            dos.write(s.getBytes(StandardCharsets.UTF_8));
            dos.finish();
            return baos.size();
        } catch (IOException e) {
            throw new RuntimeException("Compression failed", e);
        } finally {
            deflater.end();
        }
    }

    /**
     * Computes a symmetric information distance between two strings {@code x} and {@code y}.
     * <p>
     * This method guarantees:
     * <ul>
     *   <li>Commutativity: {@code distance(a, b) == distance(b, a)}</li>
     *   <li>Identity:      {@code distance(a, a) == 0}</li>
     * </ul>
     *
     * @param x the first input string
     * @param y the second input string
     * @return  the calibrated distance between the two strings
     */
    public static int distance(String x, String y) {
        x = x.toLowerCase();
        y = y.toLowerCase();
        final var yDict       = y.getBytes(StandardCharsets.UTF_8);
        final var xDict       = x.getBytes(StandardCharsets.UTF_8);

        final var xentropy = entropy(x, yDict) + entropy(x, null)
                - entropy("", yDict);
        final var yentropy = entropy(y, xDict) + entropy(y, null)
                - entropy("", xDict);

        return Math.abs(xentropy - yentropy);
    }

    public static void main(String[] args) {
        System.out.println(distance(args[0], args[1]));
    }
}
