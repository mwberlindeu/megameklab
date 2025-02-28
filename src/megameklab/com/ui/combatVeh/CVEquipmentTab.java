/*
 * MegaMekLab - Copyright (C) 2008
 *
 * Original author - jtighe (torren@users.sourceforge.net)
 *
 * This program is  free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package megameklab.com.ui.combatVeh;

import megamek.common.*;
import megamek.common.verifier.TestTank;
import megamek.common.weapons.artillery.ArtilleryWeapon;
import megameklab.com.ui.EntitySource;
import megameklab.com.ui.util.*;
import megameklab.com.util.UnitUtil;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CVEquipmentTab extends ITab implements ActionListener {
    private static final long serialVersionUID = 3978675469713289404L;

    private static final int T_ENERGY    =  0;
    private static final int T_BALLISTIC =  1;
    private static final int T_MISSILE   =  2;
    private static final int T_ARTILLERY =  3;
    private static final int T_WEAPON    =  4;
    private static final int T_AMMO      =  5;
    private static final int T_OTHER     =  6;
    private static final int T_NUM       =  7;


    private RefreshListener refresh;

    private JButton addButton = new JButton("Add");
    private JButton removeButton = new JButton("Remove");
    private JButton removeAllButton = new JButton("Remove All");
    private JComboBox<String> choiceType = new JComboBox<String>();
    private JTextField txtFilter = new JTextField();

    private JRadioButton rbtnStats = new JRadioButton("Stats");
    private JRadioButton rbtnFluff = new JRadioButton("Fluff");
    final private JCheckBox chkShowAll = new JCheckBox("Show Unavailable");

    private TableRowSorter<EquipmentTableModel> equipmentSorter;

    private CriticalTableModel equipmentList;
    private EquipmentTableModel masterEquipmentList;
    private JTable masterEquipmentTable = new JTable();
    private JScrollPane masterEquipmentScroll = new JScrollPane();
    private JTable equipmentTable = new JTable();
    private JScrollPane equipmentScroll = new JScrollPane();

    private String ADD_COMMAND = "ADD";
    private String REMOVE_COMMAND = "REMOVE";
    private String REMOVEALL_COMMAND = "REMOVEALL";

    public static String getTypeName(int type) {
        switch (type) {
            case T_WEAPON:
                return "All Weapons";
            case T_ENERGY:
                return "Energy Weapons";
            case T_BALLISTIC:
                return "Ballistic Weapons";
            case T_MISSILE:
                return "Missile Weapons";
            case T_ARTILLERY:
                return "Artillery Weapons";
            case T_AMMO:
                return "Ammunition";
            case T_OTHER:
                return "Other Equipment";
            default:
                return "?";
        }
    }

    public CVEquipmentTab(EntitySource eSource) {
        super(eSource);

        equipmentList = new CriticalTableModel(getTank(), CriticalTableModel.WEAPONTABLE);
        equipmentTable.setModel(equipmentList);
        equipmentTable.setIntercellSpacing(new Dimension(0, 0));
        equipmentTable.setShowGrid(false);
        equipmentTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        equipmentTable.setDoubleBuffered(true);
        TableColumn column = null;
        for (int i = 0; i < equipmentList.getColumnCount(); i++) {
            column = equipmentTable.getColumnModel().getColumn(i);
            if (i == CriticalTableModel.NAME) {
                column.setPreferredWidth(200);
            } else if (i == CriticalTableModel.SIZE) {
                column.setCellEditor(equipmentList.new SpinnerCellEditor());
            }
            column.setCellRenderer(equipmentList.getRenderer());

        }
        equipmentList.addTableModelListener(ev -> {
            if (refresh != null) {
                refresh.refreshStatus();
                refresh.refreshPreview();
                refresh.refreshBuild();
                refresh.refreshSummary();
            }
        });
        equipmentScroll.setViewportView(equipmentTable);

        masterEquipmentList = new EquipmentTableModel(getTank(), eSource.getTechManager());
        masterEquipmentTable.setModel(masterEquipmentList);
        masterEquipmentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        equipmentSorter = new TableRowSorter<>(masterEquipmentList);
        for (int col = 0; col < EquipmentTableModel.N_COL; col++) {
            equipmentSorter.setComparator(col, masterEquipmentList.getSorter(col));
        }
        masterEquipmentTable.setRowSorter(equipmentSorter);
        ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(EquipmentTableModel.COL_NAME, SortOrder.ASCENDING));
        equipmentSorter.setSortKeys(sortKeys);
        XTableColumnModel equipColumnModel = new XTableColumnModel();
        masterEquipmentTable.setColumnModel(equipColumnModel);
        masterEquipmentTable.createDefaultColumnsFromModel();
        column = null;
        for (int i = 0; i < EquipmentTableModel.N_COL; i++) {
            column = masterEquipmentTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(masterEquipmentList.getColumnWidth(i));
            column.setCellRenderer(masterEquipmentList.getRenderer());
        }
        masterEquipmentTable.setIntercellSpacing(new Dimension(0, 0));
        masterEquipmentTable.setShowGrid(false);
        masterEquipmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        masterEquipmentTable.getSelectionModel().addListSelectionListener(selectionListener);
        masterEquipmentTable.setDoubleBuffered(true);
        masterEquipmentScroll.setViewportView(masterEquipmentTable);

        masterEquipmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable)e.getSource();
                    int view = target.getSelectedRow();
                    int selected = masterEquipmentTable.convertRowIndexToModel(view);
                    EquipmentType equip = masterEquipmentList.getType(selected);
                    addEquipment(equip);
                    fireTableRefresh();
                }
            }
        });

        masterEquipmentTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "add");
        masterEquipmentTable.getActionMap().put("add", new EnterAction());

        Enumeration<EquipmentType> miscTypes = EquipmentType.getAllTypes();
        ArrayList<EquipmentType> allTypes = new ArrayList<EquipmentType>();
        while (miscTypes.hasMoreElements()) {
            EquipmentType eq = miscTypes.nextElement();
            allTypes.add(eq);
        }

        masterEquipmentList.setData(allTypes);

        loadEquipmentTable();

        DefaultComboBoxModel<String> typeModel = new DefaultComboBoxModel<String>();
        for (int i = 0; i < T_NUM; i++) {
            typeModel.addElement(getTypeName(i));
        }
        choiceType.setModel(typeModel);
        choiceType.setSelectedIndex(0);
        choiceType.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterEquipment();
            }
        });

        txtFilter.setText("");
        txtFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                filterEquipment();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterEquipment();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                filterEquipment();
            }
        });

        filterEquipment();
        addButton.setMnemonic('A');
        removeButton.setMnemonic('R');
        removeAllButton.setMnemonic('l');

        ButtonGroup bgroupView = new ButtonGroup();
        bgroupView.add(rbtnStats);
        bgroupView.add(rbtnFluff);

        rbtnStats.setSelected(true);
        rbtnStats.addActionListener(ev -> setEquipmentView());
        rbtnFluff.addActionListener(ev -> setEquipmentView());
        chkShowAll.addActionListener(ev -> filterEquipment());
        JPanel viewPanel = new JPanel(new GridLayout(0,3));
        viewPanel.add(rbtnStats);
        viewPanel.add(rbtnFluff);
        viewPanel.add(chkShowAll);
        setEquipmentView();

        //layout
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();

        JPanel loadoutPanel = new JPanel(new GridBagLayout());
        JPanel databasePanel = new JPanel(new GridBagLayout())
        {
            @Override
            // Allow downsizing the database with the Splitpane for small screen sizes
            public Dimension getMinimumSize() {
                Dimension prefSize = super.getPreferredSize();
                return new Dimension(prefSize.width / 2, prefSize.height);
            }
        };

        loadoutPanel.setBorder(BorderFactory.createTitledBorder("Current Loadout"));
        databasePanel.setBorder(BorderFactory.createTitledBorder("Equipment Database"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        databasePanel.add(addButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        databasePanel.add(choiceType, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        databasePanel.add(txtFilter, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        databasePanel.add(viewPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        loadoutPanel.add(removeButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        loadoutPanel.add(removeAllButton, gbc);

        gbc.insets = new Insets(2,0,0,0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        databasePanel.add(masterEquipmentScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        loadoutPanel.add(equipmentScroll, gbc);

        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, loadoutPanel, databasePanel);
        pane.setOneTouchExpandable(true);
        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);
    }

    public void addRefreshedListener(RefreshListener l) {
        refresh = l;
    }

    private void loadEquipmentTable() {
        for (Mounted mount : getTank().getWeaponList()) {
            equipmentList.addCrit(mount);
        }

        for (Mounted mount : getTank().getAmmo()) {
            equipmentList.addCrit(mount);
        }

        List<EquipmentType> spreadAlreadyAdded = new ArrayList<>();

        for (Mounted mount : getTank().getMisc()) {
            EquipmentType etype = mount.getType();
            if (etype.hasFlag(MiscType.F_JUMP_JET)
                    || UnitUtil.isArmorOrStructure(etype)) {
                continue;
            }

            if (UnitUtil.isFixedLocationSpreadEquipment(etype)
                    && !spreadAlreadyAdded.contains(etype)) {
                equipmentList.addCrit(mount);
                // keep track of spreadable equipment here, so it doesn't
                // show up multiple times in the table
                spreadAlreadyAdded.add(etype);
            } else {
                equipmentList.addCrit(mount);
            }
        }
    }

    private void removeHeatSinks() {
        for (int location = 0; location < equipmentList.getRowCount(); ) {
            Mounted mount = (Mounted) equipmentList.getValueAt(location, CriticalTableModel.EQUIPMENT);
            EquipmentType eq = mount.getType();
            if ((eq instanceof MiscType) && (UnitUtil.isHeatSink(mount))) {
                try {
                    equipmentList.removeCrit(location);
                } catch (IndexOutOfBoundsException ignored) {
                    return;
                } catch (Exception e) {
                    LogManager.getLogger().error("", e);
                    return;
                }
            } else {
                location++;
            }
        }
    }

    public void refresh() {
        removeAllListeners();
        filterEquipment();
        updateEquipment();
        addAllListeners();
        fireTableRefresh();
    }

    private void removeAllListeners() {
        addButton.removeActionListener(this);
        removeButton.removeActionListener(this);
        removeAllButton.removeActionListener(this);
    }

    private void addAllListeners() {
        addButton.addActionListener(this);
        removeButton.addActionListener(this);
        removeAllButton.addActionListener(this);
        addButton.setActionCommand(ADD_COMMAND);
        removeButton.setActionCommand(REMOVE_COMMAND);
        removeAllButton.setActionCommand(REMOVEALL_COMMAND);
        addButton.setMnemonic('A');
        removeButton.setMnemonic('R');
        removeAllButton.setMnemonic('L');
    }

    private void addEquipment(EquipmentType equip) {
        boolean success = false;
        Mounted mount = null;
        boolean isMisc = equip instanceof MiscType;
        if (isMisc && equip.hasFlag(MiscType.F_TARGCOMP)) {
            if (!UnitUtil.hasTargComp(getTank())) {
                mount = UnitUtil.updateTC(getTank(), equip);
                success = mount != null;
            }
        } else {
            try {
                mount = new Mounted(getTank(), equip);
                int loc = Entity.LOC_NONE;
                if (isMisc && equip.hasFlag(MiscType.F_MAST_MOUNT)) {
                    loc = VTOL.LOC_ROTOR;
                } else if (TestTank.isBodyEquipment(equip)) {
                    loc = Tank.LOC_BODY;
                }
                getTank().addEquipment(mount, loc, false);
                if ((equip instanceof WeaponType) && equip.hasFlag(WeaponType.F_ONESHOT)) {
                    UnitUtil.removeOneShotAmmo(eSource.getEntity());
                }
                success = true;
            } catch (LocationFullException lfe) {
                // this can't happen, we add to Entity.LOC_NONE
            }
        }
        if (success) {
            equipmentList.addCrit(mount);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals(ADD_COMMAND)) {
            int view = masterEquipmentTable.getSelectedRow();
            if(view < 0) {
                //selection got filtered away
                return;
            }
            int selected = masterEquipmentTable.convertRowIndexToModel(view);
            EquipmentType equip = masterEquipmentList.getType(selected);
            addEquipment(equip);
        } else if (e.getActionCommand().equals(REMOVE_COMMAND)) {
            int selectedRows[] = equipmentTable.getSelectedRows();
            for (Integer row : selectedRows){
                equipmentList.removeMounted(row);
            }
            equipmentList.removeCrits(selectedRows);
            UnitUtil.compactCriticals(getTank());
        } else if (e.getActionCommand().equals(REMOVEALL_COMMAND)) {
            removeAllEquipment();
        } else {
            return;
        }
        fireTableRefresh();
        refresh.refreshSummary();
        refresh.refreshStatus();
    }

    public void updateEquipment() {
        removeHeatSinks();
        equipmentList.removeAllCrits();
        loadEquipmentTable();
    }

    public void removeAllEquipment() {
        removeHeatSinks();
        for (int count = 0; count < equipmentList.getRowCount(); count++) {
            equipmentList.removeMounted(count);
        }
        equipmentList.removeAllCrits();
    }

    private void fireTableRefresh() {
        equipmentList.updateUnit(getTank());
        equipmentList.refreshModel();
        if (refresh != null) {
            refresh.refreshStatus();
            refresh.refreshBuild();
            refresh.refreshPreview();
            //FIXME: this is causing some kind of infinite loop
            //refresh.refreshStructure();
        }
    }

    public CriticalTableModel getEquipmentList() {
        return equipmentList;
    }

    private void filterEquipment() {
        RowFilter<EquipmentTableModel, Integer> equipmentTypeFilter = null;
        final int nType = choiceType.getSelectedIndex();
        equipmentTypeFilter = new RowFilter<EquipmentTableModel,Integer>() {
            @Override
            public boolean include(Entry<? extends EquipmentTableModel, ? extends Integer> entry) {
                Tank tank = getTank();
                EquipmentTableModel equipModel = entry.getModel();
                EquipmentType etype = equipModel.getType(entry.getIdentifier());
                WeaponType wtype = null;
                if (etype instanceof WeaponType) {
                    wtype = (WeaponType)etype;
                }
                AmmoType atype = null;
                if (etype instanceof AmmoType) {
                    atype = (AmmoType)etype;
                }
                if (UnitUtil.isHeatSink(etype) || UnitUtil.isJumpJet(etype)) {
                    return false;
                }
                if (((nType == T_OTHER) && UnitUtil.isTankMiscEquipment(etype, tank))
                        || (((nType == T_WEAPON) && (UnitUtil.isTankWeapon(etype, tank))))
                        || ((nType == T_ENERGY) && UnitUtil.isTankWeapon(etype, tank)
                            && (wtype != null) && (wtype.hasFlag(WeaponType.F_ENERGY)
                            || (wtype.hasFlag(WeaponType.F_PLASMA) && (wtype.getAmmoType() == AmmoType.T_PLASMA))))
                        || ((nType == T_BALLISTIC) && UnitUtil.isTankWeapon(etype, tank)
                            && (wtype != null) && (wtype.hasFlag(WeaponType.F_BALLISTIC)
                                    && (wtype.getAmmoType() != AmmoType.T_NA)))
                        || ((nType == T_MISSILE) && UnitUtil.isTankWeapon(etype, tank)
                            && (wtype != null) && ((wtype.hasFlag(WeaponType.F_MISSILE)
                                    && (wtype.getAmmoType() != AmmoType.T_NA)) || (wtype.getAmmoType() == AmmoType.T_C3_REMOTE_SENSOR)))
                        || ((nType == T_ARTILLERY) && UnitUtil.isTankWeapon(etype, tank)
                            && (wtype instanceof ArtilleryWeapon))
                        || (((nType == T_AMMO) & (atype != null)) && UnitUtil.canUseAmmo(tank, atype, false))) {
                    if (!eSource.getTechManager().isLegal(etype)
                            && !chkShowAll.isSelected()) {
                        return false;
                    }
                    if (txtFilter.getText().length() > 0) {
                        String text = txtFilter.getText();
                        return etype.getName().toLowerCase().contains(text.toLowerCase());
                    } else {
                        return true;
                    }
                }
                return false;
            }
        };
        equipmentSorter.setRowFilter(equipmentTypeFilter);
    }

    public void setEquipmentView() {
        XTableColumnModel columnModel = (XTableColumnModel)masterEquipmentTable.getColumnModel();
        if(rbtnStats.isSelected()) {
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_NAME), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DAMAGE), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DIVISOR), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_SPECIAL), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_HEAT), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_MRANGE), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_RANGE), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_SHOTS), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_TECH), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_TLEVEL), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_TRATING), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DPROTOTYPE), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DPRODUCTION), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DCOMMON), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DEXTINCT), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DREINTRO), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_COST), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_CREW), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_BV), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_TON), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_CRIT), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_REF), true);
        } else {
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_NAME), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DAMAGE), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DIVISOR), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_SPECIAL), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_HEAT), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_MRANGE), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_RANGE), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_SHOTS), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_TECH), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_TLEVEL), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_TRATING), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DPROTOTYPE), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DPRODUCTION), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DCOMMON), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DEXTINCT), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_DREINTRO), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_COST), true);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_CREW), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_BV), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_TON), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_CRIT), false);
            columnModel.setColumnVisible(columnModel.getColumnByModelIndex(EquipmentTableModel.COL_REF), true);
        }
    }

    private class EnterAction extends AbstractAction {

        /**
         *
         */
        private static final long serialVersionUID = 8247993757008802162L;

        @Override
        public void actionPerformed(ActionEvent e) {
            int view = masterEquipmentTable.getSelectedRow();
            if (view < 0) {
                //selection got filtered away
                return;
            }
            int selected = masterEquipmentTable.convertRowIndexToModel(view);
            EquipmentType equip = masterEquipmentList.getType(selected);
            addEquipment(equip);
            fireTableRefresh();
        }
    }

    private ListSelectionListener selectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int selected = masterEquipmentTable.getSelectedRow();
            EquipmentType etype = null;
            if (selected >= 0) {
                etype = masterEquipmentList.getType(masterEquipmentTable.convertRowIndexToModel(selected));
            }
            addButton.setEnabled((null != etype) && eSource.getTechManager().isLegal(etype));
        }
        
    };

    public void refreshTable() {
        filterEquipment();
    }
}