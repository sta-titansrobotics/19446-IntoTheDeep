package auto;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class VSlide {

    private DcMotor motor;

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
        position = Math.min(1000, position);

        motor.setTargetPosition(position);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.5);
    }

    public int getCurrentPosition() {
        return motor.getCurrentPosition();
    }
}