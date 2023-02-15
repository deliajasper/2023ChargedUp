// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import javax.lang.model.util.ElementScanner14;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.playingwithfusion.CANVenom.BrakeCoastMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.ArmConstants;
public class ArmToSetpoint extends SubsystemBase {

  /*Behold, My variables */
   TalonFX ArmMotor = new TalonFX(3,"Bobby");
   public double angle = 0.0;
   public double StartEncoderTicks;
   public double AngleDif;


    /* Creates a new Pneumatics subsystem */
    public ArmToSetpoint () 
    {
      ArmMotor.setNeutralMode(NeutralMode.Brake);
      //sets the initil position of the arm segment
      StartEncoderTicks = ArmMotor.getSensorCollection().getIntegratedSensorPosition();
    }
    
    //Moves arm to a desired angle
    public void MoveArm (double DesiredAngle)
    {
    double ArmKp = ArmConstants.UpperArmKP;
    double ArmKI = ArmConstants.UpperArmKI;
    double ArmRatio = ArmConstants.UpperArmRatio;
    double tempOutput;
    double ArmMax = ArmConstants.UpperArmMax;
    double ArmMin = ArmConstants.UpperArmMin;
    double TempEncoderTicks = ArmMotor.getSensorCollection().getIntegratedSensorPosition();
      
      //Calculates angle based on last angle and difference in encoder ticks
      angle = -(TempEncoderTicks- StartEncoderTicks) / (2048*100) *360/(40/24);

      //finds distance from current angle to desired angle
      AngleDif = angle - DesiredAngle;
      double AngleDifAbsolute = Math.abs(AngleDif);
      //Acounts for distance
      if(AngleDifAbsolute > 15)
      {
        tempOutput = ArmMax;
      }
      else
      {
        if(AngleDifAbsolute * ArmKp * ArmKI < ArmMin)
        {
          tempOutput = ArmMin;
          if(AngleDifAbsolute == 0)
          {
            tempOutput = 0;
          }
        }
        else
        {
          tempOutput = AngleDifAbsolute * ArmKp * ArmKI;
        }
      }

      //moves arm motor in the direction it should go in
      if((AngleDif)>0)
      {
        ArmMotor.set(TalonFXControlMode.PercentOutput, tempOutput);
      }
      else if (AngleDif < 0)
      {
        ArmMotor.set(TalonFXControlMode.PercentOutput, -tempOutput);
      }
      else
      {
        //yay
      }

      

      //Makes my variables beholdable
      SmartDashboard.putNumber("encoder Ticks", TempEncoderTicks);
      SmartDashboard.putNumber("AngleDif", AngleDif);
      SmartDashboard.putNumber("Angle", angle);
    }

    // zeros the current angle, will be used with limit switches
    public void Reset()
    {
      angle = 0.0;
      StartEncoderTicks = ArmMotor.getSensorCollection().getIntegratedSensorPosition();
    }

    //keeps the arm from never decelerating
    public void stop()
    {
      ArmMotor.set(TalonFXControlMode.Disabled,0);
  }
}
