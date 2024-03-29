package SSGUI.Theme;

public final class MenuTheme extends Theme{

  @Override
  protected void init(){
    putColor("background",new Color(220,220,220));
    putColor("selectedBackground",new Color(200,200,200));
    putColor("foreground",new Color(0,0,0));
    putColor("selectedForeground",new Color(250,250,250));
    putColor("foreground_fill",new Color(45,50,50));
    putColor("border",new Color(0,0,0));
    putColor("selectedBorder",new Color(0,150,255));
  }
}
