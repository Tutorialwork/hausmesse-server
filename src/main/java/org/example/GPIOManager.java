package org.example;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;

public class GPIOManager {

    private final GpioPinDigitalInput doorSwitch;
    private final GpioPinDigitalOutput yellowLed;
    private final GpioPinDigitalOutput redLed;


    public GPIOManager() {
        GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
        final GpioController gpio = GpioFactory.getInstance();

        this.doorSwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_17, PinPullResistance.PULL_UP);
        this.yellowLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_21);
        this.redLed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13);
    }

    public GpioPinDigitalInput getDoorSwitch() {
        return doorSwitch;
    }

    public GpioPinDigitalOutput getYellowLed() {
        return yellowLed;
    }

    public GpioPinDigitalOutput getRedLed() {
        return redLed;
    }
}
