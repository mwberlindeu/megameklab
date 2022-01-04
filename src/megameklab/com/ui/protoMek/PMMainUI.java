/*
 * MegaMekLab - Copyright (C) 2018 - The MegaMek Team
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
package megameklab.com.ui.protoMek;

import java.awt.BorderLayout;

import javax.swing.*;

import megamek.common.Engine;
import megamek.common.Entity;
import megamek.common.EntityMovementMode;
import megamek.common.EquipmentType;
import megamek.common.ITechManager;
import megamek.common.Protomech;
import megamek.common.SimpleTechLevel;
import megamek.common.TechConstants;
import megamek.common.verifier.TestProtomech;
import megameklab.com.ui.MegaMekLabMainUI;
import megameklab.com.ui.generalUnit.EquipmentTab;
import megameklab.com.ui.generalUnit.FluffTab;
import megameklab.com.ui.generalUnit.PreviewTab;
import megameklab.com.ui.util.TabScrollPane;

/**
 * Main UI for building protomechs
 * 
 * @author Neoancient
 *
 */
public class PMMainUI extends MegaMekLabMainUI {

    private static final long serialVersionUID = 8103672350822665207L;

    private final JTabbedPane configPane = new JTabbedPane(SwingConstants.TOP);

    private PMStructureTab structureTab;
    private EquipmentTab equipmentTab;
    private PreviewTab previewTab;
    private PMBuildTab buildTab;
    private PMStatusBar statusbar;

    public PMMainUI() {
        super();
        createNewUnit(Entity.ETYPE_PROTOMECH);
        setTitle(getEntity().getChassis() + " " + getEntity().getModel() + ".blk");
        finishSetup();
    }

    @Override
    public void reloadTabs() {
        configPane.removeAll();
        getContentPane().removeAll();

        structureTab = new PMStructureTab(this);
        previewTab = new PreviewTab(this);
        statusbar = new PMStatusBar(this);
        equipmentTab = new EquipmentTab(this);
        buildTab = new PMBuildTab(this, equipmentTab, this);
        FluffTab fluffTab = new FluffTab(this);
        structureTab.addRefreshedListener(this);
        equipmentTab.addRefreshedListener(this);
        statusbar.addRefreshedListener(this);
        fluffTab.setRefreshedListener(this);

        configPane.addTab("Structure/Armor", new TabScrollPane(structureTab));
        configPane.addTab("Equipment", equipmentTab);
        configPane.addTab("Assign Criticals", new TabScrollPane(buildTab));
        configPane.addTab("Fluff", new TabScrollPane(fluffTab));
        configPane.addTab("Preview", previewTab);

        add(configPane, BorderLayout.CENTER);
        add(statusbar, BorderLayout.SOUTH);

        refreshHeader();
        validate();
    }

    @Override
    public void createNewUnit(long entitytype, boolean isPrimitive, boolean isIndustrial, Entity oldEntity) {

        Protomech proto = new Protomech();
        setEntity(proto);

        getEntity().setWeight(2);
        proto.setMovementMode(EntityMovementMode.BIPED);
        proto.setTechLevel(TechConstants.T_CLAN_TW);
        proto.setOriginalWalkMP(1);
        proto.setEngine(new Engine(TestProtomech.calcEngineRating(proto), Engine.NORMAL_ENGINE, Engine.CLAN_ENGINE));
        proto.setArmorType(EquipmentType.T_ARMOR_STANDARD);
        proto.setArmorTechLevel(getEntity().getTechLevel());

        proto.autoSetInternal();
        proto.setHasMainGun(false);
        for (int loc = 0; loc < proto.locations(); loc++) {
            proto.initializeArmor(0, loc);
        }

        if (null == oldEntity) {
            proto.setChassis("New");
            proto.setModel("Protomek");
            proto.setYear(3145);
        } else {
            proto.setChassis(oldEntity.getChassis());
            proto.setModel(oldEntity.getModel());
            proto.setYear(Math.max(oldEntity.getYear(), proto.getConstructionTechAdvancement().getIntroductionDate()));
            proto.setSource(oldEntity.getSource());
            proto.setManualBV(oldEntity.getManualBV());
            SimpleTechLevel lvl = SimpleTechLevel.max(proto.getStaticTechLevel(),
                    SimpleTechLevel.convertCompoundToSimple(oldEntity.getTechLevel()));
            proto.setTechLevel(lvl.getCompoundTechLevel(oldEntity.isClan()));
            proto.setMixedTech(oldEntity.isMixedTech());
        }

    }

    @Override
    public void refreshAll() {
        statusbar.refresh();
        structureTab.refresh();
        equipmentTab.refresh();
        buildTab.refresh();
        previewTab.refresh();
    }

    @Override
    public void refreshArmor() { }

    @Override
    public void refreshBuild() {
        buildTab.refresh();
    }

    @Override
    public void refreshEquipment() {
        equipmentTab.refresh();
    }

    @Override
    public void refreshTransport() {
        // not used for protomechs
    }

    @Override
    public void refreshPreview() {
        previewTab.refresh();
    }

    @Override
    public void refreshHeader() {
        String title = getEntity().getChassis() + " " + getEntity().getModel() + ".blk";
        setTitle(title);
    }

    @Override
    public void refreshStatus() {
        statusbar.refresh();
    }

    @Override
    public void refreshStructure() {
        structureTab.refresh();
    }

    @Override
    public void refreshWeapons() {
    }
    
    @Override
    public void refreshSummary() {
        structureTab.refreshSummary();
    }
    
    @Override
    public void refreshEquipmentTable() {
        equipmentTab.refreshTable();
    }

    @Override
    public ITechManager getTechManager() {
        return structureTab.getTechManager();
    }

}
