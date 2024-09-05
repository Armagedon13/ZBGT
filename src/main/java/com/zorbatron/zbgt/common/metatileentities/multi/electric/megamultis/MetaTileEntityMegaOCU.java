package com.zorbatron.zbgt.common.metatileentities.multi.electric.megamultis;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.zorbatron.zbgt.api.metatileentity.LaserCapableGCYMRecipeMapMultiblockController;

import gregicality.multiblocks.api.capability.impl.GCYMMultiblockRecipeLogic;
import gregtech.api.block.IHeatingCoilBlockStats;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;

public class MetaTileEntityMegaOCU extends LaserCapableGCYMRecipeMapMultiblockController {

    private int coilTier;

    public MetaTileEntityMegaOCU(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.CRACKING_RECIPES);
        this.recipeMapWorkable = new CrackingUnitWorkableHandler(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityMegaOCU(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXEEEEEEEEEXX", "#X#########X#", "#X#########X#", "#X#########X#", "#X#########X#",
                        "#X#########X#", "#X#########X#")
                .aisle("XXXXXXXXXXXXX", "XGGGGGGGGGGGX", "XGGGGGGGGGGGX", "XGGGGGGGGGGGX", "XGGGGGGGGGGGX",
                        "XGGGGGGGGGGGX", "XXGGGGGGGGGXX")
                .aisle("XXXXXXXXXXXXX", "#G#C#C#C#C#G#", "#G#C#C#C#C#G#", "#G#C#C#C#C#G#", "#G#C#C#C#C#G#",
                        "#G#C#C#C#C#G#", "#XGGGGGGGGGX#")
                .aisle("XXXXXXXXXXXXX", "#G#C#C#C#C#G#", "#X###C###C#X#", "#X#C#C#C#C#X#", "#X###C###C#X#",
                        "#G#C#C#C#C#G#", "#XGGGXXXGGGX#")
                .aisle("XXXXXXXXXXXXX", "#G#C#C#C#C#G#", "#X#C#C#C#C#X#", "#I#C#C#C#C#O#", "#X#C#C#C#C#X#",
                        "#G#C#C#C#C#G#", "#XGGGXTXGGGX#")
                .aisle("XXXXXXXXXXXXX", "#G#C#C#C#C#G#", "#X###C###C#X#", "#X#C#C#C#C#X#", "#X###C###C#X#",
                        "#G#C#C#C#C#G#", "#XGGGXXXGGGX#")
                .aisle("XXXXXXXXXXXXX", "#G#C#C#C#C#G#", "#G#C#C#C#C#G#", "#G#C#C#C#C#G#", "#G#C#C#C#C#G#",
                        "#G#C#C#C#C#G#", "#XGGGGGGGGGX#")
                .aisle("XXXXXXXXXXXXX", "XGGGGGGGGGGGX", "XGGGGGGGGGGGX", "XGGGGGGGGGGGX", "XGGGGGGGGGGGX",
                        "XGGGGGGGGGGGX", "XXGGGGGGGGGXX")
                .aisle("XXEEEESEEEEXX", "#X#########X#", "#X#########X#", "#X#########X#", "#X#########X#",
                        "#X#########X#", "#X#########X#")
                .where('S', selfPredicate())
                .where('C', heatingCoils())
                .where('X', states(getCasingState()))
                .where('I', abilities(MultiblockAbility.IMPORT_FLUIDS))
                .where('T', abilities(MultiblockAbility.IMPORT_FLUIDS).or(states(getCasingState())))
                .where('O', abilities(MultiblockAbility.EXPORT_FLUIDS))
                .where('E', states(getCasingState())
                        .or(autoAbilities(false, true, true, true, false, false, false))
                        .or(autoEnergyInputs(1, 8)))
                .where('G', states(getGlassState()))
                .where('#', air())
                .build();
    }

    protected IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STAINLESS_CLEAN);
    }

    protected IBlockState getGlassState() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.CLEAN_STAINLESS_STEEL_CASING;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format("gregtech.machine.cracker.tooltip.1"));
    }

    @Override
    public SoundEvent getBreakdownSound() {
        return GTSoundEvents.BREAKDOWN_ELECTRICAL;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom(tl -> {
                    // Coil energy discount line
                    if (isStructureFormed()) {
                        ITextComponent energyDiscount = TextComponentUtil.stringWithColor(TextFormatting.AQUA,
                                (100 - 10 * coilTier) + "%");

                        ITextComponent base = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gregtech.multiblock.cracking_unit.energy",
                                energyDiscount);

                        ITextComponent hover = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gregtech.multiblock.cracking_unit.energy_hover");

                        tl.add(TextComponentUtil.setHover(base, hover));
                    }
                })
                .addParallelsLine(recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine()
                .addProgressLine(recipeMapWorkable.getProgressPercent());
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        Object type = context.get("CoilType");
        if (type instanceof IHeatingCoilBlockStats) {
            this.coilTier = ((IHeatingCoilBlockStats) type).getTier();
        } else {
            this.coilTier = 0;
        }
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.coilTier = -1;
    }

    protected int getCoilTier() {
        return this.coilTier;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    private class CrackingUnitWorkableHandler extends GCYMMultiblockRecipeLogic {

        public CrackingUnitWorkableHandler(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        protected void modifyOverclockPost(int[] resultOverclock, @NotNull IRecipePropertyStorage storage) {
            super.modifyOverclockPost(resultOverclock, storage);

            int coilTier = ((MetaTileEntityMegaOCU) metaTileEntity).getCoilTier();
            if (coilTier <= 0)
                return;

            resultOverclock[0] *= 1.0f - coilTier * 0.1; // each coil above cupronickel (coilTier = 0) uses 10% less
            // energy
            resultOverclock[0] = Math.max(1, resultOverclock[0]);
        }
    }
}
