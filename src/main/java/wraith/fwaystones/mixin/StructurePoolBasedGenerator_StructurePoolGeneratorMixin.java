package wraith.fwaystones.mixin;

import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import wraith.fwaystones.access.StructurePoolBasedGenerator_StructurePoolGeneratorAccess;

@Mixin(StructurePoolBasedGenerator.StructurePoolGenerator.class)
public class StructurePoolBasedGenerator_StructurePoolGeneratorMixin implements StructurePoolBasedGenerator_StructurePoolGeneratorAccess {

    @Unique
    private int maxWaystoneCount = -1;

    @Unique
    public void fabricWaystones$setMaxWaystoneCount(int maxWaystoneCount) {
        this.maxWaystoneCount = maxWaystoneCount;
    }
}
