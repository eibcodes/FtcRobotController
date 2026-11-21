package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class MecanumDrive {

    private DcMotor frontleftmotor;
    private DcMotor frontrightmotor;
    private DcMotor backleftmotor;
    private DcMotor backrightmotor;

    private IMU imu;

    static final double COUNTS_PER_INCH = (537.7) / (3.78 * Math.PI);

    public MecanumDrive(HardwareMap hardwareMap) {

        frontleftmotor  = hardwareMap.get(DcMotor.class, "frontleftmotor");
        frontrightmotor = hardwareMap.get(DcMotor.class, "frontrightmotor");
        backleftmotor   = hardwareMap.get(DcMotor.class, "backleftmotor");
        backrightmotor  = hardwareMap.get(DcMotor.class, "backrightmotor");

        backleftmotor.setDirection(DcMotor.Direction.REVERSE);
        frontleftmotor.setDirection(DcMotor.Direction.REVERSE);

        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters params = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        ));
        imu.initialize(params);
        imu.resetYaw();
    }

    public double getHeadingDegrees() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    public void resetEncoders() {
        frontleftmotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontrightmotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backleftmotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backrightmotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontleftmotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontrightmotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backleftmotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backrightmotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public double getAverageEncoderInches() {
        double avgTicks = (Math.abs(frontleftmotor.getCurrentPosition()) + Math.abs(frontrightmotor.getCurrentPosition())
                + Math.abs(backleftmotor.getCurrentPosition()) + Math.abs(backrightmotor.getCurrentPosition())) / 4.0;
        return avgTicks / COUNTS_PER_INCH;
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

    private void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        frontleftmotor.setZeroPowerBehavior(behavior);
        frontrightmotor.setZeroPowerBehavior(behavior);
        backleftmotor.setZeroPowerBehavior(behavior);
        backrightmotor.setZeroPowerBehavior(behavior);
    }
}

