package org.example.loyalty.model;
import java.util.List;

public class PointsResponse {

    private long basePoints;
    private double tierBonus;
    private double promoBonus;
    private double totalPoints;
    private double effectiveFxRate;
    private List<String> warnings;

    public PointsResponse() {}

    public PointsResponse(long basePoints, double tierBonus, double promoBonus, double totalPoints, double effectiveFxRate, List<String> warnings) {
        this.basePoints = basePoints;
        this.tierBonus = tierBonus;
        this.promoBonus = promoBonus;
        this.totalPoints = totalPoints;
        this.effectiveFxRate = effectiveFxRate;
        this.warnings = warnings;
    }

    public long getBasePoints() { return basePoints; }
    public void setBasePoints(long basePoints) { this.basePoints = basePoints; }

    public double getTierBonus() { return tierBonus; }
    public void setTierBonus(double tierBonus) { this.tierBonus = tierBonus; }

    public double getPromoBonus() { return promoBonus; }
    public void setPromoBonus(double promoBonus) { this.promoBonus = promoBonus; }

    public double getTotalPoints() { return totalPoints; }
    public void setTotalPoints(double totalPoints) { this.totalPoints = totalPoints; }

    public double getEffectiveFxRate() { return effectiveFxRate; }
    public void setEffectiveFxRate(double effectiveFxRate) { this.effectiveFxRate = effectiveFxRate; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
}

