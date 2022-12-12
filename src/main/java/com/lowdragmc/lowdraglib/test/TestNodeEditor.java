package com.lowdragmc.lowdraglib.test;

import com.lowdragmc.lowdraglib.LDLMod;
import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.nodeWidget.NodeEditPanelWidget;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class TestNodeEditor extends UIFactory<TestNodeEditor> implements IUIHolder {

	public static final TestNodeEditor INSTANCE = new TestNodeEditor();

	private TestNodeEditor(){

	}

	@Override
	protected ModularUI createUITemplate(TestNodeEditor holder, Player entityPlayer) {
		var ui = new ModularUI(450,250,this,entityPlayer);
		ui.widget(new NodeEditPanelWidget(0,0,450,250));
		return ui;
	}

	@Override
	protected TestNodeEditor readHolderFromSyncData(FriendlyByteBuf syncData) {
		return null;
	}

	@Override
	protected void writeHolderToSyncData(FriendlyByteBuf syncData, TestNodeEditor holder) {

	}


	@Override
	public ModularUI createUI(Player entityPlayer) {
		return null;
	}

	@Override
	public boolean isInvalid() {
		return false;
	}

	@Override
	public boolean isRemote() {
		return LDLMod.isRemote();
	}

	@Override
	public void markAsDirty() {

	}
}
