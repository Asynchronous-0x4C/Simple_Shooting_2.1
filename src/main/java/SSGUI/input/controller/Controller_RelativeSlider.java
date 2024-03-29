package SSGUI.input.controller;

import net.java.games.input.Component;

public class Controller_RelativeSlider extends Controller_Slider {
  private float pollValue;
  
  public Controller_RelativeSlider(Component component){
    super(component);
  }

  @Override
  public void update(){
    rawValue=component.getPollData();
    pollValue += rawValue;
  }

  @Override
  public float getTotalValue(){
    return pollValue;
  }
}