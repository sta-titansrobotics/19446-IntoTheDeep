package robotControl47_2425.Sliders;

import com.qualcomm.robotcore.hardware.DcMotor;

public class HSlide {

    private DcMotor motor;
    private static final int MAX_POSITION = 2000; // Updated maximum position

    public HSlide(DcMotor motor) {
        this.motor = motor;
    }

    public void initialize() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setDirection(DcMotor.Direction.REVERSE); // Set motor direction to REVERSE
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
}