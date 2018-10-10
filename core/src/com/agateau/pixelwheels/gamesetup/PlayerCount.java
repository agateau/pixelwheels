package com.agateau.pixelwheels.gamesetup;

public enum PlayerCount {
    ONE,
    MULTI;

    public int toInt() {
        switch (this) {
            case ONE:
                return 1;
            case MULTI:
                return 2;
        }
        return -1;
    }
}
