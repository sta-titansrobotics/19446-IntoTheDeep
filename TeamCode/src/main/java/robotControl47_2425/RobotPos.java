package robotControl47_2425;

import com.qualcomm.robotcore.robot.Robot;

public class RobotPos {
    double x, y, angle;
    public RobotPos(double x, double y, double angle)
    {
        this.x = x;this.y = y; this.angle = angle;
    }
    public void setPosition(double x, double y, double angle){
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void setPosition(RobotPos pos){
        this.x = pos.x;
        this.y = pos.y;
        this.angle = pos.angle;
    }


}
