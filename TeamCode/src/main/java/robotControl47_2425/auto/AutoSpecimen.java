package robotControl47_2425.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import robotControl47_2425.teleop.DriveControlled;
import robotControl47_2425.Sliders.VSlideController;

@Autonomous(name = "AutoSpecimen", group = "Test")
public class AutoSpecimen extends LinearOpMode {

    private DcMotor fl, fr, bl, br;
    private VSlideController vSlideController;

    @Override
    public void runOpMode() {
        // Initialize motors
        fl = hardwareMap.get(DcMotor.class, "lf");
        fr = hardwareMap.get(DcMotor.class, "rf");
        bl = hardwareMap.get(DcMotor.class, "lr");
        br = hardwareMap.get(DcMotor.class, "rr");

        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);

        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Initialize VSlideController
        vSlideController = new VSlideController(hardwareMap, this);

        waitForStart();

        // Move towards the submersible
        double power = 0.5;
        fl.setPower(power);
        fr.setPower(power);
        bl.setPower(power);
        br.setPower(power);
        sleep(2000);

        // Stop all motors
        fl.setPower(0);
        fr.setPower(0);
        bl.setPower(0);
        br.setPower(0);

        // Place specimen on high rung
        vSlideController.goToPosition(500);
        sleep(1000);
        vSlideController.transferPos();
        sleep(300);
        vSlideController.goToPosition(1200);
        sleep(600);
        vSlideController.openClaw();
    }
}