package com.nonodo.hardware;

final class AngleMath {

    private AngleMath() {
    }

    static double normalize(double degrees) {
        double wrapped = degrees % 360.0;
        if (wrapped > 180.0) {
            wrapped -= 360.0;
        } else if (wrapped <= -180.0) {
            wrapped += 360.0;
        }
        return wrapped;
    }
}
