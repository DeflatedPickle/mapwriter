package com.cabchinoe.minimap;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Maps;
import com.cabchinoe.minimap.region.MwChunk;
import com.cabchinoe.minimap.tasks.SaveChunkTask;
import com.cabchinoe.minimap.tasks.UpdateSurfaceChunksTask;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChunkManager {
	public Mw mw;
	private boolean closed = false;
	private CircularHashMap<Chunk, Integer> chunkMap = new CircularHashMap<Chunk, Integer>();
	
	private static final int VISIBLE_FLAG = 0x01;
	private static final int VIEWED_FLAG = 0x02;
	
	public ChunkManager(Mw mw) {
		this.mw = mw;
	}
	
	public synchronized void close() {
		this.closed = true;
		this.saveChunks();
		this.chunkMap.clear();
	}
	
	// create MwChunk from Minecraft chunk.
	// only MwChunk's should be used in the background thread.
	// make this a full copy of chunk data to prevent possible race conditions <-- done
	public static MwChunk copyToMwChunk(Chunk chunk) {

		Map<BlockPos, TileEntity> TileEntityMap = Maps.newHashMap();
		TileEntityMap.putAll(chunk.getTileEntityMap());
		byte[] biomeArray = Arrays.copyOf(chunk.getBiomeArray(), chunk.getBiomeArray().length);

		ExtendedBlockStorage[] storageArrays = chunk.getBlockStorageArray();

		return new MwChunk(chunk.x, chunk.z, chunk.getWorld().provider.getDimension(),
				storageArrays, biomeArray,TileEntityMap);
	}


	public synchronized void addChunk(Chunk chunk) {
		if (!this.closed && (chunk != null)) {
			this.chunkMap.put(chunk, 0);
		}
	}
	
	public synchronized void removeChunk(Chunk chunk) {
		if (!this.closed && (chunk != null)) {
            if(!this.chunkMap.containsKey(chunk)) return; //FIXME: Is this failsafe enough for unloading?
			int flags = this.chunkMap.get(chunk);
			if ((flags & VIEWED_FLAG) != 0) {
				this.addSaveChunkTask(chunk);
			}
			this.chunkMap.remove(chunk);
		}
	}
	
	public synchronized void saveChunks() {
		for (Map.Entry<Chunk, Integer> entry : this.chunkMap.entrySet()) {
			int flags = entry.getValue();
			if ((flags & VIEWED_FLAG) != 0) {
				this.addSaveChunkTask(entry.getKey());
			}
		}
	}


	public void updateUndergroundChunks() {
		int chunkArrayX = (this.mw.playerXInt >> 4) - 1;
		int chunkArrayZ = (this.mw.playerZInt >> 4) - 1;
		MwChunk[] chunkArray = new MwChunk[9];
		for (int z = 0; z < 3; z++) {
			for (int x = 0; x < 3; x++) {
				Chunk chunk = this.mw.mc.world.getChunkFromChunkCoords(
					chunkArrayX + x,
					chunkArrayZ + z
				);
				if (!chunk.isEmpty()) {
					chunkArray[(z * 3) + x] = copyToMwChunk(chunk);
				}
			}
		}
	}
	
	public void updateSurfaceChunks() {
		int chunksToUpdate = Math.min(this.chunkMap.size(), this.mw.chunksPerTick);
		MwChunk[] chunkArray = new MwChunk[chunksToUpdate];
		//计算出每个时间步需要处理的chunk数目
		for (int i = 0; i < chunksToUpdate; i++) {
			Map.Entry<Chunk, Integer> entry = this.chunkMap.getNextEntry();
			if (entry != null) {
				// if this chunk is within a certain distance to the player then
				// add it to the viewed set
				Chunk chunk = entry.getKey();

				int flags = entry.getValue();
				if (MwUtil.distToChunkSq(this.mw.playerXInt, this.mw.playerZInt, chunk) <= this.mw.maxChunkSaveDistSq) {
					flags |= (VISIBLE_FLAG | VIEWED_FLAG);
				} else {
					flags &= ~VISIBLE_FLAG;
				}
				//每个chunk打上标志
				entry.setValue(flags);

//				将chunk转为自己格式的MwChunk
				if ((flags & VISIBLE_FLAG) != 0) {
					chunkArray[i] = copyToMwChunk(chunk);
				} else {
					chunkArray[i] = null;
				}
			}
		}
		
		this.mw.executor.addTask(new UpdateSurfaceChunksTask(this.mw, chunkArray));
	}
	
	public void onTick() {
		if (!this.closed) {
			if ((this.mw.tickCounter & 0xf) == 0) {
				this.updateUndergroundChunks();
			} else {
				this.updateSurfaceChunks();
			}
		}
	}
	
	public void forceChunks(MwChunk[] chunkArray){
		this.mw.executor.addTask(new UpdateSurfaceChunksTask(this.mw, chunkArray));
	}

	private void addSaveChunkTask(Chunk chunk) {
		if ((this.mw.multiplayer && this.mw.regionFileOutputEnabledMP) ||
			(!this.mw.multiplayer && this.mw.regionFileOutputEnabledSP)) {
			if (!chunk.isEmpty()) {
				this.mw.executor.addTask(new SaveChunkTask(copyToMwChunk(chunk), this.mw.regionManager));
			}
		}
	}
}