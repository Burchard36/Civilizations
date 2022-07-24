package git.buchard36.civilizations.utils;

import git.buchard36.civilizations.npc.BlockReference;
import org.bukkit.*;

import java.util.*;

public class BlockScanner {

    public BlockScanner() {

    }

    protected List<BlockReference> getReferancesTo(Chunk origin, Material... types) {
        final List<BlockReference> result = new ArrayList<>();
        Collection<ChunkSnapshot> chunks = around(origin, 2);
        for (ChunkSnapshot snapshot : chunks) {
            Bukkit.broadcastMessage("Searching chunks");
            for (int x = 0; x <= 15; x++) {
                for (int z = 0; z <= 15; z++) {
                    for (int y = 40; y <= 200; y++) {
                        Material type = snapshot.getBlockType(x, y, z);
                        if (type == Material.AIR) continue;
                        for (Material aType : types) {
                            if (aType == type) {
                                result.add(new BlockReference(snapshot, x, y, z, aType));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    protected final Collection<ChunkSnapshot> around(Chunk origin, int radius) {
        World world = origin.getWorld();

        int length = (radius * 2) + 1;
        Set<ChunkSnapshot> chunks = new HashSet<>(length * length);

        int cX = origin.getX();
        int cZ = origin.getZ();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(world.getChunkAt(cX + x, cZ + z).getChunkSnapshot());
            }
        }
        return chunks;
    }

}
