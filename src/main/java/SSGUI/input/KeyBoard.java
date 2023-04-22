package SSGUI.input;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.opengl.GLWindow;

import SSGUI.Component.Direction;
import processing.core.PApplet;
import processing.opengl.PSurfaceJOGL;

import static com.jogamp.newt.event.KeyEvent.*;

public class KeyBoard extends Device {
  private HashSet<Integer>pressedKeys;
  private HashMap<String,HashSet<Integer>>keyBind;
  private HashMap<String,HashMap<String,Consumer<KeyEvent>>>eventMap;

  private short latestKey;
  private boolean keyPress;
  private boolean keyRelease;

  private boolean oldKeyPressed=false;

  private com.jogamp.newt.event.KeyEvent latestEvent;
  
  public static final String eventNames[]=new String[]{"pressed","released"};

  public KeyBoard(PApplet applet,PSurfaceJOGL surface){
    super(applet,surface);
  }

  protected void init(){
    eventMap=new HashMap<>();
    for(String s:eventNames){
      eventMap.put(s,new HashMap<>());
    }
    initKeyBind();
    pressedKeys=new HashSet<>();
    ((GLWindow)surface.getNative()).addKeyListener(new KeyAdapter(){
      @Override
      public void keyPressed(com.jogamp.newt.event.KeyEvent e) {
        keyPress=true;
        latestKey=e.getKeyCode();
        pressedKeys.add((int)e.getKeyCode());
        eventMap.get(eventNames[0]).forEach((k,v)->v.accept(e));
      }
      @Override
      public void keyReleased(com.jogamp.newt.event.KeyEvent e) {
        latestEvent=e;
      }
    });
  }

  private void keyReelasedProcess(com.jogamp.newt.event.KeyEvent e){
    keyRelease=true;
    pressedKeys.remove((int)e.getKeyCode());
    eventMap.get(eventNames[1]).forEach((k,v)->v.accept(e));
  }

  private void initKeyBind(){
    keyBind=new HashMap<>();
    keyBind.put("Up",new HashSet<>(Arrays.asList((int)VK_W,(int)VK_UP)));
    keyBind.put("Down",new HashSet<>(Arrays.asList((int)VK_S,(int)VK_DOWN)));
    keyBind.put("Right",new HashSet<>(Arrays.asList((int)VK_D,(int)VK_RIGHT)));
    keyBind.put("Left",new HashSet<>(Arrays.asList((int)VK_A,(int)VK_LEFT)));
    keyBind.put("Enter",new HashSet<>(Arrays.asList((int)VK_ENTER)));
    keyBind.put("Back",new HashSet<>(Arrays.asList((int)VK_SHIFT)));
    keyBind.put("Menu",new HashSet<>(Arrays.asList((int)VK_CONTROL)));
    keyBind.put("Change",new HashSet<>(Arrays.asList((int)VK_TAB)));
  }

  /**
   * This method updates inner variable state.
   * This method should call after processing the input.
   */
  public void update(){
    keyPress=keyRelease=false;
    if(oldKeyPressed&&!applet.keyPressed)keyReelasedProcess(latestEvent);
    oldKeyPressed=applet.keyPressed;
  }

  public void addProcess(String eventName,String name,Consumer<KeyEvent> process){
    eventMap.get(eventName).put(name,process);
  }

  public Consumer<KeyEvent> getProcess(String eventName,String name){
    return eventMap.get(eventName).get(name);
  }

  public void removeProcess(String eventName,String name){
    eventMap.get(eventName).remove(name);
  }

  public boolean keyPress(){
    return keyPress;
  }

  public boolean keyPressed(){
    return applet.keyPressed;
  }

  public boolean keyRelease(){
    return keyRelease;
  }

  public int keyCode(){
    return applet.keyCode;
  }

  public HashSet<Integer> getPressedKeys(){
    return pressedKeys;
  }

  public boolean getBindedInput(String bind){
    return containsSet(pressedKeys, keyBind.get(bind));
  }

  public Direction getDirection(){
    if(!keyPressed()&&!keyPress())return Direction.None;
    return switch((Integer)(int)latestKey){
      case Integer i when keyBind.get("Up").contains(i)->Direction.Up;
      case Integer i when keyBind.get("Down").contains(i)->Direction.Down;
      case Integer i when keyBind.get("Right").contains(i)->Direction.Right;
      case Integer i when keyBind.get("Left").contains(i)->Direction.Left;
      default->Direction.None;
    };
  }

  public float getAngle(){
    if(!keyPressed()&&!keyRelease())return Float.NaN;
    int binary=0b000000;
    if(containsSet(keyBind.get("Up"), pressedKeys))binary=binary|0b001000;
    if(containsSet(keyBind.get("Down"), pressedKeys))binary=binary|0b000100;
    if(containsSet(keyBind.get("Right"), pressedKeys))binary=binary|0b100000;
    if(containsSet(keyBind.get("Left"), pressedKeys))binary=binary|0b010000;
    return Direction.getAngle(binary);
  }

  private <T> boolean containsSet(Set<T> src,Set<T> target){
    for(T val:target){
      if(src.contains(val))return true;
    }
    return false;
  }
}
