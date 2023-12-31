package com.example.lab_6;

public class Score implements Comparable<Score> {

    private final String name;
    private final int score;

    public Score(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        if (score > 376)
            return name.substring(0, Math.min(name.length(),10)) + ": CHEAT";
        else
            return name.substring(0,Math.min(name.length(),10)) + ": " + score;
    }

    @Override
    public int compareTo(Score score) {
        return Integer.compare(this.score, score.score);
    }

}
