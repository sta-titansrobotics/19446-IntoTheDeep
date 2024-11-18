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
    private RobotPos currentRobotPos = new RobotPos(0, 0, 0); //start value
    private DcMotor slideH1, slideH2, slideV;
    private Chassis chassis;
    private Odometry odometry;
    private VSlide sliderV;
    private HSlide sliderH1, sliderH2;

    private VSlideController VSlide;
    private HSlideController HSlideLeft, HSlideRight;

    @Override
    public void runOpMode() {
        // Initialize hardware devices and controllers here
        slideV = hardwareMap.get(DcMotor.class, "vSlide");
        slideH1 = hardwareMap.get(DcMotor.class, "hSlide1");
        slideH2 = hardwareMap.get(DcMotor.class, "hSlide2");

        // Initialize sliders and their controllers
        sliderH1 = new HSlide(slideH1);
        HSlideLeft = new HSlideController(sliderH1);

        sliderH2 = new HSlide(slideH2);
        HSlideRight = new HSlideController(sliderH2);

        sliderV = new VSlide(slideV, hardwareMap, this);
        VSlide = new VSlideController(sliderV);

        //Initialize chasis and Odometry
        odometry = new Odometry(this);
        chassis = new Chassis(this, odometry);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Run autonomous-specific code here
        HSlideLeft.start();
        HSlideRight.start();
        VSlide.start();
        while (opModeIsActive() && !isStopRequested()) {
            //This is moving in terms of centimeters
            chassis.moveToPosition(60, 30, 0);
            sleep(5000);
            chassis.moveToPosition(0, 30, 230);
        }
        HSlideLeft.stop();
        HSlideRight.stop();
        VSlide.stop();
    }
}