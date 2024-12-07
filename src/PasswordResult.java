public class PasswordResult {
    private int score;
    private String suggestions;
    private double entropy;

    public PasswordResult(int score, String suggestions, double entropy) {
        this.score = score;
        this.suggestions = suggestions;
        this.entropy = entropy;
    }

    public int getScore() {
        return score;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public double getEntropy() {
        return entropy;
    }
}
