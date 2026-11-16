package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Mecanum Teleop",group="interactive opmode")
public class MecanumTeleOp extends OpMode {
    private MecanumDrive drive;
    // todo: write your code here
    private DcMotor intakemotor;
    private DcMotor shootermotor1;
    private DcMotor shootermotor2;

    Servo servomotor1;

    boolean intakebutton;
    float shooterbutton;

    boolean intakeOn = false;
    boolean lastIntakeButton = false;

    @Override
    public void init() {
        drive=new MecanumDrive(hardwareMap);

        intakemotor = hardwareMap.get(DcMotor.class, "intakemotor");
        shootermotor1 = hardwareMap.get(DcMotor.class, "shootermotor1");
        shootermotor2 = hardwareMap.get(DcMotor.class, "shootermotor2");

        servomotor1 = hardwareMap.get(Servo.class, "servomotor1");

        intakeOn = false;
        lastIntakeButton = false;
        intakemotor.setPower(0.0); // make sure power is 0

        servomotor1.setPosition(0);

        telemetry.addLine("TeleOp ready");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Motor control
        double forward  = -gamepad1.left_stick_y;
        double strafe   = gamepad1.left_stick_x;
        double rotate   = gamepad1.right_stick_x;
        drive.driveRobot(forward, strafe, rotate);

        intakebutton = gamepad1.a;
        if (intakebutton && !lastIntakeButton) {
            intakeOn = !intakeOn; // this just flips the true/false using the !
        }
        lastIntakeButton = intakebutton;
        intakemotor.setPower(intakeOn ? 1.0 : 0.0); // this is shorthand if statement

        shooterbutton = gamepad1.right_trigger;
        shootermotor1.setPower(-shooterbutton * 0.5);
        shootermotor2.setPower(shooterbutton * 0.5);

        if (gamepad1.y) {
            servomotor1.setPosition(0.0);
        } else if (gamepad1.x) {
            servomotor1.setPosition(0.5);
        }

        telemetry.addData("Forward", forward);
        telemetry.addData("Strafe", strafe);
        telemetry.addData("Rotate", rotate);
        telemetry.addData("Intake", intakeOn ? "ON" : "OFF");
        telemetry.addData("Servo Position", servomotor1.getPosition());
        telemetry.update();
    }
}