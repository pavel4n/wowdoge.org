package org.wowdoge;
 
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class CustomTheme extends DefaultMetalTheme
{
	private String szThemeName = "Custom Theme";

    private ColorUIResource primary1;
    private ColorUIResource primary2;
    private ColorUIResource primary3;

    private ColorUIResource secondary1;
    private ColorUIResource secondary2;
    private ColorUIResource secondary3;

    private ColorUIResource black;
    private ColorUIResource white;
    
    private FontUIResource windowTitleFont;
    private FontUIResource controlFont;
    

//******************************************************************************************	
// Function Name : CustomTheme(InputStream is) => the default constructor
// Parameter : InputStream is => since the custom theme will be loaded from a properties file, 	
//			   the constructor will take an i/p stream as a parameter.
//	
// Initially sets colors and fonts to their default values, and then reads these values from
// the theme file's input stream
//	
//******************************************************************************************

    public CustomTheme(InputStream is)
    {
    	defaultColors();	
    	defaultFonts();
    	loadProperties(is);
    }

//******************************************************************************************	
// Function Name : defaultFonts()
// Parameter : None
// Returns : None
//	
// sets fonts to that of Super's(DefaultMetalTheme) default 
//	
//******************************************************************************************
    
    private void defaultFonts()
    {
    	windowTitleFont = super.getWindowTitleFont();
    	controlFont		= super.getControlTextFont();
    }
    
//******************************************************************************************	
// Function Name : defaultColors()
// Parameter : None
// Returns : None
//	
// sets colors to that of Super's(DefaultMetalTheme) default 
//	
//******************************************************************************************    
    
    private void defaultColors()
    {
    	primary1 		= super.getPrimary1();
        primary2 		= super.getPrimary2();
        primary3 		= super.getPrimary3();

        secondary1 		= super.getSecondary1();
        secondary2 		= super.getSecondary2();
        secondary3 		= super.getSecondary3();

		black = super.getBlack();
		white = super.getWhite();
    }
    
//******************************************************************************************	
// Function Name : defaultColors()
// Parameter : None
// Returns : None
//	
// Read colors and fonts from the theme file's(which is a property file) input stream and set 
// values of primary colors, secondary colors and configurable fonts accordingly. If any value 
// is missing default value of the DefaultMetalTheme is used
//	
//******************************************************************************************      
    
    private void loadProperties(InputStream is)
    {
    	Properties prop = new Properties();
    	
    	try{
    			prop.load(is);
    	}catch(IOException e)
    	{
    			System.out.println(e);	
    	}
    	
    		// get theme name
    	String szTemp = prop.getProperty("name");
    	if(null != szTemp)
    	{
    		szThemeName = szTemp;
    	}
    	
    		//get primary colors	
    	szTemp = prop.getProperty("primary1");
    	if(null != szTemp)
    	{
    		primary1 = parseColor(szTemp);
    	}
    	szTemp = prop.getProperty("primary2");
    	if(null != szTemp)
    	{
    		primary2 = parseColor(szTemp);
    	}
    	szTemp = prop.getProperty("primary3");
    	if(null != szTemp)
    	{
    		primary3 = parseColor(szTemp);
    	}
    		//get secondary colors
    	szTemp = prop.getProperty("secondary1");
    	if(null != szTemp)
    	{
    		secondary1 = parseColor(szTemp);
    	}
    	szTemp = prop.getProperty("secondary2");
    	if(null != szTemp)
    	{
    		secondary2 = parseColor(szTemp);
    	}
    	szTemp = prop.getProperty("secondary3");
    	if(null != szTemp)
    	{
    		secondary3 = parseColor(szTemp);
    	}
    		//get black
    	szTemp = prop.getProperty("black");
    	if(null != szTemp)
    	{
    		black = parseColor(szTemp);
    	}
    	
    		//get white
    	szTemp = prop.getProperty("white");
    	if(null != szTemp)
    	{
    		white = parseColor(szTemp);
    	}
    	
    		//get window title font
    	szTemp = prop.getProperty("titlefont");
    	if(null != szTemp)
    	{
    		windowTitleFont = parseFont(szTemp);
    	}
    	
    		//get control font, for menu, and other controls (buttons, list etc)
    	szTemp = prop.getProperty("controlfont");
    	if(null != szTemp)
    	{
    		controlFont = parseFont(szTemp);
    	}
    }

//******************************************************************************************	
// Function Name : parseColor(String color)
// Parameter : String color => the color from the theme file
// Returns : ColorUIResource , parsed color
//	
// Parses the color String, which is of the form => RED, GREEN, BLUE in decimals and returns
// a ColorUIResource value constructed from this
//	
//******************************************************************************************  
    
    private ColorUIResource parseColor(String color)
    {
    	int r,g,b;
    	r = g = b = 0;
    	try{
    			StringTokenizer stok = new StringTokenizer(color, ",");
    			r = Integer.parseInt(stok.nextToken());
    			g = Integer.parseInt(stok.nextToken());
    			b = Integer.parseInt(stok.nextToken());
    	}catch(Exception e)
    	{
    		System.out.println(e);
	    	System.out.println("Couldn't parse color :" + color);
    	}
    	
    	return new ColorUIResource(r, g, b);
    }
    
//******************************************************************************************	
// Function Name : parseFont(String font)
// Parameter : String font => the font from the theme file
// Returns : FontUIResource , parsed font
//	
// Parses the color String, which is of the form => FONT_NAME, FONT_STYLE, FONT_SIZE and 
// returns a FontUIResource value constructed from this
//	
//******************************************************************************************     
    
    private FontUIResource parseFont(String font)
    {
    	String szName = "Dialog";
    	int iSize=12, iStyle=Font.PLAIN;
    	
    	try{
    			StringTokenizer stok = new StringTokenizer(font, ",");
    			szName 	= stok.nextToken();
    			String tmp 	= stok.nextToken();
    			iSize 	= Integer.parseInt(stok.nextToken());
    				
    				//get font style
    			if(tmp.equalsIgnoreCase("Font.BOLD"))
    			{
    				iStyle = Font.BOLD;
    			}
    			else if(tmp.equalsIgnoreCase("Font.ITALIC"))
    			{
    				iStyle = Font.ITALIC;
    			}
    			else if(tmp.equalsIgnoreCase("Font.BOLD|Font.ITALIC") ||
    					tmp.equalsIgnoreCase("Font.ITALIC|Font.BOLD"))
    			{
    				iStyle = Font.BOLD|Font.ITALIC;
    			}
    			
    	}catch(Exception e)
    	{
    		System.out.println(e);
	    	System.out.println("Couldn't parse font :" + font);
    	}
    	
    	return new FontUIResource(szName,iStyle,iSize);
    	
    }
	    
		// the functions overridden from the base class => DefaultMetalTheme

    
    public String getName() { return szThemeName; }

    protected ColorUIResource getPrimary1() { return primary1; }
    protected ColorUIResource getPrimary2() { return primary2; }
    protected ColorUIResource getPrimary3() { return primary3; }

    protected ColorUIResource getSecondary1() { return secondary1; }
    protected ColorUIResource getSecondary2() { return secondary2; }
    protected ColorUIResource getSecondary3() { return secondary3; }

    protected ColorUIResource getBlack() { return black; }
    protected ColorUIResource getWhite() { return white; }
    
    public FontUIResource getWindowTitleFont() { return windowTitleFont;}
    public FontUIResource getMenuTextFont() { return controlFont;}
    public FontUIResource getControlTextFont() { return controlFont;}

}