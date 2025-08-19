package org.satellite.dev.progiple.sateschematics.commands.pos;

import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.commands.processor.ZeroArgCommand;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = "pos1")
@Check(permissions = "@.pos", flags = ZeroArgCommand.AccessFlag.PLAYER_ONLY)
public class Pos1SubCommand extends PosSubCommand {
    public Pos1SubCommand() {
        super(true);
    }
}
