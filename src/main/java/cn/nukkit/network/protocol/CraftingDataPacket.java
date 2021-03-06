package cn.nukkit.network.protocol;

import cn.nukkit.inventory.*;
import cn.nukkit.item.Item;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Nukkit Project Team
 */
@ToString
public class CraftingDataPacket extends DataPacket {

    public static final String CRAFTING_TAG_CRAFTING_TABLE = "crafting_table";
    public static final String CRAFTING_TAG_CARTOGRAPHY_TABLE = "cartography_table";
    public static final String CRAFTING_TAG_STONECUTTER = "stonecutter";
    public static final String CRAFTING_TAG_FURNACE = "furnace";
    public static final String CRAFTING_TAG_CAMPFIRE = "campfire";
    public static final String CRAFTING_TAG_BLAST_FURNACE = "blast_furnace";
    public static final String CRAFTING_TAG_SMOKER = "smoker";

    private List<Recipe> entries = new ArrayList<>();
    private List<BrewingRecipe> brewingEntries = new ArrayList<>();
    private List<ContainerRecipe> containerEntries = new ArrayList<>();
    public boolean cleanRecipes;

    public void addShapelessRecipe(ShapelessRecipe... recipe) {
        Collections.addAll(entries, recipe);
    }

    public void addShapedRecipe(ShapedRecipe... recipe) {
        Collections.addAll(entries, recipe);
    }

    public void addFurnaceRecipe(FurnaceRecipe... recipe) {
        Collections.addAll(entries, recipe);
    }

    public void addBrewingRecipe(BrewingRecipe... recipe) {
        Collections.addAll(brewingEntries, recipe);
    }

    public void addContainerRecipe(ContainerRecipe... recipe) {
        Collections.addAll(containerEntries, recipe);
    }

    @Override
    public DataPacket clean() {
        entries = new ArrayList<>();
        return super.clean();
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();
        this.putUnsignedVarInt(entries.size());

        for (Recipe recipe : entries) {
            this.putVarInt(recipe.getType().ordinal());
            switch (recipe.getType()) {
                case SHAPELESS:
                    ShapelessRecipe shapeless = (ShapelessRecipe) recipe;
                    if (protocol >= 361) {
                        this.putString(shapeless.getRecipeId());
                    }
                    List<Item> ingredients = shapeless.getIngredientList();
                    this.putUnsignedVarInt(ingredients.size());
                    for (Item ingredient : ingredients) {
                        if (protocol < 361) {
                            this.putSlot(protocol, ingredient);
                        } else {
                            this.putRecipeIngredient(ingredient);
                        }
                    }
                    this.putUnsignedVarInt(1);
                    this.putSlot(protocol, shapeless.getResult());
                    this.putUUID(shapeless.getId());
                    if (protocol >= 354) {
                        this.putString(CRAFTING_TAG_CRAFTING_TABLE);
                        if (protocol >= 361) {
                            this.putVarInt(shapeless.getPriority());
                        }
                    }
                    break;
                case SHAPED:
                    ShapedRecipe shaped = (ShapedRecipe) recipe;
                    if (protocol >= 361) {
                        this.putString(shaped.getRecipeId());
                    }
                    this.putVarInt(shaped.getWidth());
                    this.putVarInt(shaped.getHeight());

                    for (int z = 0; z < shaped.getHeight(); ++z) {
                        for (int x = 0; x < shaped.getWidth(); ++x) {
                            if (protocol < 361) {
                                this.putSlot(protocol, shaped.getIngredient(x, z));
                            } else {
                                this.putRecipeIngredient(shaped.getIngredient(x, z));
                            }
                        }
                    }
                    List<Item> outputs = new ArrayList<>();
                    outputs.add(shaped.getResult());
                    outputs.addAll(shaped.getExtraResults());
                    this.putUnsignedVarInt(outputs.size());
                    for (Item output : outputs) {
                        this.putSlot(protocol, output);
                    }
                    this.putUUID(shaped.getId());
                    if (protocol >= 354) {
                        this.putString(CRAFTING_TAG_CRAFTING_TABLE);
                        if (protocol >= 361) {
                            this.putVarInt(shaped.getPriority());
                        }
                    }
                    break;
                case FURNACE:
                case FURNACE_DATA:
                    FurnaceRecipe furnace = (FurnaceRecipe) recipe;
                    Item input = furnace.getInput();
                    this.putVarInt(input.getId());
                    if (recipe.getType() == RecipeType.FURNACE_DATA) {
                        this.putVarInt(input.getDamage());
                    }
                    this.putSlot(protocol, furnace.getResult());
                    if (protocol >= 354) {
                        this.putString(CRAFTING_TAG_FURNACE);
                    }
                    break;
            }
        }

        if (protocol >= 388) {
            this.putUnsignedVarInt(this.brewingEntries.size());
            for (BrewingRecipe recipe : brewingEntries) {
                this.putVarInt(recipe.getInput().getDamage());
                this.putVarInt(recipe.getIngredient().getId());
                this.putVarInt(recipe.getResult().getDamage());
            }

            this.putUnsignedVarInt(this.containerEntries.size());
            for (ContainerRecipe recipe : containerEntries) {
                this.putVarInt(recipe.getInput().getId());
                this.putVarInt(recipe.getIngredient().getId());
                this.putVarInt(recipe.getResult().getId());
            }
        }

        this.putBoolean(cleanRecipes);
    }

    @Override
    public byte pid() {
        return ProtocolInfo.CRAFTING_DATA_PACKET;
    }
}
