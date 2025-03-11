package com.Estoque.api;

import javafx.scene.text.Font;

public class LoadFonts {

	private static Font carregarAutomaticamente(String caminho, double tamanho) {
		Font font = Font.loadFont(Font.class.getResource(caminho).toExternalForm(), tamanho);
		return font != null ? font: null;
	}
	public static void todasAsFontes(){
		carregarAutomaticamente("/fonts/game_over.ttf", 0);
		carregarAutomaticamente("/fonts/goldenAge.ttf", 0);
		carregarAutomaticamente("/fonts/ka1.ttf", 0);
		carregarAutomaticamente("/fonts/unlearn.ttf", 0);
		carregarAutomaticamente("/fonts/RETROTECH.ttf", 0);
		carregarAutomaticamente("/fonts/good_timing.otf", 0);
	}
}
