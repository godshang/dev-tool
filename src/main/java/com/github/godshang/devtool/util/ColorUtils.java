package com.github.godshang.devtool.util;

import javafx.scene.paint.Color;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.stream.Stream;

public class ColorUtils {

    public static int[] hexToRgb(String hex) {
        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);
        return new int[]{r, g, b};
    }

    public static String rgbToHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x", r, g, b);
    }

    public static double[] rgbToCmyk(int r, int g, int b) {
        double percentageR = r / 255.0;
        double percentageG = g / 255.0;
        double percentageB = b / 255.0;

        double k = 1 - Math.max(Math.max(percentageR, percentageG), percentageB);
        if (k == 1) {
            return new double[]{0, 0, 0, 1};
        }

        double c = (1 - percentageR - k) / (1 - k);
        double m = (1 - percentageG - k) / (1 - k);
        double y = (1 - percentageB - k) / (1 - k);
        return new double[]{c, m, y, k};
    }

    public static int[] cmykToRgb(double c, double m, double y, double k) {
        int r = (int) Math.round(255 * (1 - c) * (1 - k));
        int g = (int) Math.round(255 * (1 - m) * (1 - k));
        int b = (int) Math.round(255 * (1 - y) * (1 - k));
        return new int[]{r, g, b};
    }

    public static double[] rgbToHsl(int r, int g, int b) {
        HSLColor hslColor = new HSLColor(Color.rgb(r, g, b));
        return hslColor.getHSL();
    }

    public static int[] hslToRgb(double h, double s, double l) {
        HSLColor hslColor = new HSLColor(h, s, l);
        Color color = hslColor.getRGB();
        return toRGB(color);
    }

    public static Color parse(String colorString) {
        String color = colorString.toLowerCase(Locale.ROOT);
        if (color.endsWith(")")) {
            color = color.substring(0, color.length() - 1);
        }
        if (color.startsWith("rgb(")) {
            color = color.substring(4);
            int[] rgb = Stream.of(color.split(",")).map(String::trim).mapToInt(Integer::parseInt).toArray();
            return Color.rgb(rgb[0], rgb[1], rgb[2]);
        } else if (color.startsWith("hex(")) {
            color = color.substring(4);
            int[] rgb = hexToRgb(color);
            return Color.rgb(rgb[0], rgb[1], rgb[2]);
        } else if (color.startsWith("cmyk(")) {
            color = color.substring(5);
            double[] cmyk = Stream.of(color.split(",")).map(String::trim).mapToDouble(Double::parseDouble)
                    .map(e -> e / 100.0).toArray();
            int[] rgb = cmykToRgb(cmyk[0], cmyk[1], cmyk[2], cmyk[3]);
            return Color.rgb(rgb[0], rgb[1], rgb[2]);
        } else if (color.startsWith("hsb(") || color.startsWith("hsv(")) {
            color = color.substring(4);
            double[] hsb = Stream.of(color.split(",")).map(String::trim).mapToDouble(Double::parseDouble).toArray();
            return Color.hsb(hsb[0], hsb[1] / 100.0, hsb[2] / 100.0);
        } else if (color.startsWith("hsl(")) {
            color = color.substring(4);
            double[] hsl = Stream.of(color.split(",")).map(String::trim).mapToDouble(Double::parseDouble).toArray();
            HSLColor hslColor = new HSLColor(hsl[0], hsl[1], hsl[2]);
            return hslColor.getRGB();
        }
        return null;
    }

    public static String rgb(Color color) {
        int[] rgb = toRGB(color);
        return "rgb(" + rgb[0] + ", " + rgb[1] + ", " + rgb[2] + ")";
    }

    public static String hex(Color color) {
        int[] rgb = toRGB(color);
        return "hex(" + rgbToHex(rgb[0], rgb[1], rgb[2]) + ")";
    }

    public static String cmyk(Color color) {
        int[] rgb = toRGB(color);
        double[] cmyk = rgbToCmyk(rgb[0], rgb[1], rgb[2]);
        return "cmyk(" + percentage(cmyk[0]) + ", " + percentage(cmyk[1]) + ", " + percentage(cmyk[2]) + ", " + percentage(cmyk[3]) + ")";
    }

    public static String hsb(Color color) {
        return "hsb(" + toString(color.getHue()) + ", " + percentage(color.getSaturation()) + ", " + percentage(color.getBrightness()) + ")";
    }

    public static String hsl(Color color) {
        HSLColor hslColor = new HSLColor(color);
        double[] hsl = hslColor.getHSL();
        return "hsl(" + toString(hsl[0]) + ", " + toString(hsl[1]) + ", " + toString(hsl[2]) + ")";
    }

    private static String percentage(double d) {
        return toString(d * 100.0);
    }

    private static String toString(double d) {
        BigDecimal bigDecimal = new BigDecimal(d);
        return bigDecimal.setScale(2, RoundingMode.HALF_UP).toString();
    }

    private static int[] toRGB(Color color) {
        int[] rgb = new int[]{
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255)
        };
        return rgb;
    }


    private static class HSLColor {
        private Color rgb;
        private double[] hsl;
        private double alpha;

        public HSLColor(Color rgb) {
            this.rgb = rgb;
            hsl = fromRGB(rgb);
            alpha = rgb.getOpacity();
        }

        public HSLColor(double h, double s, double l) {
            this(h, s, l, 1.0f);
        }

        public HSLColor(double h, double s, double l, double alpha) {
            hsl = new double[]{h, s, l};
            this.alpha = alpha;
            rgb = toRGB(hsl, alpha);
        }

        public HSLColor(double[] hsl) {
            this(hsl, 1.0f);
        }

        public HSLColor(double[] hsl, double alpha) {
            this.hsl = hsl;
            this.alpha = alpha;
            rgb = toRGB(hsl, alpha);
        }

        public Color adjustHue(double degrees) {
            return toRGB(degrees, hsl[1], hsl[2], alpha);
        }

        public Color adjustLuminance(double percent) {
            return toRGB(hsl[0], hsl[1], percent, alpha);
        }

        public Color adjustSaturation(double percent) {
            return toRGB(hsl[0], percent, hsl[2], alpha);
        }

        public Color adjustShade(double percent) {
            double multiplier = (100.0f - percent) / 100.0f;
            double l = Math.max(0.0f, hsl[2] * multiplier);

            return toRGB(hsl[0], hsl[1], l, alpha);
        }

        public Color adjustTone(double percent) {
            double multiplier = (100.0f + percent) / 100.0f;
            double l = Math.min(100.0f, hsl[2] * multiplier);

            return toRGB(hsl[0], hsl[1], l, alpha);
        }

        public double getAlpha() {
            return alpha;
        }

        public Color getComplementary() {
            double hue = (hsl[0] + 180.0f) % 360.0f;
            return toRGB(hue, hsl[1], hsl[2]);
        }

        public double getHue() {
            return hsl[0];
        }

        public double[] getHSL() {
            return hsl;
        }

        public double getLuminance() {
            return hsl[2];
        }

        public Color getRGB() {
            return rgb;
        }

        public double getSaturation() {
            return hsl[1];
        }

        public String toString() {
            String toString =
                    "HSLColor[h=" + hsl[0] +
                            ",s=" + hsl[1] +
                            ",l=" + hsl[2] +
                            ",alpha=" + alpha + "]";

            return toString;
        }

        public static double[] fromRGB(Color color) {
            //  Get RGB values in the range 0 - 1

            double r = color.getRed();
            double g = color.getGreen();
            double b = color.getBlue();

            //	Minimum and Maximum RGB values are used in the HSL calculations

            double min = Math.min(r, Math.min(g, b));
            double max = Math.max(r, Math.max(g, b));

            //  Calculate the Hue

            double h = 0;

            if (max == min)
                h = 0;
            else if (max == r)
                h = ((60 * (g - b) / (max - min)) + 360) % 360;
            else if (max == g)
                h = (60 * (b - r) / (max - min)) + 120;
            else if (max == b)
                h = (60 * (r - g) / (max - min)) + 240;

            //  Calculate the Luminance

            double l = (max + min) / 2;
            //System.out.println(max + " : " + min + " : " + l);

            //  Calculate the Saturation

            double s = 0;

            if (max == min)
                s = 0;
            else if (l <= .5f)
                s = (max - min) / (max + min);
            else
                s = (max - min) / (2 - max - min);

            return new double[]{h, s * 100, l * 100};
        }

        public static Color toRGB(double[] hsl) {
            return toRGB(hsl, 1.0f);
        }

        public static Color toRGB(double[] hsl, double alpha) {
            return toRGB(hsl[0], hsl[1], hsl[2], alpha);
        }

        public static Color toRGB(double h, double s, double l) {
            return toRGB(h, s, l, 1.0f);
        }

        public static Color toRGB(double h, double s, double l, double alpha) {
            if (s < 0.0f || s > 100.0f) {
                String message = "Color parameter outside of expected range - Saturation";
                throw new IllegalArgumentException(message);
            }

            if (l < 0.0f || l > 100.0f) {
                String message = "Color parameter outside of expected range - Luminance";
                throw new IllegalArgumentException(message);
            }

            if (alpha < 0.0f || alpha > 1.0f) {
                String message = "Color parameter outside of expected range - Alpha";
                throw new IllegalArgumentException(message);
            }

            //  Formula needs all values between 0 - 1.

            h = h % 360.0f;
            h /= 360f;
            s /= 100f;
            l /= 100f;

            double q = 0;

            if (l < 0.5)
                q = l * (1 + s);
            else
                q = (l + s) - (s * l);

            double p = 2 * l - q;

            double r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
            double g = Math.max(0, HueToRGB(p, q, h));
            double b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

            r = Math.min(r, 1.0f);
            g = Math.min(g, 1.0f);
            b = Math.min(b, 1.0f);

            return new Color(r, g, b, alpha);
        }

        private static double HueToRGB(double p, double q, double h) {
            if (h < 0) h += 1;

            if (h > 1) h -= 1;

            if (6 * h < 1) {
                return p + ((q - p) * 6 * h);
            }

            if (2 * h < 1) {
                return q;
            }

            if (3 * h < 2) {
                return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
            }

            return p;
        }
    }
}
