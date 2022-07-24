package git.buchard36.civilizations.npc;

import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockReference {

    public final ChunkSnapshot snapshot;
    public final int blockX;
    public final int blockY;
    public final int blockZ;
    public final Material material;

    public BlockReference(ChunkSnapshot snapshot,
                          int blockX,
                          int blockY,
                          int blockZ,
                          Material blockType) {
        this.snapshot = snapshot;
        this.blockX = blockX;;
        this.blockY = blockY;
        this.blockZ = blockZ;
        this.material = blockType;
    }

    public Block getBlock() {
        return Bukkit.getWorld(snapshot.getWorldName())
                .getChunkAt(snapshot.getX(), snapshot.getZ()).getBlock(blockX, blockY, blockZ);
    }

}
