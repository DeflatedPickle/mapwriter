package com.cabchinoe.minimap.region;

import java.util.HashMap;

/**
 * Created by n3212 on 2017/8/18.
 */
public class BiomeNameTransfer {
    private HashMap<String,String> BiomeNameMap = new HashMap<String, String>();
    public BiomeNameTransfer(){
        BiomeNameMap.put("Ocean","minimap.biomename.Ocean");
        BiomeNameMap.put("Plains","minimap.biomename.Plains");
        BiomeNameMap.put("Desert","minimap.biomename.Desert");
        BiomeNameMap.put("Extreme Hills","minimap.biomename.ExtremeHills");
        BiomeNameMap.put("Forest","minimap.biomename.Forest");
        BiomeNameMap.put("Taiga","minimap.biomename.Taiga");
        BiomeNameMap.put("Swampland","minimap.biomename.Swampland");
        BiomeNameMap.put("River","minimap.biomename.River");
        BiomeNameMap.put("Hell","minimap.biomename.Hell");
        BiomeNameMap.put("The End","minimap.biomename.TheEnd");
        BiomeNameMap.put("FrozenOcean","minimap.biomename.FrozenOcean");
        BiomeNameMap.put("FrozenRiver","minimap.biomename.FrozenRiver");
        BiomeNameMap.put("Ice Plains","minimap.biomename.IcePlains");
        BiomeNameMap.put("Ice Mountains","minimap.biomename.IceMountains");
        BiomeNameMap.put("MushroomIsland","minimap.biomename.MushroomIsland");
        BiomeNameMap.put("MushroomIslandShore","minimap.biomename.MushroomIslandShore");
        BiomeNameMap.put("Beach","minimap.biomename.Beach");
        BiomeNameMap.put("DesertHills","minimap.biomename.DesertHills");
        BiomeNameMap.put("ForestHills","minimap.biomename.ForestHills");
        BiomeNameMap.put("TaigaHills","minimap.biomename.TaigaHills");
        BiomeNameMap.put("Extreme Hills Edge","minimap.biomename.ExtremeHillsEdge");
        BiomeNameMap.put("Jungle","minimap.biomename.Jungle");
        BiomeNameMap.put("JungleHills","minimap.biomename.JungleHills");
        BiomeNameMap.put("JungleEdge","minimap.biomename.JungleEdge");
        BiomeNameMap.put("Deep Ocean","minimap.biomename.DeepOcean");
        BiomeNameMap.put("Stone Beach","minimap.biomename.StoneBeach");
        BiomeNameMap.put("Cold Beach","minimap.biomename.ColdBeach");
        BiomeNameMap.put("Birch Forest","minimap.biomename.BirchForest");
        BiomeNameMap.put("Birch Forest Hills","minimap.biomename.BirchForestHills");
        BiomeNameMap.put("Roofed Forest","minimap.biomename.RoofedForest");
        BiomeNameMap.put("Cold Taiga","minimap.biomename.ColdTaiga");
        BiomeNameMap.put("Cold Taiga Hills","minimap.biomename.ColdTaigaHills");
        BiomeNameMap.put("Mega Taiga","minimap.biomename.MegaTaiga");
        BiomeNameMap.put("Mega Taiga Hills","minimap.biomename.MegaTaigaHills");
        BiomeNameMap.put("Extreme Hills+","minimap.biomename.ExtremeHills+");
        BiomeNameMap.put("Savanna","minimap.biomename.Savanna");
        BiomeNameMap.put("Savanna Plateau","minimap.biomename.SavannaPlateau");
        BiomeNameMap.put("Mesa","minimap.biomename.Mesa");
        BiomeNameMap.put("Mesa Plateau F","minimap.biomename.MesaPlateauF");
        BiomeNameMap.put("Mesa Plateau","minimap.biomename.MesaPlateau");
        BiomeNameMap.put("The Void","minimap.biomename.TheVoid");
        BiomeNameMap.put("Sunflower Plains","minimap.biomename.SunflowerPlains");
        BiomeNameMap.put("Desert M","minimap.biomename.DesertM");
        BiomeNameMap.put("Extreme Hills M","minimap.biomename.ExtremeHillsM");
        BiomeNameMap.put("Flower Forest","minimap.biomename.FlowerForest");
        BiomeNameMap.put("Taiga M","minimap.biomename.TaigaM");
        BiomeNameMap.put("Swampland M","minimap.biomename.SwamplandM");
        BiomeNameMap.put("Mega Spruce Taiga","minimap.biomename.MegaSpruceTaiga");
        BiomeNameMap.put("Redwood Taiga Hills M","minimap.biomename.RedwoodTaigaHillsM");
        BiomeNameMap.put("Extreme Hills+ M","minimap.biomename.ExtremeHills+M");
        BiomeNameMap.put("Savanna M","minimap.biomename.SavannaM");
        BiomeNameMap.put("Savanna Plateau M","minimap.biomename.SavannaPlateauM");
        BiomeNameMap.put("Mesa (Bryce)","minimap.biomename.Mesa(Bryce)");
        BiomeNameMap.put("Mesa Plateau F M","minimap.biomename.MesaPlateauFM");
        BiomeNameMap.put("Mesa Plateau M","minimap.biomename.MesaPlateauM");
        BiomeNameMap.put("Birch Forest M","minimap.biomename.BirchForestM");
        BiomeNameMap.put("Birch Forest Hills M","minimap.biomename.BirchForestHillsM");
        BiomeNameMap.put("Roofed Forest M","minimap.biomename.RoofedForestM");
        BiomeNameMap.put("Cold Taiga M","minimap.biomename.ColdTaigaM");
        BiomeNameMap.put("Ice Plains Spikes","minimap.biomename.IcePlainsSpikes");
        BiomeNameMap.put("Jungle M","minimap.biomename.JungleM");
        BiomeNameMap.put("JungleEdge M","minimap.biomename.JungleEdgeM");
    }

    public String getBiomeName(String k){
        String I = this.BiomeNameMap.get(k);
        return I == null?k:I;
    }

}
