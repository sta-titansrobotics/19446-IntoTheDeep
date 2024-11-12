package teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import robotControl47_2425.Sliders.HSlide;
import robotControl47_2425.Sliders.VSlide;

@TeleOp(name = "TeleopSliderTest", group = "Test")
public class TeleopSliderTest extends OpMode {

    private HSlide hSlide;
    private VSlide vSlide;
    private boolean hSlideExtended = false;
    private boolean vSlideExtended = false;
    private boolean previousAState = false;
    private boolean previousBState = false;

    @Override
    public void init() {
        DcMotor hSlideMotor = hardwareMap.get(DcMotor.class, "hSlide");
        DcMotor vSlideMotor = hardwareMap.get(DcMotor.class, "vSlide");

        hSlide = new HSlide(hSlideMotor);
        vSlide = new VSlide(vSlideMotor);

        hSlide.initialize();
        vSlide.initialize();
    }

    @Override
    public void loop() {
        // Control HSlide with gamepad1.a
        if (gamepad1.a && !previousAState) {
            if (!hSlideExtended) {
                hSlide.goToPosition(hSlide.getMaxPosition());
            } else {
                hSlide.goToPosition(0);
            }
            hSlideExtended = !hSlideExtended;
        }
        previousAState = gamepad1.a;

        // Control VSlide with gamepad1.b
        if (gamepad1.b && !previousBState) {
            if (!vSlideExtended) {
                vSlide.goToPosition(vSlide.getMaxPosition());
            } else {
                vSlide.goToPosition(0);
            }
            vSlideExtended = !vSlideExtended;
        }
        previousBState = gamepad1.b;

        // Add telemetry data
        telemetry.addData("HSlide Position", hSlide.getCurrentPosition());
        telemetry.addData("HSlide Extended", hSlideExtended);
        telemetry.addData("VSlide Position", vSlide.getCurrentPosition());
        telemetry.addData("VSlide Extended", vSlideExtended);
        telemetry.update();
    }
}