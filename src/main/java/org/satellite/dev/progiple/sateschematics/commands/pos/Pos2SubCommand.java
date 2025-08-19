package org.satellite.dev.progiple.sateschematics.commands.pos;

import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.commands.processor.ZeroArgCommand;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = "pos2")
@Check(permissions = "@.pos", flags = ZeroArgCommand.AccessFlag.PLAYER_ONLY)
public class Pos2SubCommand extends PosSubCommand {
    public Pos2SubCommand() {
        super(false);
    }
}
