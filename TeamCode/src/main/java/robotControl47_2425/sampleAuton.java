package robotControl47_2425;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import robotControl47_2425.Sliders.HSlide;
import robotControl47_2425.Sliders.HSlideController;
import robotControl47_2425.Sliders.VSlide;
import robotControl47_2425.Sliders.VSlideController;

@Autonomous
public class sampleAuton extends LinearOpMode {
    // initialize chassis, current robot pos, odometry, sliders, etc.
    //private RobotPos currentRobotPos = new RobotPos(0, 0, 0); //start value
    private DcMotor slideH, sliderVL, sliderVR;
    private Chassis chassis;
    private Odometry odometry;
    private VSlide sliderV1, sliderV2;
    private HSlide sliderH;

    private VSlideController VSlide;
    private HSlideController HSlide;

    @Override
    public void runOpMode() {
        // Initialize hardware devices and controllers here
        sliderVL = hardwareMap.get(DcMotor.class, "vSlideL");
        sliderVR = hardwareMap.get(DcMotor.class, "vSlideR");
        slideH = hardwareMap.get(DcMotor.class, "hSlide");

        // Initialize sliders
        sliderH = new HSlide(slideH);

        sliderV1 = new VSlide(slideV1, hardwareMap, this);
        sliderV2 = new VSlide(slideV2, hardwareMap, this);

        //Inistialize the 2 controllers
        HSlide = new HSlideController(sliderH);
        VSlide = new VSlideController(sliderVL, sliderVR);

        //Initialize chasis and Odometry
        odometry = new Odometry(this);
        chassis = new Chassis(this, odometry);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Run autonomous-specific code here
        HSlide.start();
        VSlide.start();
        while (opModeIsActive() && !isStopRequested()) {
            //This is moving in terms of centimeters
            chassis.moveToPosition(60, 30, 0);
            sleep(5000);
            chassis.moveToPosition(0, 30, 230);
        }
        HSlide.stop();
        VSlide.stop();
    }
}