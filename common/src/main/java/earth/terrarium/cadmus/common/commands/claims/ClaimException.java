package earth.terrarium.cadmus.common.commands.claims;

import earth.terrarium.cadmus.common.util.ModUtils;
import net.minecraft.network.chat.Component;

public class ClaimException extends Exception {

    public static final ClaimException CHUNK_ALREADY_CLAIMED = new ClaimException(ModUtils.serverTranslation("command.cadmus.exception.chunk_already_claimed"));
    public static final ClaimException ALREADY_CLAIMED_CHUNK = new ClaimException(ModUtils.serverTranslation("command.cadmus.exception.already_claimed_chunk"));
    public static final ClaimException MAXED_OUT_CLAIMS = new ClaimException(ModUtils.serverTranslation("command.cadmus.exception.maxed_out_claims"));
    public static final ClaimException CHUNK_NOT_CLAIMED = new ClaimException(ModUtils.serverTranslation("command.cadmus.exception.chunk_not_claimed"));
    public static final ClaimException DONT_OWN_CHUNK = new ClaimException(ModUtils.serverTranslation("command.cadmus.exception.dont_own_chunk"));
    public static final ClaimException CLAIM_HAS_NO_FLAGS = new ClaimException(ModUtils.serverTranslation("command.cadmus.exception.claim_has_no_flags"));
    public static final ClaimException CLAIM_ALREADY_EXISTS = new ClaimException(ModUtils.serverTranslation("command.cadmus.exception.claim_already_exists"));
    public static final ClaimException CLAIM_DOES_NOT_EXIST = new ClaimException(ModUtils.serverTranslation("command.cadmus.exception.claim_does_not_exist"));

    private final Component message;

    private ClaimException(Component message) {
        super(message.getString());
        this.message = message;
    }

    public Component message() {
        return message;
    }
}
