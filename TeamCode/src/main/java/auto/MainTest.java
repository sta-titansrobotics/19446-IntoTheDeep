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
        //Making Variables
        slideH = hardwareMap.get(DcMotor.class, "odom_h");


        //-------------------------------------------------------------------------------------
        //Initialize
        sliderH = new HSlide(slideH);
        slideController = new SlideController(sliderH);


        sliderH.initialize();
        //================================================================================

        waitForStart();

        // START ALL THREADS
        slideController.start();



        //-----------------------------------------------------------------------------------
        //Main while loop

        while (opModeIsActive()) {
            telemetry.addData("position", slideH.getCurrentPosition());
            telemetry.update();
        }

        //===============================================================================

        // STOP ALL THREADS
        slideController.stop();
    }

    public void resetPosition() {
        // Base implementation for resetting position
    }
}
