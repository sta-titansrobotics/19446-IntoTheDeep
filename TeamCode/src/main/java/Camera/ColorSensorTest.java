package Camera;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;

@TeleOp(name = "Color Sensor TeleOp", group = "Sensor")
public class ColorSensorTest extends OpMode {

    // Declare the color sensor
    private ColorSensor colorSensor;

    @Override
    public void init() {
        // Initialize the color sensor from the hardware map
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");
        if (colorSensor != null) {
            telemetry.addData("Status", "Color Sensor Initialized");
        } else {
            telemetry.addData("Status", "Color Sensor Not Found");
        }
        telemetry.update();
    }

    @Override
    public void loop() {
        if (colorSensor != null) {

            int red = colorSensor.red();
            int green = colorSensor.green();
            int blue = colorSensor.blue();

            telemetry.addData("Red", red);
            telemetry.addData("Green", green);
            telemetry.addData("Blue", blue);

            String detectedColor;
            if (red > green && red > blue) {
                detectedColor = "Red";
            } else if (green > red && green > blue) {
                detectedColor = "Yellow"; // Green + Red
            } else if (blue > red && blue > green) {
                detectedColor = "Blue";
            } else if (red > 200 && green > 200 && blue > 200) {
                detectedColor = "White"; // High values of all colors
            } else if (red < 50 && green < 50 && blue < 50) {
                detectedColor = "Black"; // Low values of all colors
            } else {
                detectedColor = "Unknown";
            }

            telemetry.addData("Detected Color", detectedColor);
        } else {
            telemetry.addData("Status", "Color Sensor Not Initialized");
        }

        telemetry.update();
    }

    @Override
    public void stop() {
        telemetry.addData("Status", "Stopped");
        telemetry.update();
    }
}