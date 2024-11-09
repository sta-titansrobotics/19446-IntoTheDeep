package teleop;

import com.qualcomm.robotcore.hardware.DcMotor;

public class VSlide {

    private final DcMotor motor;
    private static final int MAX_POSITION = 2200; // Updated maximum position

    public VSlide(DcMotor motor) {
        this.motor = motor;
    }

    public void initialize() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
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

    public int getMaxPosition(){
        return MAX_POSITION;
    }

}