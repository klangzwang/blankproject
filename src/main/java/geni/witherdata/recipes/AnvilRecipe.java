package geni.witherdata.recipes;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import geni.withercore.recipe.OutputStack;
import geni.withercore.recipe.WitherRecipe;
import geni.withercore.init.WUTRecipes;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class AnvilRecipe implements WitherRecipe<RecipeInput> {
	
    private final Ingredient input;
    private final ItemStack output;
	private final int hitcounter;
    private final SoundEvent sound;
	private final int count;
	private final int bonus;
	
    public AnvilRecipe(Ingredient input, ItemStack output, int hitcounter, SoundEvent sound, int count, int bonus)
    {
        this.input = input;
        this.output = output;
        this.hitcounter = hitcounter;
        this.sound = sound;
        this.count = count;
        this.bonus = bonus;
    }

	@Override
	public List<OutputStack> craft(RecipeInput container, RegistryAccess registryAccess) { return null; }
	@Override
	public List<OutputStack> getResultStacks(RegistryAccess registryAccess) { return null; }
	
	@Override
	public boolean matches(RecipeInput recipeInput, Level p_345375_)
	{
    	return input.test(recipeInput.getItem(0));
	}
	
	@Override
	public ItemStack assemble(RecipeInput p_345149_, Provider p_346030_)
	{
        return this.output.copy();
	}
	
	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight)
	{
        return true;
	}
	
	@Override
	public ItemStack getResultItem(Provider pRegistries)
	{
		return output;
	}
    
    public Ingredient input()
    {
        return input;
    }
    
    public ItemStack output()
    {
        return output;
    }
    
    public int hitcounter()
    {
        return hitcounter;
    }
    
    public SoundEvent sound()
    {
        return sound;
    }
    
    public int count()
    {
        return count;
    }
    
    public int bonus()
    {
        return bonus;
    }
    
    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return WUTRecipes.ANVIL.serializer().get();
    }
    
    @Override
    public RecipeType<?> getType()
    {
        return WUTRecipes.ANVIL.type().get();
    }

    public record RecipeIn(ItemStack input) implements RecipeInput
    {
        @Override
        public ItemStack getItem(int slotIndex)
        {
            return input;
        }

        @Override
        public int size()
        {
            return 1;
        }
    }

    public static class Serializer implements RecipeSerializer<AnvilRecipe>
    {
        public static final MapCodec<AnvilRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
    		Ingredient.CODEC.fieldOf("input").forGetter(AnvilRecipe::input),
    		ItemStack.CODEC.fieldOf("output").forGetter(AnvilRecipe::output),
            Codec.INT.fieldOf("hitcounter").forGetter(AnvilRecipe::hitcounter),
            SoundEvent.DIRECT_CODEC.fieldOf("sound").forGetter(AnvilRecipe::sound),
        	Codec.INT.fieldOf("count").forGetter(AnvilRecipe::count),
            Codec.INT.fieldOf("bonus").forGetter(AnvilRecipe::bonus))
    		.apply(inst, AnvilRecipe::new));
        
        public static final StreamCodec<RegistryFriendlyByteBuf, AnvilRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, AnvilRecipe::input,
            ItemStack.STREAM_CODEC, AnvilRecipe::output,
            ByteBufCodecs.INT, AnvilRecipe::hitcounter,
            SoundEvent.DIRECT_STREAM_CODEC, AnvilRecipe::sound,
            ByteBufCodecs.INT, AnvilRecipe::count,
            ByteBufCodecs.INT, AnvilRecipe::bonus,
            AnvilRecipe::new);

        @Override
        public MapCodec<AnvilRecipe> codec()
        {
            return CODEC;
        }
        
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AnvilRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }
    }
}
