package com.soybean;

import com.soybean.block.AmaterasuFireBlock;
import com.soybean.damage.FengdanDamage;
import com.soybean.enchantment.*;
import com.soybean.event.EventInit;
import com.soybean.networks.ClientInit;
import com.soybean.registery.Register;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.MapColor;
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

//指令 /give @s minecraft:enchanted_book{StoredEnchantments:[{id:"enchantseries:fire",lvl:2s}]}
//give @p minecraft:diamond_boots{Enchantments:[{id:"frost_walker",lvl:8}]}
///data get entity @s Inventory[3]
public class EnchantseriesClient implements ClientModInitializer {
	public static final String MID = "enchantseries";
	public static final Enchantment FIRE_SHOE = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "fire"), new FireShoe());
	public static final Enchantment TO_FRIEND_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "to_friend"), new ToFriendEnchantment());
	public static final Enchantment EXPLOSIVE_COIN = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "explosive_coin"), new ExplosiveCoin());
	public static final Enchantment RAMBLE_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "ramble"), new RambleEnchantment());
	public static final Enchantment AIR_SWIMMING_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "air_swim"), new AirSwimmingEnchantment());
	public static final Enchantment STATIC_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "static"), new StaticEnchantment());
	public static final Enchantment REBEL_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "rebel"), new RebelEnchantment());
	public static final Enchantment REVIVE_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "revive"), new ReviveEnchantment());
	public static final Enchantment GLAMOROUS_DEBUT_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "glamorous_debut"), new GlamorousDebutEnchantment());
	public static final Enchantment RAGE_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "rage"), new RageEnchantment());
	public static final Enchantment WATER_WALKER_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "water_walker"), new WaterWalker());
	public static final Enchantment FIRE_PATH_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "fire_path"), new FirePathEnchantment());
	public static final Enchantment AMATERASU_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "amaterasu"), new AmaterasuEnchantment());
	public static final Enchantment SPIDER_POWER_ENCHANTMENT =  Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "spider_power"), new SpiderPowerEnchantment());
	public static final Enchantment DRAW_CIRCLE_ENCHANTMENT =  Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "draw_circle"), new DrawCircleEnchantment());
	public static final Enchantment CHANNELING_REVISE_ENCHANTMENT =  Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "channeling_revise"), new ChannelingReviseEnchantment());
	public static final Enchantment FENG_DAN_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "feng_dan"), new FengdanEnchantment());
	public static final Enchantment FIRE_RECOVER_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "fire_recover"), new FireRecoverEnchantment());
	public static final Enchantment FLOWER_WALKER_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "flower_walker"), new FlowerWalkerEnchantment());
	public static final Enchantment GROWTH_SEEDLINGS_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "growth_seedlings"), new GrowthOfSeedlingsEnchantment());
	public static final Enchantment DOUBLE_JUMP_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "double_jump"), new DoubleJumpEnchantment());
	public static final Enchantment SLIME_FEET_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "slime_feet"), new SlimeFeetEnchantment());
	public static final Enchantment EXPLOSIVE_WALKER_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "explosive_walker"), new ExplosiveWalkerEnchantment());
	public static final Enchantment CONTINUOUS_FIRE_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "continuous_fire"), new ContinuousFiringEnchantment());
	public static final Enchantment DESTRUCT_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "destruct"), new DestructEnchantment());
	public static final Enchantment STRUCT_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "struct"), new StructEnchantment());
	public static final Enchantment DISCOUNT_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "discount"), new DiscountEnchantment());
	public static final Enchantment FAST_STOCK_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "fast_stock"), new FastStockEnchantment());
	public static final Enchantment SNIPER_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "sniper"), new SniperEnchantment());
	public static final Enchantment EXPLOSIVE_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "explosive"), new ExplosiveEnchantment());
	public static final Enchantment FLAME_FROST_WALKER_ENCHANTMENT = Registry.register(Registries.ENCHANTMENT, new Identifier(MID, "flame_frost_walker"), new FlameFrostWalkerEnchantment());
	public static final ItemStack FIRE_SHOE_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(FIRE_SHOE, 1));
	public static final ItemStack TO_FRIEND_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(TO_FRIEND_ENCHANTMENT, 1));
	public static final ItemStack EXPLOSIVE_COIN_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(EXPLOSIVE_COIN, 1));
	public static final ItemStack RAMBLE_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(RAMBLE_ENCHANTMENT, 1));
	public static final ItemStack AIR_SWIMMING_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(AIR_SWIMMING_ENCHANTMENT, 1));
	public static final ItemStack STATIC_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(STATIC_ENCHANTMENT, 1));
	public static final ItemStack REBEL_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(REBEL_ENCHANTMENT, 1));
	public static final ItemStack REVIVE_ENCHANTMENT_BOOK =  EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(REVIVE_ENCHANTMENT, 1));
	public static final ItemStack GLAMOROUS_DEBUT_ENCHANTMENT_BOOK =  EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(GLAMOROUS_DEBUT_ENCHANTMENT, 1));
	public static final ItemStack RAGE_ENCHANTMENT_BOOK =  EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(RAGE_ENCHANTMENT, 1));
	public static final ItemStack WATER_WALKER_ENCHANTMENT_BOOK =  EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(WATER_WALKER_ENCHANTMENT, 1));
	public static final ItemStack RAMBLE_ENCHANTMENT_8BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(RAMBLE_ENCHANTMENT, 8));     //漫步plus
	public static final ItemStack FIRE_PATH_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(FIRE_PATH_ENCHANTMENT, 1));
	public static final ItemStack AMATERASU_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(AMATERASU_ENCHANTMENT, 1));
	public static final ItemStack SPIDER_POWER_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(SPIDER_POWER_ENCHANTMENT, 1));
	public static final ItemStack DRAW_CIRCLE_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(DRAW_CIRCLE_ENCHANTMENT, 1));
	public static final ItemStack CHANNELING_REVISE_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(CHANNELING_REVISE_ENCHANTMENT, 1));
	public static final ItemStack FENG_DAN_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(FENG_DAN_ENCHANTMENT, 1));
	public static final ItemStack FIRE_RECOVER_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(FIRE_RECOVER_ENCHANTMENT, 1));
	public static final ItemStack FLOWER_WALKER_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(FLOWER_WALKER_ENCHANTMENT, 1));
	public static final ItemStack GROWTH_SEEDLINGS_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(GROWTH_SEEDLINGS_ENCHANTMENT, 1));
	public static final ItemStack DOUBLE_JUMP_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(DOUBLE_JUMP_ENCHANTMENT, 1));
	public static final ItemStack SLIME_FEET_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(SLIME_FEET_ENCHANTMENT, 1));
	public static final ItemStack EXPLOSIVE_WALKER_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(EXPLOSIVE_WALKER_ENCHANTMENT, 1));
	public static final ItemStack CONTINUOUS_FIRE_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(CONTINUOUS_FIRE_ENCHANTMENT, 1));
	public static final ItemStack DESTRUCT_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(DESTRUCT_ENCHANTMENT, 1));
	public static final ItemStack STRUCT_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(STRUCT_ENCHANTMENT, 1));
	public static final ItemStack DISCOUNT_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(DISCOUNT_ENCHANTMENT, 1));
	public static final ItemStack FAST_STOCK_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(FAST_STOCK_ENCHANTMENT, 1));
	public static final ItemStack SNIPER_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(SNIPER_ENCHANTMENT, 1));
	public static final ItemStack EXPLOSIVE_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(EXPLOSIVE_ENCHANTMENT, 1));
	public static final ItemStack FLAME_FROST_WALKER_ENCHANTMENT_BOOK = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(FLAME_FROST_WALKER_ENCHANTMENT, 1));
	public static final AmaterasuFireBlock fire = Registry.register(Registries.BLOCK, new Identifier(MID, "amaterasu"), new AmaterasuFireBlock(AbstractBlock.Settings.create().replaceable().mapColor(MapColor.BLACK).noCollision().breakInstantly().luminance((state) -> {
		return 10;
	}).sounds(BlockSoundGroup.WOOL).pistonBehavior(PistonBehavior.DESTROY)));

	public static final KeyBinding JUMP_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.jump",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_SPACE,
			"key.categories.movement"
	));
	private static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
			.icon(() -> new ItemStack(Items.ENCHANTED_BOOK))
			.displayName(Text.translatable("enchantment.soybean.category"))
			.entries((context, entries) -> {
				entries.add(FIRE_SHOE_BOOK);
				entries.add(TO_FRIEND_ENCHANTMENT_BOOK);
				entries.add(EXPLOSIVE_COIN_BOOK);
				entries.add(RAMBLE_ENCHANTMENT_BOOK);
				entries.add(AIR_SWIMMING_ENCHANTMENT_BOOK);
				entries.add(STATIC_ENCHANTMENT_BOOK);
				entries.add(REBEL_ENCHANTMENT_BOOK);
				entries.add(REVIVE_ENCHANTMENT_BOOK);
				entries.add(GLAMOROUS_DEBUT_ENCHANTMENT_BOOK);
				entries.add(RAGE_ENCHANTMENT_BOOK);
				entries.add(WATER_WALKER_ENCHANTMENT_BOOK);
				entries.add(FIRE_RECOVER_ENCHANTMENT_BOOK);
				entries.add(FLOWER_WALKER_ENCHANTMENT_BOOK);
				entries.add(RAMBLE_ENCHANTMENT_8BOOK);
				entries.add(FIRE_PATH_ENCHANTMENT_BOOK);
				entries.add(AMATERASU_ENCHANTMENT_BOOK);
				entries.add(SPIDER_POWER_ENCHANTMENT_BOOK);
				entries.add(DRAW_CIRCLE_ENCHANTMENT_BOOK);
				entries.add(CHANNELING_REVISE_ENCHANTMENT_BOOK);
				entries.add(FENG_DAN_ENCHANTMENT_BOOK);
				entries.add(GROWTH_SEEDLINGS_ENCHANTMENT_BOOK);
				entries.add(DOUBLE_JUMP_ENCHANTMENT_BOOK);
				entries.add(SLIME_FEET_ENCHANTMENT_BOOK);
				entries.add(EXPLOSIVE_WALKER_ENCHANTMENT_BOOK);
				entries.add(CONTINUOUS_FIRE_ENCHANTMENT_BOOK);
				entries.add(DESTRUCT_ENCHANTMENT_BOOK);
				entries.add(STRUCT_ENCHANTMENT_BOOK);
				entries.add(DISCOUNT_ENCHANTMENT_BOOK);
				entries.add(FAST_STOCK_ENCHANTMENT_BOOK);
				entries.add(SNIPER_ENCHANTMENT_BOOK);
				entries.add(EXPLOSIVE_ENCHANTMENT_BOOK);
				entries.add(FLAME_FROST_WALKER_ENCHANTMENT_BOOK);
			})
			.build();

	@Override
	public void onInitializeClient() {
		Registry.register(Registries.ITEM_GROUP, new Identifier(MID, "group"), ITEM_GROUP);
		EventInit.start();
		FengdanDamage.registerDamageType();
		BlockRenderLayerMap.INSTANCE.putBlock(fire,RenderLayer.getCutout());
//		ClientInit.init();
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}