package com.sirmeows.fluffyinventoryservice.util;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.PngWriter;
import com.sirmeows.fluffyinventoryservice.exception.InvalidImageException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Sanitizes uploaded images. Scrimage does the image mechanics (decode, re-encode); every
 * trust decision — format allowlist, decompression-bomb guard — is explicit code here.
 */
public final class ImageSanitizationUtil {

    private static final int MAX_DIMENSION = 4096;
    private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC = {(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A};

    private ImageSanitizationUtil() { }

    /**
     * The full sanitization pipeline: read -> allowlist format by magic bytes -> cap
     * dimensions before decoding -> re-encode to PNG. Only the re-encoded bytes should
     * ever be stored; the original upload (and anything hidden in it: EXIF, polyglot
     * payloads) is discarded.
     *
     * @param file the multipart upload; its Content-Type and filename are never trusted
     * @return freshly encoded PNG bytes, safe to persist
     * @throws InvalidImageException if the upload is missing, not JPEG/PNG, exceeds the
     *         dimension limit, or cannot be decoded
     */
    public static byte[] sanitizeToPng(MultipartFile file) {
        var bytes = readBytes(file);
        assertAllowedFormat(bytes);
        assertHeaderDimensionsWithinLimit(bytes);
        return reencodeAsPng(bytes);
    }

    /** Rejects empty/unreadable uploads; size is already capped by the multipart config. */
    private static byte[] readBytes(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidImageException("No file provided");
        }
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new InvalidImageException("Could not read upload", e);
        }
    }

    /**
     * Allows only JPEG and PNG, decided from the file's magic bytes — the client-sent
     * Content-Type and filename are spoofable and never trusted.
     */
    private static void assertAllowedFormat(byte[] bytes) {
        if (startsWith(bytes, JPEG_MAGIC) || startsWith(bytes, PNG_MAGIC)) {
            return;
        }
        throw new InvalidImageException("Only JPEG and PNG images are accepted");
    }

    private static boolean startsWith(byte[] bytes, byte[] prefix) {
        if (bytes.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (bytes[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private record Dimensions(int width, int height) {}

    /**
     * Decompression-bomb guard: reads the dimensions from the image header WITHOUT decoding
     * pixel data and rejects oversized images — a tiny compressed file can otherwise expand
     * to gigabytes of pixels. Uses a raw ImageIO reader because Scrimage always fully
     * decodes on load.
     */
    private static void assertHeaderDimensionsWithinLimit(byte[] bytes) {
        var dimensions = readHeaderDimensions(bytes);
        if (dimensions.width() > MAX_DIMENSION || dimensions.height() > MAX_DIMENSION) {
            throw new InvalidImageException("Image dimensions exceed " + MAX_DIMENSION + "px");
        }
    }

    /** Opens the bytes as an ImageIO stream and translates read failures to a rejection. */
    private static Dimensions readHeaderDimensions(byte[] bytes) {
        try (var inputStream = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            return readDimensions(inputStream);
        } catch (IOException e) {
            throw new InvalidImageException("Unreadable image", e);
        }
    }

    /** Reads width/height from the header; getWidth/getHeight(0) never decode pixel data. */
    private static Dimensions readDimensions(ImageInputStream inputStream) throws IOException {
        var reader = firstReaderFor(inputStream);
        try {
            reader.setInput(inputStream);
            return new Dimensions(reader.getWidth(0), reader.getHeight(0));
        } finally {
            reader.dispose();
        }
    }

    /** A reader whose format plugin recognizes the stream's header — none means not an image. */
    private static ImageReader firstReaderFor(ImageInputStream inputStream) {
        var readers = ImageIO.getImageReaders(inputStream);
        if (!readers.hasNext()) {
            throw new InvalidImageException("Unreadable image");
        }
        return readers.next();
    }

    /**
     * Decodes and re-encodes to PNG via Scrimage. Re-encoding is the core defence: the
     * stored bytes are freshly generated pixels, so payloads smuggled in the original file
     * never reach the database. Failure to decode means it wasn't a real image -> reject.
     */
    private static byte[] reencodeAsPng(byte[] bytes) {
        try {
            return ImmutableImage.loader().fromBytes(bytes).bytes(PngWriter.MaxCompression);
        } catch (IOException e) {
            throw new InvalidImageException("Image could not be re-encoded", e);
        }
    }
}
