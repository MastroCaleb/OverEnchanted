package caleb.overenchanted.mixin;

import java.util.Iterator;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;

@SuppressWarnings("all")
@Mixin(priority = 500, value = AnvilScreenHandler.class)
public abstract class UnlimitedLevelsMixin {

	private int overenchanted_preValue = 0;

	@Inject(method = "updateResult",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I",
			ordinal = 1
		),
		locals = LocalCapture.CAPTURE_FAILHARD)
	public void getOverLimitEnchantmentLevel(CallbackInfo ci, ItemStack stack1, int i, int j, int k, ItemStack stack2, ItemStack stack3, 
			Map map1, boolean bl1, Map map2, boolean bl2, boolean bl3, Iterator it1, Enchantment ench, int r) {
		/* WARNING: This is probably not the best solution, but it's the only one my tiny brain found.
		 * 
		 * This is run after the check "if(r > enchantment.getMaxLevel())", then we store the level
		 * value before they set it to "r = enchantment.getMaxLevel()" as we don't need that.
		 */
		overenchanted_preValue = r;
		if(ench.getMaxLevel() == 1){
			//If the max level is one, it probably doesn't support multiple levels so we just don't care about over enchanting it.
			overenchanted_preValue = 0;
		}
	}

	@ModifyVariable(method = "updateResult", at = @At("STORE"), ordinal = 3)
	public int setOverLimitEnchantmentLevel(int r) {
		/* WARNING: This is probably not the best solution, but it's the only one my tiny brain found.
		 * 
		 * This is run after they set the variable r to "r = enchantment.getMaxLevel()" essentially
		 * nullifying what they did.
		 */
		if(overenchanted_preValue != 0){ //We check if preValue isn't 0, which happens only if it's not set to begin with. (Skipping this results to r being set to 0)
			int temp = overenchanted_preValue; //We store preValue in a temp value (so we can reset preValue)
			overenchanted_preValue = 0; //We reset preValue so it can remake the process of over enchanting
			return temp; //We set r to temp
		}
		return r; //If preValue is 0 we are better off leaving it default value, we don't want a level 0 enchantment lol
	}
}
