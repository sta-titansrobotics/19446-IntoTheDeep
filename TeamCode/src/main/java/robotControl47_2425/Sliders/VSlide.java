package robotControl47_2425.Sliders;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class VSlide {

    private final DcMotor motor;
    private static final int MAX_POSITION = 2200; // Updated maximum position

    private Servo servo1, servo2, servo3, servo4, servo5, claw;
    private OpMode opMode;

    public VSlide(DcMotor motor, HardwareMap hardwareMap, OpMode opMode) {
        this.motor = motor;
        this.opMode = opMode;
        initializeServos(hardwareMap);
    }

    public void initialize() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    private void initializeServos(HardwareMap hardwareMap) {
        claw = hardwareMap.get(Servo.class, "claw");
        servo1 = hardwareMap.get(Servo.class, "servo1");
        servo2 = hardwareMap.get(Servo.class, "servo2");
        servo3 = hardwareMap.get(Servo.class, "servo3");
        servo4 = hardwareMap.get(Servo.class, "servo4");
        servo5 = hardwareMap.get(Servo.class, "servo5");
    }

    public void resetPosition() {
        motor.setTargetPosition(0);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.5);
    }

    public void goToPosition(int position) {
        position = Math.max(0, position);
        position = Math.min(MAX_POSITION, position);

        motor.setTargetPosition(position);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.5);
    }

    public int getCurrentPosition() {
        return motor.getCurrentPosition();
    }

    public int getMaxPosition() {
        return MAX_POSITION;
    }

    // Method to open claw
    public void openClaw() {
        claw.setPosition(0.9);
        updateTelemetry();
    }

    // Method to close claw
    public void closeClaws() {
        claw.setPosition(0.5);
        updateTelemetry();
    }

    // Method to roll claw upright
    public void rollClawUp() {
        claw.setPosition(0.156);
        updateTelemetry();
    }

    // Method to roll claw upside down
    public void rollClawDown() {
        claw.setPosition(0.816);
        updateTelemetry();
    }

    // Method to update telemetry with servo positions
    private void updateTelemetry() {
        opMode.telemetry.addData("Claw Position", claw.getPosition());
        opMode.telemetry.addData("Servo1 Position", servo1.getPosition());
        opMode.telemetry.addData("Servo2 Position", servo2.getPosition());
        opMode.telemetry.addData("Servo3 Position", servo3.getPosition());
        opMode.telemetry.addData("Servo4 Position", servo4.getPosition());
        opMode.telemetry.addData("Servo5 Position", servo5.getPosition());
        opMode.telemetry.update();
    }
}