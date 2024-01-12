package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Snagger {
    private DcMotorEx leftLift;
    private DcMotorEx rightLift;
    private LinearOpMode currentOpMode;


    /*boolean stateOfUp;
    boolean stateOfDown;*/

    private double power = 0.8;
    private double powerMultiplierL;
    private double powerMultiplierR;
    private int highPos = 500;
    private int lowPos = 0;
    private Level where = Level.LOW;

    public Snagger(LinearOpMode opMode){
        currentOpMode = opMode;
        leftLift = currentOpMode.hardwareMap.get(DcMotorEx.class, "LS");
        rightLift = currentOpMode.hardwareMap.get(DcMotorEx.class, "RS");
        leftLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    //manual teleop movement
    public void move(double magnitude){

        if (magnitude > 0.1 || magnitude < -0.1) {
            leftLift.setDirection(DcMotor.Direction.REVERSE);
            rightLift.setDirection(DcMotor.Direction.FORWARD);
            leftLift.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
            rightLift.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
            currentOpMode.telemetry.addLine("in move");
            if(leftLift.getCurrent(CurrentUnit.AMPS)>5){
                powerMultiplierL=5*(1/leftLift.getCurrent(CurrentUnit.AMPS));
            }else{
                powerMultiplierR=1;
            }
            if(rightLift.getCurrent(CurrentUnit.AMPS)>5){
                powerMultiplierR=5*(1/rightLift.getCurrent(CurrentUnit.AMPS));
            }else{
                powerMultiplierR=1;
            }

            leftLift.setPower(magnitude);
            rightLift.setPower(magnitude);
            where = Level.NOWHERE;
            /*stateOfDown = false;
            stateOfUp = false;*/

        }
        else
        {
            leftLift.setPower(0);
            rightLift.setPower(0);
        }
        currentOpMode.telemetry.addData("Snagger slide power", magnitude);

        currentOpMode.telemetry.addData("Snagger slide amps", "left lift:"+leftLift.getCurrent(CurrentUnit.AMPS));
        currentOpMode.telemetry.addData("Snagger slide amps", "right lift"+rightLift.getCurrent(CurrentUnit.AMPS));
    }

    public void moveLevels(boolean upPressed, boolean downPressed){
        switch (where){
            case NOWHERE:
                if (upPressed){
                    moveToTop();
                }
                if (downPressed){
                    moveToBottom();
                }
            case HIGH:
                if (downPressed){
                    moveToBottom();
                }
            case LOW:
                if (upPressed) {
                    moveToTop();
                }
            default:
                stay();
        }
        //currentOpMode.telemetry.addData("slide position", );
    }

    public void moveToTop(){
        leftLift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightLift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        leftLift.setTargetPosition(highPos);
        rightLift.setTargetPosition(highPos);
        rightLift.setPower(power);
        leftLift.setPower(power);
        leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        where = Level.HIGH;
        while(rightLift.isBusy() || leftLift.isBusy()){}

    }

    public void moveToBottom(){
        leftLift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightLift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        leftLift.setTargetPosition(lowPos);
        rightLift.setTargetPosition(lowPos);
        leftLift.setPower(power);
        rightLift.setPower(power);
        leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        where = Level.LOW;
        while(rightLift.isBusy() || leftLift.isBusy()){}
    }

    public void stay(){
        leftLift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightLift.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        leftLift.setPower(power);
        rightLift.setPower(power);
        leftLift.setTargetPosition(leftLift.getCurrentPosition());
        rightLift.setTargetPosition(rightLift.getCurrentPosition());
    }



    public enum Level {
        LOW,
        HIGH,
        NOWHERE
    }

    public void stop() {
        rightLift.setPower(0);
        leftLift.setPower(0);
    }
}
