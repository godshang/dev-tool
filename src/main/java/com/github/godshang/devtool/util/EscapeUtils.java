package com.github.godshang.devtool.util;

import java.util.HashMap;
import java.util.Map;

public class EscapeUtils {

    private static final Map<Character, String> htmlEscapeTable = new HashMap<>();
    private static final Map<String, Character> htmlUnescapeTable = new HashMap<>();

    static {
        // Special characters for HTML
        htmlEscapeTable.put('\u0026', "&amp;");
        htmlEscapeTable.put('\u003C', "&lt;");
        htmlEscapeTable.put('\u003E', "&gt;");
        htmlEscapeTable.put('\u0022', "&quot;");

        htmlEscapeTable.put('\u0152', "&OElig;");
        htmlEscapeTable.put('\u0153', "&oelig;");
        htmlEscapeTable.put('\u0160', "&Scaron;");
        htmlEscapeTable.put('\u0161', "&scaron;");
        htmlEscapeTable.put('\u0178', "&Yuml;");
        htmlEscapeTable.put('\u02C6', "&circ;");
        htmlEscapeTable.put('\u02DC', "&tilde;");
        htmlEscapeTable.put('\u2002', "&ensp;");
        htmlEscapeTable.put('\u2003', "&emsp;");
        htmlEscapeTable.put('\u2009', "&thinsp;");
        htmlEscapeTable.put('\u200C', "&zwnj;");
        htmlEscapeTable.put('\u200D', "&zwj;");
        htmlEscapeTable.put('\u200E', "&lrm;");
        htmlEscapeTable.put('\u200F', "&rlm;");
        htmlEscapeTable.put('\u2013', "&ndash;");
        htmlEscapeTable.put('\u2014', "&mdash;");
        htmlEscapeTable.put('\u2018', "&lsquo;");
        htmlEscapeTable.put('\u2019', "&rsquo;");
        htmlEscapeTable.put('\u201A', "&sbquo;");
        htmlEscapeTable.put('\u201C', "&ldquo;");
        htmlEscapeTable.put('\u201D', "&rdquo;");
        htmlEscapeTable.put('\u201E', "&bdquo;");
        htmlEscapeTable.put('\u2020', "&dagger;");
        htmlEscapeTable.put('\u2021', "&Dagger;");
        htmlEscapeTable.put('\u2030', "&permil;");
        htmlEscapeTable.put('\u2039', "&lsaquo;");
        htmlEscapeTable.put('\u203A', "&rsaquo;");
        htmlEscapeTable.put('\u20AC', "&euro;");

        // Character entity references for ISO 8859-1 characters
        htmlEscapeTable.put('\u00A0', "&nbsp;");
        htmlEscapeTable.put('\u00A1', "&iexcl;");
        htmlEscapeTable.put('\u00A2', "&cent;");
        htmlEscapeTable.put('\u00A3', "&pound;");
        htmlEscapeTable.put('\u00A4', "&curren;");
        htmlEscapeTable.put('\u00A5', "&yen;");
        htmlEscapeTable.put('\u00A6', "&brvbar;");
        htmlEscapeTable.put('\u00A7', "&sect;");
        htmlEscapeTable.put('\u00A8', "&uml;");
        htmlEscapeTable.put('\u00A9', "&copy;");
        htmlEscapeTable.put('\u00AA', "&ordf;");
        htmlEscapeTable.put('\u00AB', "&laquo;");
        htmlEscapeTable.put('\u00AC', "&not;");
        htmlEscapeTable.put('\u00AD', "&shy;");
        htmlEscapeTable.put('\u00AE', "&reg;");
        htmlEscapeTable.put('\u00AF', "&macr;");
        htmlEscapeTable.put('\u00B0', "&deg;");
        htmlEscapeTable.put('\u00B1', "&plusmn;");
        htmlEscapeTable.put('\u00B2', "&sup2;");
        htmlEscapeTable.put('\u00B3', "&sup3;");
        htmlEscapeTable.put('\u00B4', "&acute;");
        htmlEscapeTable.put('\u00B5', "&micro;");
        htmlEscapeTable.put('\u00B6', "&para;");
        htmlEscapeTable.put('\u00B7', "&middot;");
        htmlEscapeTable.put('\u00B8', "&cedil;");
        htmlEscapeTable.put('\u00B9', "&sup1;");
        htmlEscapeTable.put('\u00BA', "&ordm;");
        htmlEscapeTable.put('\u00BB', "&raquo;");
        htmlEscapeTable.put('\u00BC', "&frac14;");
        htmlEscapeTable.put('\u00BD', "&frac12;");
        htmlEscapeTable.put('\u00BE', "&frac34;");
        htmlEscapeTable.put('\u00BF', "&iquest;");
        htmlEscapeTable.put('\u00C0', "&Agrave;");
        htmlEscapeTable.put('\u00C1', "&Aacute;");
        htmlEscapeTable.put('\u00C2', "&Acirc;");
        htmlEscapeTable.put('\u00C3', "&Atilde;");
        htmlEscapeTable.put('\u00C4', "&Auml;");
        htmlEscapeTable.put('\u00C5', "&Aring;");
        htmlEscapeTable.put('\u00C6', "&AElig;");
        htmlEscapeTable.put('\u00C7', "&Ccedil;");
        htmlEscapeTable.put('\u00C8', "&Egrave;");
        htmlEscapeTable.put('\u00C9', "&Eacute;");
        htmlEscapeTable.put('\u00CA', "&Ecirc;");
        htmlEscapeTable.put('\u00CB', "&Euml;");
        htmlEscapeTable.put('\u00CC', "&Igrave;");
        htmlEscapeTable.put('\u00CD', "&Iacute;");
        htmlEscapeTable.put('\u00CE', "&Icirc;");
        htmlEscapeTable.put('\u00CF', "&Iuml;");
        htmlEscapeTable.put('\u00D0', "&ETH;");
        htmlEscapeTable.put('\u00D1', "&Ntilde;");
        htmlEscapeTable.put('\u00D2', "&Ograve;");
        htmlEscapeTable.put('\u00D3', "&Oacute;");
        htmlEscapeTable.put('\u00D4', "&Ocirc;");
        htmlEscapeTable.put('\u00D5', "&Otilde;");
        htmlEscapeTable.put('\u00D6', "&Ouml;");
        htmlEscapeTable.put('\u00D7', "&times;");
        htmlEscapeTable.put('\u00D8', "&Oslash;");
        htmlEscapeTable.put('\u00D9', "&Ugrave;");
        htmlEscapeTable.put('\u00DA', "&Uacute;");
        htmlEscapeTable.put('\u00DB', "&Ucirc;");
        htmlEscapeTable.put('\u00DC', "&Uuml;");
        htmlEscapeTable.put('\u00DD', "&Yacute;");
        htmlEscapeTable.put('\u00DE', "&THORN;");
        htmlEscapeTable.put('\u00DF', "&szlig;");
        htmlEscapeTable.put('\u00E0', "&agrave;");
        htmlEscapeTable.put('\u00E1', "&aacute;");
        htmlEscapeTable.put('\u00E2', "&acirc;");
        htmlEscapeTable.put('\u00E3', "&atilde;");
        htmlEscapeTable.put('\u00E4', "&auml;");
        htmlEscapeTable.put('\u00E5', "&aring;");
        htmlEscapeTable.put('\u00E6', "&aelig;");
        htmlEscapeTable.put('\u00E7', "&ccedil;");
        htmlEscapeTable.put('\u00E8', "&egrave;");
        htmlEscapeTable.put('\u00E9', "&eacute;");
        htmlEscapeTable.put('\u00EA', "&ecirc;");
        htmlEscapeTable.put('\u00EB', "&euml;");
        htmlEscapeTable.put('\u00EC', "&igrave;");
        htmlEscapeTable.put('\u00ED', "&iacute;");
        htmlEscapeTable.put('\u00EE', "&icirc;");
        htmlEscapeTable.put('\u00EF', "&iuml;");
        htmlEscapeTable.put('\u00F0', "&eth;");
        htmlEscapeTable.put('\u00F1', "&ntilde;");
        htmlEscapeTable.put('\u00F2', "&ograve;");
        htmlEscapeTable.put('\u00F3', "&oacute;");
        htmlEscapeTable.put('\u00F4', "&ocirc;");
        htmlEscapeTable.put('\u00F5', "&otilde;");
        htmlEscapeTable.put('\u00F6', "&ouml;");
        htmlEscapeTable.put('\u00F7', "&divide;");
        htmlEscapeTable.put('\u00F8', "&oslash;");
        htmlEscapeTable.put('\u00F9', "&ugrave;");
        htmlEscapeTable.put('\u00FA', "&uacute;");
        htmlEscapeTable.put('\u00FB', "&ucirc;");
        htmlEscapeTable.put('\u00FC', "&uuml;");
        htmlEscapeTable.put('\u00FD', "&yacute;");
        htmlEscapeTable.put('\u00FE', "&thorn;");
        htmlEscapeTable.put('\u00FF', "&yuml;");

        // Mathematical, Greek and Symbolic characters for HTML
        htmlEscapeTable.put('\u0192', "&fnof;");
        htmlEscapeTable.put('\u0391', "&Alpha;");
        htmlEscapeTable.put('\u0392', "&Beta;");
        htmlEscapeTable.put('\u0393', "&Gamma;");
        htmlEscapeTable.put('\u0394', "&Delta;");
        htmlEscapeTable.put('\u0395', "&Epsilon;");
        htmlEscapeTable.put('\u0396', "&Zeta;");
        htmlEscapeTable.put('\u0397', "&Eta;");
        htmlEscapeTable.put('\u0398', "&Theta;");
        htmlEscapeTable.put('\u0399', "&Iota;");
        htmlEscapeTable.put('\u039A', "&Kappa;");
        htmlEscapeTable.put('\u039B', "&Lambda;");
        htmlEscapeTable.put('\u039C', "&Mu;");
        htmlEscapeTable.put('\u039D', "&Nu;");
        htmlEscapeTable.put('\u039E', "&Xi;");
        htmlEscapeTable.put('\u039F', "&Omicron;");
        htmlEscapeTable.put('\u03A0', "&Pi;");
        htmlEscapeTable.put('\u03A1', "&Rho;");
        htmlEscapeTable.put('\u03A3', "&Sigma;");
        htmlEscapeTable.put('\u03A4', "&Tau;");
        htmlEscapeTable.put('\u03A5', "&Upsilon;");
        htmlEscapeTable.put('\u03A6', "&Phi;");
        htmlEscapeTable.put('\u03A7', "&Chi;");
        htmlEscapeTable.put('\u03A8', "&Psi;");
        htmlEscapeTable.put('\u03A9', "&Omega;");
        htmlEscapeTable.put('\u03B1', "&alpha;");
        htmlEscapeTable.put('\u03B2', "&beta;");
        htmlEscapeTable.put('\u03B3', "&gamma;");
        htmlEscapeTable.put('\u03B4', "&delta;");
        htmlEscapeTable.put('\u03B5', "&epsilon;");
        htmlEscapeTable.put('\u03B6', "&zeta;");
        htmlEscapeTable.put('\u03B7', "&eta;");
        htmlEscapeTable.put('\u03B8', "&theta;");
        htmlEscapeTable.put('\u03B9', "&iota;");
        htmlEscapeTable.put('\u03BA', "&kappa;");
        htmlEscapeTable.put('\u03BB', "&lambda;");
        htmlEscapeTable.put('\u03BC', "&mu;");
        htmlEscapeTable.put('\u03BD', "&nu;");
        htmlEscapeTable.put('\u03BE', "&xi;");
        htmlEscapeTable.put('\u03BF', "&omicron;");
        htmlEscapeTable.put('\u03C0', "&pi;");
        htmlEscapeTable.put('\u03C1', "&rho;");
        htmlEscapeTable.put('\u03C2', "&sigmaf;");
        htmlEscapeTable.put('\u03C3', "&sigma;");
        htmlEscapeTable.put('\u03C4', "&tau;");
        htmlEscapeTable.put('\u03C5', "&upsilon;");
        htmlEscapeTable.put('\u03C6', "&phi;");
        htmlEscapeTable.put('\u03C7', "&chi;");
        htmlEscapeTable.put('\u03C8', "&psi;");
        htmlEscapeTable.put('\u03C9', "&omega;");
        htmlEscapeTable.put('\u03D1', "&thetasym;");
        htmlEscapeTable.put('\u03D2', "&upsih;");
        htmlEscapeTable.put('\u03D6', "&piv;");
        htmlEscapeTable.put('\u2022', "&bull;");
        htmlEscapeTable.put('\u2026', "&hellip;");
        htmlEscapeTable.put('\u2032', "&prime;");
        htmlEscapeTable.put('\u2033', "&Prime;");
        htmlEscapeTable.put('\u203E', "&oline;");
        htmlEscapeTable.put('\u2044', "&frasl;");
        htmlEscapeTable.put('\u2118', "&weierp;");
        htmlEscapeTable.put('\u2111', "&image;");
        htmlEscapeTable.put('\u211C', "&real;");
        htmlEscapeTable.put('\u2122', "&trade;");
        htmlEscapeTable.put('\u2135', "&alefsym;");
        htmlEscapeTable.put('\u2190', "&larr;");
        htmlEscapeTable.put('\u2191', "&uarr;");
        htmlEscapeTable.put('\u2192', "&rarr;");
        htmlEscapeTable.put('\u2193', "&darr;");
        htmlEscapeTable.put('\u2194', "&harr;");
        htmlEscapeTable.put('\u21B5', "&crarr;");
        htmlEscapeTable.put('\u21D0', "&lArr;");
        htmlEscapeTable.put('\u21D1', "&uArr;");
        htmlEscapeTable.put('\u21D2', "&rArr;");
        htmlEscapeTable.put('\u21D3', "&dArr;");
        htmlEscapeTable.put('\u21D4', "&hArr;");
        htmlEscapeTable.put('\u2200', "&forall;");
        htmlEscapeTable.put('\u2202', "&part;");
        htmlEscapeTable.put('\u2203', "&exist;");
        htmlEscapeTable.put('\u2205', "&empty;");
        htmlEscapeTable.put('\u2207', "&nabla;");
        htmlEscapeTable.put('\u2208', "&isin;");
        htmlEscapeTable.put('\u2209', "&notin;");
        htmlEscapeTable.put('\u220B', "&ni;");
        htmlEscapeTable.put('\u220F', "&prod;");
        htmlEscapeTable.put('\u2211', "&sum;");
        htmlEscapeTable.put('\u2212', "&minus;");
        htmlEscapeTable.put('\u2217', "&lowast;");
        htmlEscapeTable.put('\u221A', "&radic;");
        htmlEscapeTable.put('\u221D', "&prop;");
        htmlEscapeTable.put('\u221E', "&infin;");
        htmlEscapeTable.put('\u2220', "&ang;");
        htmlEscapeTable.put('\u2227', "&and;");
        htmlEscapeTable.put('\u2228', "&or;");
        htmlEscapeTable.put('\u2229', "&cap;");
        htmlEscapeTable.put('\u222A', "&cup;");
        htmlEscapeTable.put('\u222B', "&int;");
        htmlEscapeTable.put('\u2234', "&there4;");
        htmlEscapeTable.put('\u223C', "&sim;");
        htmlEscapeTable.put('\u2245', "&cong;");
        htmlEscapeTable.put('\u2248', "&asymp;");
        htmlEscapeTable.put('\u2260', "&ne;");
        htmlEscapeTable.put('\u2261', "&equiv;");
        htmlEscapeTable.put('\u2264', "&le;");
        htmlEscapeTable.put('\u2265', "&ge;");
        htmlEscapeTable.put('\u2282', "&sub;");
        htmlEscapeTable.put('\u2283', "&sup;");
        htmlEscapeTable.put('\u2284', "&nsub;");
        htmlEscapeTable.put('\u2286', "&sube;");
        htmlEscapeTable.put('\u2287', "&supe;");
        htmlEscapeTable.put('\u2295', "&oplus;");
        htmlEscapeTable.put('\u2297', "&otimes;");
        htmlEscapeTable.put('\u22A5', "&perp;");
        htmlEscapeTable.put('\u22C5', "&sdot;");
        htmlEscapeTable.put('\u2308', "&lceil;");
        htmlEscapeTable.put('\u2309', "&rceil;");
        htmlEscapeTable.put('\u230A', "&lfloor;");
        htmlEscapeTable.put('\u230B', "&rfloor;");
        htmlEscapeTable.put('\u2329', "&lang;");
        htmlEscapeTable.put('\u232A', "&rang;");
        htmlEscapeTable.put('\u25CA', "&loz;");
        htmlEscapeTable.put('\u2660', "&spades;");
        htmlEscapeTable.put('\u2663', "&clubs;");
        htmlEscapeTable.put('\u2665', "&hearts;");
        htmlEscapeTable.put('\u2666', "&diams;");

        htmlEscapeTable.entrySet().forEach(e -> htmlUnescapeTable.put(e.getValue(), e.getKey()));
    }

    public static String escapeHtml(String source) {
        if (null == source) {
            return null;
        }

        StringBuilder builder = new StringBuilder(source.length());
        char[] charArray = source.toCharArray();
        int lastMatch = -1;
        int difference = 0;

        for (int i = 0; i < charArray.length; i++) {
            char ch = charArray[i];
            if (htmlEscapeTable.containsKey(ch)) {
                difference = i - (lastMatch + 1);
                if (difference > 0) {
                    builder.append(charArray, lastMatch + 1, difference);
                }
                builder.append(htmlEscapeTable.get(ch));
                lastMatch = i;
            }
        }

        difference = charArray.length - (lastMatch + 1);
        if (difference > 0) {
            builder.append(charArray, lastMatch + 1, difference);
        }
        return builder.toString();
    }

    public static final String unescapeHTML(String source) {
        if (null == source) {
            return null;
        }

        StringBuilder builder = new StringBuilder(source.length());
        char[] charArray = source.toCharArray();
        for (int i = 0; i < charArray.length; ) {
            if (charArray[i] == '&') {
                int j = source.indexOf(";", i);
                if (j > i) {
                    String entity = source.substring(i, j + 1);
                    Character value = htmlUnescapeTable.get(entity);
                    if (value != null) {
                        builder.append(value);
                    }
                    i = j + 1;
                } else {
                    builder.append(charArray[i++]);
                }
            } else {
                builder.append(charArray[i++]);
            }
        }

//        int i, j;
//        boolean continueLoop;
//        int skip = 0;
//        do {
//            continueLoop = false;
//            i = source.indexOf("&", skip);
//            if (i > -1) {
//                j = source.indexOf(";", i);
//                if (j > i) {
//                    String entityToLookFor = source.substring(i, j + 1);
//                    Character value = htmlUnescapeTable.get(entityToLookFor);
//                    if (value != null) {
//                        source = source.substring(0, i) + value + source.substring(j + 1);
//                        continueLoop = true;
//                    } else if (value == null) {
//                        skip = i + 1;
//                        continueLoop = true;
//                    }
//                }
//            }
//        } while (continueLoop);
        return builder.toString();
    }
}
