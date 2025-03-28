package com.Estoque.api;

import javafx.scene.text.Font;

public class LoadFonts {

	private static Font loadAutomatically(String path, double size) {
		Font font = Font.loadFont(Font.class.getResource(path).toExternalForm(), size);
		return font != null ? font: null;
	}
	public static void allFonts(){
		loadAutomatically("/fonts/game_over.ttf", 0);
		loadAutomatically("/fonts/goldenAge.ttf", 0);
		loadAutomatically("/fonts/ka1.ttf", 0);
		loadAutomatically("/fonts/unlearn.ttf", 0);
		loadAutomatically("/fonts/RETROTECH.ttf", 0);
		loadAutomatically("/fonts/good_timing.otf", 0);
	}
}
