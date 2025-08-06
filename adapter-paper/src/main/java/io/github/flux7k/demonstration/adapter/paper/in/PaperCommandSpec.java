package io.github.flux7k.demonstration.adapter.paper.in;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

public interface PaperCommandSpec {

    LiteralCommandNode<CommandSourceStack> getNode();

    String getDescription();

    List<String> getAliases();

}