package com.mars.kjli.imageprocessing;

/**
 * Created by King on 2014/11/29.
 */
public abstract class ImageLibrary {

    public static int gray(int color) {
        return color & 0xff;
    }

    public static int color(int gray) {
        return (0xff << 24) | (gray << 16) | (gray << 8) | gray;
    }

    public static int[] histogram(int[][] gls) {
        if (gls == null || gls.length <= 0) {
            throw new IllegalArgumentException();
        }

        int[] hg = new int[0x100];

        for (int r = 0; r != gls.length; ++r) {
            for (int c = 0; c != gls[r].length; ++c) {
                ++hg[gls[r][c]];
            }
        }

        return hg;
    }

    public static void histogramEqualize(int[][] gls) {
        if (gls == null || gls.length <= 0) {
            throw new IllegalArgumentException();
        }

        int[] hg = histogram(gls);

        int b = 0;
        int e = 0xff;

        while (b < e && hg[b] == 0) {
            ++b;
        }

        while (e > b && hg[e] == 0) {
            --e;
        }

        if (b == e) {
            return;
        }

        final int scale = (0x100 << 10) / (e - b);

        int[] map = new int[256];
        map[b] = 0;
        map[e] = 0xff;
        for (int i = b + 1; i != e; ++i) {
            if (hg[i] == 0) {
                continue;
            }

            map[i] = ((i - b) * scale) >> 10;
        }

        for (int r = 0; r != gls.length; ++r) {
            for (int c = 0; c != gls[r].length; ++c) {
                gls[r][c] = map[gls[r][c]];
            }
        }
    }
}
