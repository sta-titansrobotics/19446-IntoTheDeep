package auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "MainTest", group = "Test")
public class MainTest extends LinearOpMode {
    private DcMotor slideH;
    private HSlide sliderH;
    private SlideController slideController;

    @Override
    public void runOpMode() {
        slideH = hardwareMap.get(DcMotor.class, "odom_h");
        sliderH = new HSlide(slideH);

        // Initialize HSlide motor
        sliderH.initialize();

        // Create SlideController and pass the HSlide instance
        slideController = new SlideController(sliderH);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // Start the slide control thread through SlideController
        slideController.start();

        while (opModeIsActive()) {
            telemetry.addData("position", slideH.getCurrentPosition());
            telemetry.update();
        }

        // Stop the slide control thread when OpMode ends
        slideController.stop();
    }

    public void resetPosition() {
        // Base implementation for resetting position
    }
}
