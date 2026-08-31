package voidsong.naturalphilosophy.common.worldgen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import voidsong.naturalphilosophy.NaturalPhilosophy;
import voidsong.naturalphilosophy.common.worldgen.treedecorators.FlamingBromeliadDecorator;
import voidsong.naturalphilosophy.common.worldgen.treedecorators.WallFernDecorator;

public class NPTreeDecorators {
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(BuiltInRegistries.TREE_DECORATOR_TYPE, NaturalPhilosophy.MODID);

    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<FlamingBromeliadDecorator>> FLAMING_BROMELIAD = TREE_DECORATORS.register("flaming_bromeliad", () -> new TreeDecoratorType<>(FlamingBromeliadDecorator.CODEC));
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<WallFernDecorator>> WALL_FERN = TREE_DECORATORS.register("wall_fern", () -> new TreeDecoratorType<>(WallFernDecorator.CODEC));
}
