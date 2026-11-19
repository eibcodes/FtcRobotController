package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;


public class MecanumDrive {

    private DcMotor frontleftmotor;
    private DcMotor frontrightmotor;
    private DcMotor backleftmotor;
    private DcMotor backrightmotor;

    public MecanumDrive(HardwareMap hardwareMap) {

        frontleftmotor  = hardwareMap.get(DcMotor.class, "frontleftmotor");
        frontrightmotor = hardwareMap.get(DcMotor.class, "frontrightmotor");
        backleftmotor   = hardwareMap.get(DcMotor.class, "backleftmotor");
        backrightmotor  = hardwareMap.get(DcMotor.class, "backrightmotor");

        backleftmotor.setDirection(DcMotor.Direction.REVERSE);
        frontleftmotor.setDirection(DcMotor.Direction.REVERSE);
    }

    public void driveRobot(double drive,double strafe,double twist){
        // Motor control
        double fl = drive + strafe + twist;
        double fr = drive - strafe - twist;
        double bl = drive - strafe + twist;
        double br = drive + strafe - twist;

        double max = Math.max (1.0,Math.max(
                Math.max(Math.abs(fl),Math.abs(fr)),
                Math.max(Math.abs(bl),Math.abs(br))));

        frontleftmotor.setPower(fl/max);
        frontrightmotor.setPower(fr/max);
        backleftmotor.setPower(bl/max);
        backrightmotor.setPower(br/max);


    }
    public void stop() {
        driveRobot(0,0,0);
    }
}

