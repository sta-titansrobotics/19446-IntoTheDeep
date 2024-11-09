package teleop;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

//Gordon
public class VSlide extends MainTest {

    private final DcMotor motor;

    public VSlide(DcMotor motor) {
        // Constructor code if needed
        this.motor = motor;
    }

    public void initialize() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void resetPosition() {
        // Code to reset V-Slide position using touch sensor
        // motor.setDirection(DcMotorSimple.Direction.REVERSE);

        // motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // future implementation for checking the touch sensor to see if its at 0 position,
        // if it is not, then throw an error
        motor.setTargetPosition(0);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.5);
    }

    public void goToPosition(int position) {
        // Code to move V-Slide to specified position

        position = Math.max(0, position);
        position = Math.min(2000, position);

        motor.setTargetPosition(position);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(0.5);

        /*
        if(motor.getCurrentPosition()<position) {
            motor.setPower(0.5);
        }else{
            motor.setPower(0);
        }
        */
    }

    // Add getCurrentPosition to retrieve the motor's current position
    // Do not delete, this is for the SlideController Class
    public int getCurrentPosition() {
        return motor.getCurrentPosition();
    }
}
