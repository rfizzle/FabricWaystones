package wraith.fwaystones.mixin;

import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wraith.fwaystones.FabricWaystones;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementMixin {

    @Unique
    private static final Set<BlockPos> fabricWaystones$generatedPositions = ConcurrentHashMap.newKeySet();

    @Unique
    private volatile Boolean fabricWaystones$isWaystone;

    @Unique
    private boolean fabricWaystones$checkIsWaystone() {
        if (fabricWaystones$isWaystone == null) {
            fabricWaystones$isWaystone = ((SinglePoolElementAccessor) this)
                .getLocation()
                .left()
                .map(id -> id.getNamespace().equals(FabricWaystones.MOD_ID) || id.getNamespace().equals("waystones"))
                .orElse(false);
        }
        return fabricWaystones$isWaystone;
    }

    @Inject(method = "generate(Lnet/minecraft/structure/StructureTemplateManager;Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/BlockRotation;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/structure/StructureLiquidSettings;Z)Z",
        at = @At("HEAD"),
        cancellable = true)
    private void fabricwaystones_limitWaystonePlacement(StructureTemplateManager structureTemplateManager, StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, BlockPos pos, BlockPos pivot, BlockRotation rotation, BlockBox box, Random random, StructureLiquidSettings liquidSettings, boolean keepJigsaws, CallbackInfoReturnable<Boolean> cir) {
        if (!fabricWaystones$checkIsWaystone()) {
            return;
        }

        if (!FabricWaystones.CONFIG.worldgen.generate_in_villages()) {
            cir.setReturnValue(false);
            return;
        }

        // Check if another waystone was already placed nearby (same village)
        for (BlockPos existingPos : fabricWaystones$generatedPositions) {
            if (pos != existingPos && existingPos.getSquaredDistance(pos) < 100 * 100) {
                FabricWaystones.LOGGER.debug("[FWaystones] Prevented duplicate waystone at {} (too close to existing at {})", pos, existingPos);
                cir.setReturnValue(false);
                return;
            }
        }

        fabricWaystones$generatedPositions.add(pos);
        FabricWaystones.LOGGER.debug("[FWaystones] Placing waystone at {}", pos);
    }
}
