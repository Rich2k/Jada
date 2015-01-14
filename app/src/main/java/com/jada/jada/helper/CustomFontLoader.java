package com.jada.jada.helper;

import android.content.Context;
import android.graphics.Typeface;

public class CustomFontLoader {

	public static final int ROBOTO_MEDIUM = 0;
	//public static final int FONT_NAME_2 = 1;
	//public static final int FONT_NAME_3 = 2;
	
	public static final int NUM_OF_CUSTOM_FONTS = 1;
	
	private static boolean fontsLoaded = false;
	
	private static Typeface[] fonts = new Typeface[NUM_OF_CUSTOM_FONTS];
	
	private static String[] fontPath = {
		"fonts/robotoMedium.ttf",
		//"fonts/FONT_NAME_2.ttf",
		//"fonts/FONT_NAME_3.ttf"
	};
	
	public static Typeface getTypeface(Context context, int fontIdentifier){
		if(!fontsLoaded) {
			loadFonts(context);
		}
		return fonts[fontIdentifier];
	}
	
	private static void loadFonts(Context context){
		for (int i=0;i<NUM_OF_CUSTOM_FONTS;i++){
			fonts[i] = Typeface.createFromAsset(context.getAssets(),fontPath[i]);
		}
		fontsLoaded = true;
	}
}
