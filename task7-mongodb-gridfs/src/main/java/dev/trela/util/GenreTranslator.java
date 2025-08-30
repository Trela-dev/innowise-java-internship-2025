package dev.trela.util;

import java.util.Map;
public class GenreTranslator {
    private static final Map<String, String> genreTranslationMap = Map.ofEntries(
            Map.entry("Fantasy", "Fantasy"),
            Map.entry("Science Fiction", "Science Fiction"),
            Map.entry("Horror", "Horror"),
            Map.entry("Tajemnica","Mystery"),
            Map.entry("Kryminał", "Crime"),
            Map.entry("Thriller", "Thriller"),
            Map.entry("Romans", "Romance"),
            Map.entry("Historia", "Historical"),
            Map.entry("Literatura faktu", "Non-Fiction"),
            Map.entry("Biografia", "Biography"),
            Map.entry("Poradniki", "Self-Help"),
            Map.entry("Przygoda", "Adventure"),
            Map.entry("Młodzieżowa", "Young Adult"),
            Map.entry("Dziecięca", "Children's"),
            Map.entry("Dramat", "Drama"),
            Map.entry("Poezja", "Poetry"),
            Map.entry("Klasyka", "Classic"),
            Map.entry("Dystopia", "Dystopian"),
            Map.entry("Komedia", "Comedy"),
            Map.entry("Filozofia", "Philosophy")
    );

    public static String translateToEnglish(String polishGenre) {
        return genreTranslationMap.getOrDefault(polishGenre, polishGenre);
    }
    private GenreTranslator() {
    }

}
