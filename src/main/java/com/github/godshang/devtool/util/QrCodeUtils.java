package com.github.godshang.devtool.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class QrCodeUtils {

    public static WritableImage createQR(String data, int width, int height) throws WriterException, IOException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix matrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public static void writeQR(String data, File output, int width, int height) throws WriterException, IOException {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix matrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToPath(matrix, FileUtils.getExtensionName(output), output.toPath());
    }

    public static String readQR(File input) throws IOException, NotFoundException {
        BufferedImage bufferedImage = ImageIO.read(input);
        return readQR(bufferedImage);
    }

    public static String readQR(WritableImage writableImage) throws NotFoundException {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
        return readQR(bufferedImage);
    }

    public static String readQR(BufferedImage bufferedImage) throws NotFoundException {
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new GlobalHistogramBinarizer(source));

        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText();
    }
}
