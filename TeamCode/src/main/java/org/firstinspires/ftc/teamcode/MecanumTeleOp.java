package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Mecanum TeleOp by EB", group="Iterative OpMode")
public class MecanumTeleOp extends OpMode {
    private MecanumDrive drive;

    // todo: add your existing intake and shooter motor fields here

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap);

        // TODO: initialize your existing intake and shooter motors
        telemetry.addLine("TODO: initialize your existing intake and shooter motors");
        telemetry.addLine("TeleOp ready");
        telemetry.update();
    }

    @Override
    public void loop() {
        double forward = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        drive.driveRobot(forward, strafe, rotate);

        // todo: copy in working gamepad controls for intake
        // todo: copy in working gamepad controls for shooter
        // add reverse for intake, paul asked for

        telemetry.addData("Forward", forward);
        telemetry.addData("Strafe", strafe);
        telemetry.addData("Rotate", rotate);
        telemetry.update();
    }

    @Override
    public void stop(){
        if (drive != null) drive.stop();
        // stop intake and shooter motors here as well
    }
}
