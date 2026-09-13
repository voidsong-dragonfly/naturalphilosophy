package voidsong.naturalphilosophy.common.worldgen.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CoralWallFanBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import voidsong.naturalphilosophy.common.NPBlocks;


public class FossilReefFeature extends Feature<NoneFeatureConfiguration> {
    public FossilReefFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        StructureTemplateManager templateManager = level.getLevel().getServer().getStructureManager();
        StructureTemplate skull = templateManager.getOrCreate(ResourceLocation.fromNamespaceAndPath("minecraft", "fossil/skull_" + (1 + random.nextInt(4))));
        StructureTemplate spine = templateManager.getOrCreate(ResourceLocation.fromNamespaceAndPath("minecraft", "fossil/spine_" + (1 + random.nextInt(4))));

        Rotation rotation = Rotation.getRandom(random);
        StructurePlaceSettings settings = (new StructurePlaceSettings()).setRotation(rotation).setRandom(random).addProcessor(new BlockRotProcessor(HolderSet.direct(Blocks.BONE_BLOCK.builtInRegistryHolder()), 0.9f));

        int heightOffset = -(random.nextIntBetweenInclusive(1, 2));
        int radius = Math.max(spine.getSize().getX(), spine.getSize().getZ()) + Math.max(skull.getSize().getX(), skull.getSize().getZ());

        BlockPos spineOrigin = switch(rotation) {
            case Rotation.NONE -> origin.offset(-(int)Math.floor((double)spine.getSize().getX()/2), heightOffset, 0);
            case Rotation.CLOCKWISE_90 -> origin.offset(0, heightOffset, -(int)Math.floor((double)spine.getSize().getX()/2));
            case Rotation.CLOCKWISE_180 -> origin.offset((int)Math.floor((double)spine.getSize().getX()/2),   heightOffset, 0);
            case Rotation.COUNTERCLOCKWISE_90 -> origin.offset(0,   heightOffset, (int)Math.floor((double)spine.getSize().getX()/2));
        };
        BlockPos skullOrigin = switch(rotation) {
            case Rotation.NONE -> origin.offset(-(int)Math.floor((double)skull.getSize().getX()/2),   heightOffset, -(skull.getSize().getZ()-1));
            case Rotation.CLOCKWISE_90 -> origin.offset(skull.getSize().getZ()-1,   heightOffset, -(int)Math.floor((double)skull.getSize().getX()/2));
            case Rotation.CLOCKWISE_180 -> origin.offset((int)Math.floor((double)skull.getSize().getX()/2),   heightOffset, skull.getSize().getZ()-1);
            case Rotation.COUNTERCLOCKWISE_90 -> origin.offset(-(skull.getSize().getZ()-1),   heightOffset, (int)Math.floor((double)skull.getSize().getX()/2));
        };

        spine.placeInWorld(level, spineOrigin, spineOrigin, settings, random, 3);
        skull.placeInWorld(level, skullOrigin, skullOrigin, settings, random, 3);

        for (BlockPos attachable : BlockPos.betweenClosed(origin.offset(-radius, -2, -radius), origin.offset(radius, 2, radius))) {
            if (level.getBlockState(attachable).is(Blocks.BONE_BLOCK)) {
                for (Direction relative : Direction.values()) {
                    if (relative.getAxis().isHorizontal() && level.getBlockState(attachable.relative(relative)).is(Blocks.WATER) && random.nextInt(5) == 0) {
                        level.setBlock(attachable.relative(relative), NPBlocks.BONE_CORAL_WALL_FAN.get().defaultBlockState().setValue(CoralWallFanBlock.FACING, relative), 3);
                    }
                    if (relative.equals(Direction.UP) && level.getBlockState(attachable.relative(relative)).is(Blocks.WATER)) {
                        if (random.nextInt(8) == 0) level.setBlock(attachable.relative(relative), NPBlocks.RED_ALGAE.get().defaultBlockState(), 3);
                        else if (random.nextInt(5) == 0) level.setBlock(attachable.relative(relative), NPBlocks.BONE_CORAL_FAN.get().defaultBlockState(), 3);
                        else if (random.nextInt(4) == 0) level.setBlock(attachable.relative(relative), NPBlocks.BONE_CORAL.get().defaultBlockState(), 3);
                    }
                }
            }
        }
        return true;
    }
}
