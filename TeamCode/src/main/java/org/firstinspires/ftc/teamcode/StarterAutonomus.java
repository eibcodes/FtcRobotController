package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Starter FSM by EB")
public class StarterAutonomus extends OpMode {
    private MecanumDrive drive;

    private enum State {
        LEAVE, SEARCH, APPROACH, AQUIRE, POSITION, SHOOT, PARK, DONE
    }
    private State state = State.LEAVE;

    private final ElapsedTime stateTimer = new ElapsedTime();
    private final ElapsedTime autoTimer = new ElapsedTime();

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap);

        // todo initialize shooter and intake motors here as well

        telemetry.addLine("Autonomous ready!");
        telemetry.update();
    }

    @Override
    public void start() {
        state = State.LEAVE;
        stateTimer.reset();
        autoTimer.reset();
    }

    @Override
    public void loop() {
        if (autoTimer.seconds() >= 29.0) {
            changeState(State.DONE);
        }

        // give yourself 5 seconds to park?
        if (autoTimer.seconds() >= 25.0) {
            changeState(State.PARK);
        }

        switch (state) {
            case LEAVE:
//                drive.driveRobot(.5, 0, 0);
                if (stateTimer.seconds() >= 2.0)
                    changeState(State.SEARCH);
                break;
            case SEARCH:
                if (stateTimer.seconds() >= 2.0)
                    changeState(State.APPROACH);
                break;
            case APPROACH:
                if (stateTimer.seconds() >= 2.0)
                    changeState(State.AQUIRE);
                break;
            case AQUIRE:
                if (stateTimer.seconds() >= 2.0)
                    // have x # of balls?
                    changeState(State.POSITION);
                break;
            case POSITION:
//                drive.driveRobot(0, 0.3, 0);
                if (stateTimer.seconds() >= 1.0)
                    changeState(State.SHOOT);
                break;
            case SHOOT:
                drive.stop();
                if (stateTimer.seconds() >= 2.0)
                    changeState(State.SEARCH);
                break;
            case PARK:
                if (stateTimer.seconds() >= 2.0)
                    changeState(State.PARK);
                break;
            case DONE:
                drive.stop();
                break;
        }

        telemetry.addData("State", state);
        telemetry.addData("State time", stateTimer.seconds());
        telemetry.addData("Auto time", autoTimer.seconds());
        telemetry.update();
    }

    private void changeState(State next) {
        if (state == next) return; // dont reset timer every loop
        drive.stop();
        state = next;
        stateTimer.reset();
    }

    @Override
    public void stop() {
        if (drive != null) drive.stop();
        // todo stop intake and shooters too
    }
}
