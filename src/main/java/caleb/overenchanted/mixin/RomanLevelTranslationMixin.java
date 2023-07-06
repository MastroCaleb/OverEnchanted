package caleb.overenchanted.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(Enchantment.class)
public class RomanLevelTranslationMixin {

    Enchantment enchantment = (Enchantment)(Object)this;

    private static String intToRoman(int num){
        int[] values = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String[] romanLetters = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
        StringBuilder roman = new StringBuilder();  
        for(int i=0;i<values.length;i++){  
            while(num >= values[i]){  
                num = num - values[i];
                roman.append(romanLetters[i]);
            }
        }
        return roman.toString();
    }
    
    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    public void levelToRoman(int level, CallbackInfoReturnable<Text> r) {
        MutableText mutableText = Text.translatable(enchantment.getTranslationKey());
        if (enchantment.isCursed()) {
            mutableText.formatted(Formatting.RED);
        }
        else {
            mutableText.formatted(Formatting.GRAY);
        }

        if(level != 1){
            mutableText.append(" ").append(Text.of(intToRoman(level)));
        }

        r.setReturnValue(mutableText);
    }
}
