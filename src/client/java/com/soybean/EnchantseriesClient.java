package com.soybean;

import com.soybean.block.AmaterasuFireBlock;
import com.soybean.damage.FengdanDamage;
import com.soybean.enchantment.*;
import com.soybean.event.EventInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.soybean.Enchantseries.fire;

//指令 /give @s minecraft:enchanted_book{StoredEnchantments:[{id:"enchantseries:fire",lvl:2s}]}
//give @p minecraft:diamond_boots{Enchantments:[{id:"frost_walker",lvl:8}]}
///data get entity @s Inventory[3]
public class EnchantseriesClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(fire,RenderLayer.getCutout());
//		ClientInit.init();
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}