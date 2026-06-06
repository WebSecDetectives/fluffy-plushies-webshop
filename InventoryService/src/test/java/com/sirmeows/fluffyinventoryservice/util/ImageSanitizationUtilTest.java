package com.sirmeows.fluffyinventoryservice.util;

import com.sirmeows.fluffyinventoryservice.exception.InvalidImageException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ImageSanitizationUtilTest {

    private static final byte[] PNG_MAGIC = {(byte) 0x89, 'P', 'N', 'G'};

    @Test
    void sanitizeToPng_validPng_returnsPngBytes() throws IOException {
        var upload = fileOf(imageBytes("png", 10, 10));

        var result = ImageSanitizationUtil.sanitizeToPng(upload);

        assertTrue(startsWithPngMagic(result));
    }

    @Test
    void sanitizeToPng_validJpeg_isReencodedToPng() throws IOException {
        var upload = fileOf(imageBytes("jpg", 10, 10));

        var result = ImageSanitizationUtil.sanitizeToPng(upload);

        assertTrue(startsWithPngMagic(result));
    }

    @Test
    void sanitizeToPng_reencodes_neverStoresOriginalBytes() throws IOException {
        var original = imageBytes("png", 10, 10);

        var result = ImageSanitizationUtil.sanitizeToPng(fileOf(original));

        assertFalse(java.util.Arrays.equals(original, result));
    }

    @Test
    void sanitizeToPng_textFileWithSpoofedNameAndContentType_isRejected() {
        var upload = new MockMultipartFile(
                "file", "innocent.png", "image/png", "<script>alert(1)</script>".getBytes(StandardCharsets.UTF_8));

        assertThrows(InvalidImageException.class, () -> ImageSanitizationUtil.sanitizeToPng(upload));
    }

    @Test
    void sanitizeToPng_emptyFile_isRejected() {
        var upload = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        assertThrows(InvalidImageException.class, () -> ImageSanitizationUtil.sanitizeToPng(upload));
    }

    @Test
    void sanitizeToPng_nullFile_isRejected() {
        assertThrows(InvalidImageException.class, () -> ImageSanitizationUtil.sanitizeToPng(null));
    }

    @Test
    void sanitizeToPng_oversizedDimensions_areRejected() throws IOException {
        var upload = fileOf(imageBytes("png", 4097, 1));

        assertThrows(InvalidImageException.class, () -> ImageSanitizationUtil.sanitizeToPng(upload));
    }

    @Test
    void sanitizeToPng_truncatedImage_isRejected() throws IOException {
        var bytes = imageBytes("png", 10, 10);
        var truncated = java.util.Arrays.copyOf(bytes, 12);

        assertThrows(InvalidImageException.class, () -> ImageSanitizationUtil.sanitizeToPng(fileOf(truncated)));
    }

    private static byte[] imageBytes(String format, int width, int height) throws IOException {
        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var output = new ByteArrayOutputStream();
        ImageIO.write(image, format, output);
        return output.toByteArray();
    }

    private static MockMultipartFile fileOf(byte[] bytes) {
        return new MockMultipartFile("file", "upload.bin", "application/octet-stream", bytes);
    }

    private static boolean startsWithPngMagic(byte[] bytes) {
        for (int i = 0; i < PNG_MAGIC.length; i++) {
            if (bytes[i] != PNG_MAGIC[i]) {
                return false;
            }
        }
        return true;
    }
}
