package com.nonodo.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import java.util.Iterator;

/**
 * Exponential moving-average of Control Hub battery voltage. Uses low pass filter to smooth out noise.
 */
public class BatteryVoltageFilter {

    public static final double initialVoltageGuess = 13;
    public static final double alpha = .8;

    private static BatteryVoltageFilter instance;

    public static BatteryVoltageFilter getInstance(HardwareMap hardwareMap) {
        if (instance == null) {
            instance = new BatteryVoltageFilter(hardwareMap);
        }
        return instance;
    }

    private final VoltageSensor voltageSensor;
    private double voltage;

    public BatteryVoltageFilter(HardwareMap hardwareMap) {
        voltageSensor = firstVoltageSensor(hardwareMap);
        voltage = initialVoltageGuess;
    }

    public void update() {
        if (voltageSensor == null) {
            return;
        }
        double rawVoltage = voltageSensor.getVoltage();
        voltage = voltage * alpha + rawVoltage * (1 - alpha);
    }

    public double getVoltage() {
        return voltage;
    }

    private static VoltageSensor firstVoltageSensor(HardwareMap hardwareMap) {
        if (hardwareMap == null) {
            return null;
        }
        Iterator<VoltageSensor> sensors = hardwareMap.voltageSensor.iterator();
        return sensors.hasNext() ? sensors.next() : null;
    }
}
