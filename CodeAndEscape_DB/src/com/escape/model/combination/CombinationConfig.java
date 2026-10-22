package main.java.com.escape.model.combination;
public class CombinationConfig {

    // Static method - call this once at game startup
    public static void initialize(CombinationManager manager) {

        manager.addCombination(new Combination(
                "magnifying_glass",
                "letter",
                "readable_letter"
        ));

        manager.addCombination(new Combination(
                "key",
                "locked_box",
                "old_journal"
        ));

        manager.addCombination(new Combination(
                "old_journal",
                "candle",
                "flammable_paper"
        ));

        manager.addCombination(new Combination(
                "wet_cloth",
                "lightbulb",
                "cold_lightbulb"
        ));
    }
}