package mapwriter.util;

public class BiomeColors {
    private final int water;
    private final int grass;
    private final int foliage;

    public BiomeColors(int water, int grass, int foliage) {
        this.water = water;
        this.grass = grass;
        this.foliage = foliage;
    }

    public int getWater() {
        return this.water;
    }

    public int getGrass() {
        return this.grass;
    }

    public int getFoliage() {
        return this.foliage;
    }
}