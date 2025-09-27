package org.satellite.dev.progiple.sateschematics.commands.pos;

import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.commands.processor.NoArgCommand;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = "pos1")
@Check(permissions = "@.pos", flags = NoArgCommand.AccessFlag.PLAYER_ONLY)
public class Pos1SubCommand extends PosSubCommand {
    public Pos1SubCommand() {
        super(true);
    }
}
