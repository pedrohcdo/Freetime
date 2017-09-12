package com.createlier.freetime.utils;

/**
 * Created by Pedro on 03/08/2016.
 */
final public class GeometricUtils {

    /**
     * Point
     */
    final public static class Point {

        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point(){}

        @Override
        public String toString() {
            return "Point(" + x + ", " + y + ")";
        }
    }

    /**
     * Size
     */
    final public static class Size {
        public int width;
        public int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public Size(){}

        @Override
        public String toString() {
            return "Size(" + width + ", " + height + ")";
        }
    }

}
