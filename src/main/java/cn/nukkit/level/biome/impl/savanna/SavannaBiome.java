package cn.nukkit.level.biome.impl.savanna;

import cn.nukkit.block.BlockSapling;
import cn.nukkit.level.biome.type.GrassyBiome;
import cn.nukkit.level.generator.populator.impl.PopulatorFlower;
import cn.nukkit.level.generator.populator.impl.tree.SavannaTreePopulator;

/**
 * @author DaPorkchop_
 */
public class SavannaBiome extends GrassyBiome {

    public SavannaBiome() {
        super();

        SavannaTreePopulator tree = new SavannaTreePopulator(BlockSapling.ACACIA);
        tree.setBaseAmount(1);
        this.addPopulator(tree);

        PopulatorFlower flower = new PopulatorFlower();
        flower.setBaseAmount(2);
        this.addPopulator(flower);

        this.setBaseHeight(0.125f);
        this.setHeightVariation(0.05f);
    }

    @Override
    public String getName() {
        return "Savanna";
    }
}
