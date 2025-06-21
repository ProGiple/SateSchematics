package org.satellite.dev.progiple.sateschematics.commands.pos;

import org.novasparkle.lunaspring.API.commands.LunaSpringSubCommand;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = "pos1")
@Check(permissions = "sateschematics.pos", flags = LunaSpringSubCommand.AccessFlag.PLAYER_ONLY)
public class Pos1SubCommand extends PosSubCommand {
    public Pos1SubCommand() {
        super(true);
    }
}
