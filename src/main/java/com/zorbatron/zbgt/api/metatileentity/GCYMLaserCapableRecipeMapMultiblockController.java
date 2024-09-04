package com.zorbatron.zbgt.api.metatileentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.utils.TooltipHelper;

public abstract class GCYMLaserCapableRecipeMapMultiblockController extends GCYMRecipeMapMultiblockController {

    protected final boolean allowSubstationHatches;

    public GCYMLaserCapableRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMap,
                                                         boolean allowSubstationHatches) {
        super(metaTileEntityId, recipeMap);
        this.allowSubstationHatches = allowSubstationHatches;
    }

    public GCYMLaserCapableRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap,
                                                         boolean allowSubstationHatches) {
        this(metaTileEntityId, new RecipeMap<?>[] { recipeMap }, allowSubstationHatches);
    }

    @Override
    protected void initializeAbilities() {
        this.inputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.inputFluidInventory = new FluidTankList(allowSameFluidFillForOutputs(),
                getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.outputInventory = new ItemHandlerList(getAbilities(MultiblockAbility.EXPORT_ITEMS));
        this.outputFluidInventory = new FluidTankList(allowSameFluidFillForOutputs(),
                getAbilities(MultiblockAbility.EXPORT_FLUIDS));

        List<IEnergyContainer> list = new ArrayList<>();
        list.addAll(getAbilities(MultiblockAbility.INPUT_ENERGY));
        list.addAll(getAbilities(MultiblockAbility.INPUT_LASER));
        if (allowSubstationHatches) {
            list.addAll(getAbilities(MultiblockAbility.SUBSTATION_INPUT_ENERGY));
        }

        this.energyContainer = new EnergyContainerList(Collections.unmodifiableList(list));
    }

    @Override
    public TraceabilityPredicate autoAbilities(boolean checkEnergyIn, boolean checkMaintenance, boolean checkItemIn,
                                               boolean checkItemOut, boolean checkFluidIn, boolean checkFluidOut,
                                               boolean checkMuffler) {
        TraceabilityPredicate predicate = super.autoAbilities(false, checkMaintenance, checkItemIn, checkItemOut,
                checkFluidIn, checkFluidOut, checkMuffler);

        if (checkEnergyIn) {
            predicate.or(autoEnergyInputs());
        }

        return predicate;
    }

    public TraceabilityPredicate autoEnergyInputs(int min, int max) {
        if (allowSubstationHatches) {
            return new TraceabilityPredicate(abilities(MultiblockAbility.INPUT_ENERGY, MultiblockAbility.INPUT_LASER,
                    MultiblockAbility.SUBSTATION_INPUT_ENERGY)
                            .setMinGlobalLimited(min).setMaxGlobalLimited(max));
        } else {
            return new TraceabilityPredicate(abilities(MultiblockAbility.INPUT_ENERGY, MultiblockAbility.INPUT_LASER)
                    .setMinGlobalLimited(min).setMaxGlobalLimited(max));
        }
    }

    public TraceabilityPredicate autoEnergyInputs() {
        return autoEnergyInputs(1, 3);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(I18n.format(I18n.format("zbgt.laser_enabled.1") +
                TooltipHelper.RAINBOW_SLOW + I18n.format("zbgt.laser_enabled.2")));
    }
}
