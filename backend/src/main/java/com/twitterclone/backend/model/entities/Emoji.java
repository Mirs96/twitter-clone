package com.twitterclone.backend.model.entities;

public enum Emoji {
    HEART("❤️"),
    THUMBS_UP("👍"),
    LAUGH("😂"),
    SAD("😢");

    private String symbol;

    Emoji(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static Emoji fromSymbol(String symbol) {
        for (Emoji e : values()) {
            if (e.symbol.equals(symbol)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Emoji non valida: " + symbol);
    }
}
