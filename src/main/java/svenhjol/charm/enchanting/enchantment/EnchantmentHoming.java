package svenhjol.charm.enchanting.enchantment;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import svenhjol.charm.Charm;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.enchanting.feature.Homing;
import svenhjol.meson.MesonEnchantment;
import svenhjol.meson.helper.EnchantmentHelper;
import svenhjol.meson.helper.SoundHelper;
import svenhjol.meson.helper.WorldHelper;

public class EnchantmentHoming extends MesonEnchantment
{
    public EnchantmentHoming()
    {
        super("homing", Rarity.RARE, EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.MAINHAND);
    }

    @Override
    public String getModId()
    {
        return Charm.MOD_ID;
    }

    @Override
    public boolean isTreasureEnchantment()
    {
        return true;
    }

    @Override
    public boolean canApply(ItemStack stack)
    {
        return (stack.getItem() == Items.DIAMOND_HOE
                || stack.getItem() == Items.GOLDEN_HOE
                || stack.getItem() == Items.IRON_HOE
                || stack.getItem() == Items.BOOK
        );
    }

    @Override
    public int getMaxLevel()
    {
        return 1;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel)
    {
        return Homing.minEnchantability;
    }

    public void onBlockInteract(PlayerInteractEvent.RightClickBlock event)
    {
        ItemStack held = event.getEntityPlayer().getHeldItem(event.getHand());

        if (held.getItem() instanceof ItemHoe
            && EnchantmentHelper.hasEnchantment(this, held)
        ) {
            World world = event.getWorld();
            EntityPlayer player = event.getEntityPlayer();

            // do a check based on the material the item is made of
            Block checkFor = null;
            if (held.getItem() == Items.IRON_HOE) {
                checkFor = Blocks.IRON_ORE;
            } else if (held.getItem() == Items.GOLDEN_HOE) {
                checkFor = Blocks.GOLD_ORE;
            } else if (held.getItem() == Items.DIAMOND_HOE) {
                checkFor = Blocks.DIAMOND_ORE;
            }

            if (checkFor != null) {
                int range = Homing.range;
                double distance = 0;
                BlockPos eventPos = event.getPos();

                Iterable<BlockPos> positions = BlockPos.getAllInBox(eventPos.add(-range, -range, -range), eventPos.add(range, range, range));
                for (BlockPos pos : positions) {
                    if (world.getBlockState(pos).getBlock() == checkFor) {
                        double d = WorldHelper.getDistanceSq(eventPos, pos);
                        if (distance == 0 || d < distance) distance = d;
                    }
                }

                if (distance != 0) {
                    held.damageItem(held.getMaxDamage() / Homing.damage, player);

                    if (world.isRemote) { // client
                        float vol = 1 - (Math.min((float) distance, 100) / 100);
                        float pitch = 1.0f - (Math.min((float) distance, 100) / 100);
                        player.swingArm(event.getHand());
                        SoundHelper.playerSound(player, CharmSounds.HOMING, vol, pitch, SoundCategory.AMBIENT);
                    }
                }
            }
        }
    }
}
