package com.mars.kjli.imageprocessing;

import android.graphics.Color;

/**
 * Created by King on 2014/11/29.
 */
public abstract class ImageLibrary {

    public static int rgb2Gray(int color) {
        return (Color.red(color) * 306 + Color.green(color) * 601 + Color.blue(color) * 117) >> 10;
    }

    public static int gray2RGB(int gray) {
        return Color.rgb(gray, gray, gray);
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

    public static int[][] imageFilter(int[][] gls, int[][] filter) {
        if (gls == null) {
            throw new IllegalArgumentException();
        }

        if (filter == null || filter.length % 2 != 1 || filter[0].length % 2 != 1) {
            throw new IllegalArgumentException();
        }

        if (gls.length < filter.length || gls[0].length < filter[0].length) {
            throw new IllegalArgumentException();
        }

        final int N = filter.length;

        int sum = 0;
        for (int r = 0; r != N; ++r) {
            for (int c = 0; c != N; ++c) {
                sum += filter[r][c];
            }
        }

        int[][] pixels = new int[gls.length][gls[0].length];
        for (int x = 0; x != pixels.length; ++x) {
            for (int y = 0; y != pixels[x].length; ++y) {
                pixels[x][y] = gls[x][y];
            }
        }

        for (int x = N >> 1; x != gls.length - (N >> 1); ++x) {
            for (int y = N >> 1; y != gls[x].length - (N >> 1); ++y) {
                pixels[x][y] = 0;
                for (int r = 0; r != N; ++r) {
                    for (int c = 0; c != N; ++c) {
                        pixels[x][y] += filter[r][c] * gls[x + r - (N >> 1)][y + c - (N >> 1)];
                    }
                }

                if (sum != 0 && sum != 1) {
                    pixels[x][y] /= sum;
                }
            }
        }

        return pixels;
    }
}
