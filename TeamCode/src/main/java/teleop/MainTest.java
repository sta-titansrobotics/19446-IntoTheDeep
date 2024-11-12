package teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import robotControl47_2425.Sliders.HSlide;
import robotControl47_2425.Sliders.HSlideController;
import robotControl47_2425.Sliders.VSlide;
import robotControl47_2425.Sliders.VSlideController;

@TeleOp(name = "MainTest", group = "Test")
public class MainTest extends LinearOpMode {
    private DcMotor slideH;
    private DcMotor slideV;
    private VSlide sliderV;
    private HSlide sliderH;

    private VSlideController verticalslideController;
    private HSlideController horizontalSlideController;

    @Override
    public void runOpMode() {
        slideV = hardwareMap.get(DcMotor.class, "vSlide");
        slideH = hardwareMap.get(DcMotor.class, "hSlide");

        sliderH = new HSlide(slideH);
        horizontalSlideController = new HSlideController(sliderH);

        sliderV = new VSlide(slideV);

        verticalslideController = new VSlideController(sliderV);

        sliderH.initialize();
        sliderV.initialize();

        waitForStart();

        verticalslideController.start();
        horizontalSlideController.start();

        while (opModeIsActive()) {
            telemetry.addData("H Slide position", slideH.getCurrentPosition());
            telemetry.addData("V Slide position", slideV.getCurrentPosition());
            telemetry.update();
        }

        horizontalSlideController.stop();
        verticalslideController.stop();
    }

    public void resetPosition() {
        // Base implementation for resetting position
    }
}