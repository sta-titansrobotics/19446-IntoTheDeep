package auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "MainTest", group = "Test")
public class MainTest extends LinearOpMode {
    private DcMotor slideH;
    private DcMotor slideV;
    private HSlide sliderH;
    private VSlide sliderV;

    private SlideController slideController;
    private VSlideController vSlideController;

    @Override
    public void runOpMode() {
        slideH = hardwareMap.get(DcMotor.class, "vSlide");
        slideV = hardwareMap.get(DcMotor.class, "hSlide");

        sliderH = new HSlide(slideH);
        slideController = new SlideController(sliderH);

        sliderV = new VSlide(slideV);

        vSlideController = new VSlideController(sliderV);

        sliderH.initialize();
        sliderV.initialize();

        waitForStart();

        slideController.start();
        vSlideController.start();

        while (opModeIsActive()) {
            telemetry.addData("H Slide position", slideH.getCurrentPosition());
            telemetry.addData("V Slide position", slideV.getCurrentPosition());
            telemetry.update();
        }

        slideController.stop();
        vSlideController.stop();
    }

    public void resetPosition() {
        // Base implementation for resetting position
    }
}