package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MecanumDrive {
    private final DcMotor frontLeft, frontRight;
    private final DcMotor backLeft, backRight;

    public MecanumDrive(HardwareMap hardwareMap) {
        frontLeft   = hardwareMap.get(DcMotor.class, "frontleftmotor");
        frontRight   = hardwareMap.get(DcMotor.class, "frontrightmotor");
        backLeft   = hardwareMap.get(DcMotor.class, "backleftmotor");
        backRight   = hardwareMap.get(DcMotor.class, "backrightmotor");

        // reverse any motor you need here
    }

    public void driveRobot(double drive, double strafe, double twist) {
        double fl = drive + strafe + twist;
        double fr = drive - strafe - twist;
        double bl = drive - strafe + twist;
        double br = drive + strafe - twist;

        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(fl), Math.abs(fr)),
                Math.max(Math.abs(bl), Math.abs(br))));

        frontLeft.setPower(fl / max);
        frontRight.setPower(fr / max);
        backLeft.setPower(bl / max);
        backRight.setPower(br / max);
    }

    public void stop() {
        driveRobot(0,0,0);
    }
}
