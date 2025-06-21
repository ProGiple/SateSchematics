package org.satellite.dev.progiple.sateschematics.schems.pasted;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public record PastedBlock(BlockData previousState, Block nowBlock) {
}
