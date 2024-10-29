// MainTest.java
package auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "MainTest", group = "Test")
public class MainTest extends LinearOpMode {
    //private Robot robot;
    private DcMotor slideH;
    private DcMotor slideV;

    @Override
    public void runOpMode() {
        //robot = new Robot();
        slideH = hardwareMap.get(DcMotor.class, "odom_h");
        HSlide sliderH = new HSlide(slideH);
        boolean reached = false ;

       // slideV = hardwareMap.get(DcMotor.class, "slideV");
       // VSlide sliderV = new VSlide(slideV);
        sliderH.initialize();


        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
           // robot.resetAll();
            //sliderH.goToPosition(500);

            telemetry.addData("position", slideH.getCurrentPosition());
            telemetry.update();




            if(slideH.getCurrentPosition() >=500){
                sliderH.goToPosition(0);
                reached = true;
            }
            else if (!reached){
                sliderH.goToPosition(500);
            }

        }
    }

    // Add the resetPosition method e
    public void resetPosition() {
        // Base implementation or abstract method
    }
}

