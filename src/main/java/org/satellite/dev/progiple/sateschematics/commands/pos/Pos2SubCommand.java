package org.satellite.dev.progiple.sateschematics.commands.pos;

import org.novasparkle.lunaspring.API.commands.LunaSpringSubCommand;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = "pos2")
@Check(permissions = "sateschematics.pos", flags = LunaSpringSubCommand.AccessFlag.PLAYER_ONLY)
public class Pos2SubCommand extends PosSubCommand {
    public Pos2SubCommand() {
        super(false);
    }
}
