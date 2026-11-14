package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="Mecanum TeleOp by EB", group="Iterative OpMode")
public class MecanumTeleOp extends OpMode {
    private MecanumDrive drive;

    private DcMotor intakemotor;
    private DcMotor shootermotor1;
    private DcMotor shootermotor2;

    boolean intakebutton;
    float shooterButton;

    // intake's toggle variables
    boolean intakeOn = false;
    // this one tracks the last intake button's state so a press pass multiple frames dont trigger motor off/on
    boolean lastIntakeButton = false;

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap);

        intakemotor = hardwareMap.get(DcMotor.class, "intakemotor");
        shootermotor1 = hardwareMap.get(DcMotor.class, "shootermotor1");
        shootermotor2 = hardwareMap.get(DcMotor.class, "shootermotor2");

        // in intit go ahead and set these to false
        intakeOn = false;
        lastIntakeButton = false;
        intakemotor.setPower(0.0); // make sure power is 0

        telemetry.addLine("TeleOp ready");
        telemetry.update();
    }

    @Override
    public void loop() {
        double forward = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        drive.driveRobot(forward, strafe, rotate);

        // register Gamepad1's A button to intake
        intakebutton = gamepad1.a;

        // dont switch if we held down into the next "loop" aka frame[s]
        if (intakebutton && !lastIntakeButton) {
            intakeOn = !intakeOn; // this just flips the true/false using the !
        }

        // record state in this loop
        lastIntakeButton = intakebutton;

        // set power to 1.0 if intakeOn is supposed to be true
        intakemotor.setPower(intakeOn ? 1.0 : 0.0); // this is shorthand if statement
        /*
        intakeOn = true;
        if (intakeOn == true) {
            intakemotor.setPower(1.0);
        } else {
            intakemotor.setPower(0.0);
        }
        */

        // todo: copy in working gamepad controls for intake
        // todo: copy in working gamepad controls for shooter

        telemetry.addData("Forward", forward);
        telemetry.addData("Strafe", strafe);
        telemetry.addData("Rotate", rotate);
        telemetry.addData("Intake", intakeOn ? "ON" : "OFF");
        telemetry.update();
    }

    @Override
    public void stop(){
        if (drive != null) drive.stop();

        // stop intake and shooter motors
        if (intakemotor != null) intakemotor.setPower(0.0);
        if (shootermotor1 != null) shootermotor1.setPower(0.0);
        if (shootermotor2 != null) shootermotor2.setPower(0);
    }
}
