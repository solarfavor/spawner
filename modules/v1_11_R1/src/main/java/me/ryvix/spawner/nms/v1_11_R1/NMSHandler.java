package me.ryvix.spawner.nms.v1_11_R1;

import me.ryvix.spawner.Main;
import me.ryvix.spawner.Spawner;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.NBTTagList;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import me.ryvix.spawner.api.NMS;

public class NMSHandler implements NMS {

	/**
	 * Get spawner type using NBT
	 *
	 * @return Entity Name string
	 */
	@Override
	public String getEntityNameFromSpawnerNBT(Spawner spawner) {
		if (!(spawner.getType() == Material.MOB_SPAWNER)) {
			return null;
		}

		net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(spawner);

		if (nmsStack.hasTag()) {
			NBTTagCompound tag = nmsStack.getTag();

			if (tag != null) {

				if (tag.hasKey("BlockEntityTag")) {
					NBTTagCompound blockEntityTag = tag.getCompound("BlockEntityTag");

					if (blockEntityTag.hasKey("SpawnPotentials")) {
						NBTTagList spawnPotentials = blockEntityTag.getList("SpawnPotentials", 10);
						if (!spawnPotentials.isEmpty()) {
							// TODO: show all entity types in mob spawner name
							NBTTagCompound entity = spawnPotentials.get(0).getCompound("Entity");
							return entity.getString("id");
						}
					}

				} else if (tag.hasKey("SpawnData")) {
					NBTTagCompound spawnData = tag.getCompound("SpawnData");
					if (spawnData.hasKey("id")) {
						return spawnData.getString("id");
					}

				}
			}
		}

		return null;
	}

	/**
	 * Set spawner type using NBT
	 *
	 * @param spawner
	 * @return
	 */
	@Override
	public ItemStack setSpawnerNBT(Spawner spawner) {
		if (!(spawner.getType() == Material.MOB_SPAWNER)) {
			return null;
		}

		String cleanEntity = Main.instance.getLangHandler().translateEntity(spawner.getEntityName(), "key");

		net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(spawner);

		NBTTagCompound tag;
		if (nmsStack.hasTag()) {
			tag = nmsStack.getTag();
		} else {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}

		if (!tag.hasKey("SpawnData")) {
			tag.set("SpawnData", new NBTTagCompound());
		}

		NBTTagCompound spawnData = tag.getCompound("SpawnData");
		spawnData.setString("id", cleanEntity);

		if (!tag.hasKey("BlockEntityTag")) {
			tag.set("BlockEntityTag", new NBTTagCompound());
		}

		NBTTagCompound blockEntityTag = tag.getCompound("BlockEntityTag");

		if (!blockEntityTag.hasKey("SpawnPotentials")) {
			blockEntityTag.set("SpawnPotentials", new NBTTagCompound());
		}

		NBTTagCompound spawnPotentials = new NBTTagCompound();

		spawnPotentials.setInt("Weight", 1);
		spawnPotentials.set("Entity", new NBTTagCompound());

		NBTTagCompound spawnPotentialsEntity = spawnPotentials.getCompound("Entity");
		spawnPotentialsEntity.setString("id", cleanEntity);

		NBTTagList tags = new NBTTagList();

		tags.add(spawnPotentials);

		blockEntityTag.set("SpawnPotentials", tags);

		return CraftItemStack.asCraftMirror(nmsStack);
	}

	/**
	 * Get entity name from spawn egg using NBT
	 *
	 * @param is
	 * @return
	 * @url https://www.spigotmc.org/threads/tutorial-mob-eggs-in-1-9.131474/
	 */
	@Override
	public String getEntityNameFromSpawnEgg(ItemStack is) {
		if (!(is.getType() == Material.MONSTER_EGG)) {
			return null;
		}

		net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(is);

		if (nmsStack.hasTag()) {
			NBTTagCompound tag = nmsStack.getTag();
			NBTTagCompound entityTag = tag.getCompound("EntityTag");
			if (entityTag.hasKey("id")) {
				return entityTag.getString("id");
			}
		}

		return null;
	}

}