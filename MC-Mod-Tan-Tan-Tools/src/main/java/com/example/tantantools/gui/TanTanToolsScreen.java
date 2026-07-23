package com.example.tantantools.gui;

import com.example.tantantools.autodelete.AutoDeleteConfig;
import com.example.tantantools.autoeat.AutoEatConfig;
import com.example.tantantools.autotransfer.AutoTransferConfig;
import com.example.tantantools.combineenchanteditems.CombineEnchantedItemsConfig;
import com.example.tantantools.expfromnature.ExpFromNatureConfig;
import com.example.tantantools.mobcustomizer.MobConfigs;
import com.example.tantantools.mobcustomizer.MobCustomizerConfig;
import com.example.tantantools.mobcustomizer.SpawnEventHandler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Main settings screen for Tan Tan Tools. */
public final class TanTanToolsScreen extends Screen {

    private enum Tab {
        AUTO_DELETE("Auto Delete"),
        AUTO_EAT("Auto Eat"),
        AUTO_TRANSFER("Auto Transfer"),
        EXP_FROM_NATURE("Exp From Nature"),
        COMBINE_ENCHANTED_ITEMS_EXP("Combine Enchanted Items Exp"),
        MOB_CUSTOMIZER("Mob Customizer");

        final String label;

        Tab(String label) {
            this.label = label;
        }
    }

    private static final int CONTENT_TOP = 54;
    private static final int MENU_TOP = 54;
    private static final int MENU_BUTTON_HEIGHT = 24;
    private static final int MENU_GAP = 8;

    private Tab currentTab;
    private final List<Button> menuButtons = new ArrayList<>();
    private final List<net.minecraft.client.gui.components.AbstractWidget> contentWidgets = new ArrayList<>();

    // ===== Auto Delete tab state =====
    private EditBox adSearchBox;
    private List<String> adAllItems;
    private List<String> adAllItemsLower;
    private List<String> adViewItems;
    private Set<String> adDeleteList;
    private int adPage = 0;
    private int adPageSize = 24;
    private Button adPrevBtn;
    private Button adNextBtn;
    private int adIntervalMinutes;
    private Button adIntervalValueBtn;
    private Checkbox adEnabledBox;

    public TanTanToolsScreen() {
        super(Component.literal("Tan Tan Tools Settings"));
    }

    @Override
    protected void init() {
        if (currentTab == null) {
            buildMenu();
        } else {
            rebuildTabContent();
            addBackButton();
        }
    }

    private void clearMenuButtons() {
        for (Button b : menuButtons) {
            this.removeWidget(b);
        }
        menuButtons.clear();
    }

    private void buildMenu() {
        clearContentWidgets();
        clearMenuButtons();

        int margin = 24;
        int columnGap = 10;
        int buttonWidth = (this.width - margin * 2 - columnGap) / 2;
        List<Tab> tabs = new ArrayList<>(List.of(Tab.values()));
        tabs.sort(Comparator.comparing(tab -> tab.label));

        for (int i = 0; i < tabs.size(); i++) {
            Tab tab = tabs.get(i);
            int column = i % 2;
            int row = i / 2;
            int x = margin + column * (buttonWidth + columnGap);
            int y = MENU_TOP + row * (MENU_BUTTON_HEIGHT + MENU_GAP);
            Button btn = Button.builder(Component.literal(tab.label), b -> openDetail(tab))
                    .pos(x, y)
                    .size(buttonWidth, MENU_BUTTON_HEIGHT)
                    .build();
            menuButtons.add(addRenderableWidget(btn));
        }

        menuButtons.add(addRenderableWidget(Button.builder(Component.literal("Save & Close"), b -> onClose())
                .pos(this.width / 2 - 50, this.height - 32)
                .size(100, 20)
                .build()));
    }

    private void openDetail(Tab tab) {
        currentTab = tab;
        rebuildTabContent();
        addBackButton();
    }

    private void addBackButton() {
        int btnY = this.height - 28;
        int groupWidth = 80 + 10 + 100;
        int startX = (this.width - groupWidth) / 2;
        addContent(Button.builder(Component.literal("Back"), b -> returnToMenu())
                .pos(startX, btnY)
                .size(80, 20)
                .build());
        addContent(Button.builder(Component.literal("Save & Close"), b -> onClose())
                .pos(startX + 90, btnY)
                .size(100, 20)
                .build());
    }

    private void returnToMenu() {
        saveCurrentTab();
        currentTab = null;
        buildMenu();
    }

    private void clearContentWidgets() {
        for (var w : contentWidgets) {
            this.removeWidget(w);
        }
        contentWidgets.clear();
    }

    private <T extends net.minecraft.client.gui.components.AbstractWidget> T addContent(T widget) {
        contentWidgets.add(addRenderableWidget(widget));
        return widget;
    }

    private void rebuildTabContent() {
        clearContentWidgets();
        clearMenuButtons();
        switch (currentTab) {
            case AUTO_DELETE -> buildAutoDeleteTab();
            case AUTO_EAT -> buildAutoEatTab();
            case AUTO_TRANSFER -> buildAutoTransferTab();
            case EXP_FROM_NATURE -> buildExpFromNatureTab();
            case COMBINE_ENCHANTED_ITEMS_EXP -> buildCombineEnchantedItemsExpTab();
            case MOB_CUSTOMIZER -> buildMobCustomizerTab();
        }
    }

    // ===================================================================
    // Auto Delete tab
    // ===================================================================

    private void buildAutoDeleteTab() {
        adAllItems = new ArrayList<>();
        adAllItemsLower = new ArrayList<>();
        for (var item : BuiltInRegistries.ITEM) {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            if (id != null) {
                adAllItems.add(id.toString());
            }
        }
        adAllItems.sort(Comparator.naturalOrder());
        for (String s : adAllItems) adAllItemsLower.add(s.toLowerCase());

        adDeleteList = new HashSet<>(AutoDeleteConfig.DELETE_LIST.get());
        adIntervalMinutes = AutoDeleteConfig.DELETE_INTERVAL_MINUTES.get();

        int margin = 12;
        int centerX = this.width / 2;

        adEnabledBox = addContent(Checkbox.builder(Component.literal("Enable Auto Delete"), this.font)
                .pos(margin, CONTENT_TOP)
                .selected(AutoDeleteConfig.ENABLED.get())
                .build());

        adSearchBox = addContent(new EditBox(this.font, margin, CONTENT_TOP + 24, this.width - margin * 2 - 180, 20, Component.literal("Search")));
        adSearchBox.setMaxLength(256);
        adSearchBox.setResponder(q -> {
            updateAutoDeleteView();
            adPage = 0;
            rebuildAutoDeleteGrid();
        });

        adPrevBtn = addContent(Button.builder(Component.literal("< Prev"), b -> {
            if (adPage > 0) { adPage--; rebuildAutoDeleteGrid(); }
        }).pos(this.width - 170, CONTENT_TOP + 24).size(70, 20).build());

        adNextBtn = addContent(Button.builder(Component.literal("Next >"), b -> {
            int size = adViewItems == null ? 0 : adViewItems.size();
            int maxPage = Math.max(0, (size - 1) / adPageSize);
            if (adPage < maxPage) { adPage++; rebuildAutoDeleteGrid(); }
        }).pos(this.width - 95, CONTENT_TOP + 24).size(70, 20).build());

        addContent(Button.builder(Component.literal("-"), b -> {
            if (adIntervalMinutes > 1) {
                adIntervalMinutes--;
                adIntervalValueBtn.setMessage(Component.literal(" " + adIntervalMinutes + " min "));
            }
        }).pos(centerX - 76, CONTENT_TOP + 51).size(20, 20).build());

        adIntervalValueBtn = addContent(Button.builder(
                Component.literal(" " + adIntervalMinutes + " min "), b -> {})
                .pos(centerX - 52, CONTENT_TOP + 51).size(64, 20).build());

        addContent(Button.builder(Component.literal("+"), b -> {
            if (adIntervalMinutes < 60) {
                adIntervalMinutes++;
                adIntervalValueBtn.setMessage(Component.literal(" " + adIntervalMinutes + " min "));
            }
        }).pos(centerX + 16, CONTENT_TOP + 51).size(20, 20).build());

        updateAutoDeleteView();
        rebuildAutoDeleteGrid();
    }

    private void updateAutoDeleteView() {
        String q = adSearchBox == null ? "" : adSearchBox.getValue().toLowerCase();
        List<String> list = new ArrayList<>();
        if (q.isEmpty()) {
            list.addAll(adDeleteList);
            list.sort(Comparator.naturalOrder());
        } else {
            for (int i = 0; i < adAllItems.size(); i++) {
                if (adAllItemsLower.get(i).contains(q)) list.add(adAllItems.get(i));
            }
        }
        adViewItems = list;
    }

    private final List<Button> adItemButtons = new ArrayList<>();

    private void rebuildAutoDeleteGrid() {
        for (Button btn : adItemButtons) {
            this.removeWidget(btn);
            contentWidgets.remove(btn);
        }
        adItemButtons.clear();

        int columns = 3;
        int rows = 8;
        adPageSize = columns * rows;
        int gridLeft = 16;
        int gridRight = this.width - 16;
        int gridTop = CONTENT_TOP + 82;
        int cellW = (gridRight - gridLeft - (columns - 1) * 6) / columns;
        int cellH = 18;

        List<String> list = adViewItems == null ? List.of() : adViewItems;
        int start = adPage * adPageSize;
        int end = Math.min(list.size(), start + adPageSize);

        for (int idx = start; idx < end; idx++) {
            String id = list.get(idx);
            int rel = idx - start;
            int row = rel / columns;
            int col = rel % columns;
            int x = gridLeft + col * (cellW + 6);
            int y = gridTop + row * (cellH + 4);

            boolean isMarked = adDeleteList.contains(id);
            String display = id.startsWith("minecraft:") ? id.substring("minecraft:".length()) : id;
            Component label = Component.literal((isMarked ? "[X] " : "[ ] ") + display);
            Button btn = Button.builder(label, b -> {
                if (!adDeleteList.add(id)) {
                    adDeleteList.remove(id);
                }
                updateAutoDeleteView();
                rebuildAutoDeleteGrid();
            }).pos(x, y).size(cellW, cellH).build();
            adItemButtons.add(btn);
            contentWidgets.add(addRenderableWidget(btn));
        }

        int maxPage = Math.max(0, ((adViewItems == null ? 0 : adViewItems.size()) - 1) / adPageSize);
        if (adPrevBtn != null) adPrevBtn.active = adPage > 0;
        if (adNextBtn != null) adNextBtn.active = adPage < maxPage;
    }

    private void saveAutoDeleteTab() {
        if (adDeleteList == null) return;
        AutoDeleteConfig.ENABLED.set(adEnabledBox.selected());
        AutoDeleteConfig.DELETE_LIST.set(new ArrayList<>(adDeleteList));
        AutoDeleteConfig.DELETE_INTERVAL_MINUTES.set(adIntervalMinutes);
        AutoDeleteConfig.SPEC.save();
    }

    // ===================================================================
    // Auto Eat tab
    // ===================================================================

    private Checkbox aeEnabledBox;
    private int aeCheckIntervalTicks;
    private int aeLowHungerPercent;
    private int aeLowHealthPercent;
    private Button aeCheckIntervalBtn;
    private Button aeLowHungerBtn;
    private Button aeLowHealthBtn;

    private void buildAutoEatTab() {
        aeCheckIntervalTicks = AutoEatConfig.EAT_CHECK_INTERVAL_TICKS.get();
        aeLowHungerPercent = AutoEatConfig.LOW_HUNGER_PERCENT.get();
        aeLowHealthPercent = AutoEatConfig.LOW_HEALTH_PERCENT.get();

        int left = 16;
        int centerX = this.width / 2;
        int y = CONTENT_TOP;

        aeEnabledBox = addContent(Checkbox.builder(Component.literal("Enable Auto Eat"), this.font)
                .pos(left, y)
                .selected(AutoEatConfig.ENABLED.get())
                .build());

        y += 32;
        addContent(Button.builder(Component.literal("-"), b -> {
            if (aeCheckIntervalTicks > 20) {
                aeCheckIntervalTicks -= 20;
                aeCheckIntervalBtn.setMessage(Component.literal(ticksLabel("Check interval", aeCheckIntervalTicks)));
            }
        }).pos(centerX - 76, y).size(20, 20).build());
        aeCheckIntervalBtn = addContent(Button.builder(Component.literal(ticksLabel("Check interval", aeCheckIntervalTicks)), b -> {})
                .pos(centerX - 52, y).size(220, 20).build());
        addContent(Button.builder(Component.literal("+"), b -> {
            if (aeCheckIntervalTicks < 200) {
                aeCheckIntervalTicks += 20;
                aeCheckIntervalBtn.setMessage(Component.literal(ticksLabel("Check interval", aeCheckIntervalTicks)));
            }
        }).pos(centerX + 172, y).size(20, 20).build());

        y += 26;
        addContent(Button.builder(Component.literal("-"), b -> {
            if (aeLowHungerPercent > 10) {
                aeLowHungerPercent -= 5;
                aeLowHungerBtn.setMessage(Component.literal(percentLabel("Eat below hunger %", aeLowHungerPercent)));
            }
        }).pos(centerX - 76, y).size(20, 20).build());
        aeLowHungerBtn = addContent(Button.builder(Component.literal(percentLabel("Eat below hunger %", aeLowHungerPercent)), b -> {})
                .pos(centerX - 52, y).size(220, 20).build());
        addContent(Button.builder(Component.literal("+"), b -> {
            if (aeLowHungerPercent < 100) {
                aeLowHungerPercent += 5;
                aeLowHungerBtn.setMessage(Component.literal(percentLabel("Eat below hunger %", aeLowHungerPercent)));
            }
        }).pos(centerX + 172, y).size(20, 20).build());

        y += 26;
        addContent(Button.builder(Component.literal("-"), b -> {
            if (aeLowHealthPercent > 10) {
                aeLowHealthPercent -= 5;
                aeLowHealthBtn.setMessage(Component.literal(percentLabel("Eat anytime below health %", aeLowHealthPercent)));
            }
        }).pos(centerX - 76, y).size(20, 20).build());
        aeLowHealthBtn = addContent(Button.builder(Component.literal(percentLabel("Eat anytime below health %", aeLowHealthPercent)), b -> {})
                .pos(centerX - 52, y).size(220, 20).build());
        addContent(Button.builder(Component.literal("+"), b -> {
            if (aeLowHealthPercent < 100) {
                aeLowHealthPercent += 5;
                aeLowHealthBtn.setMessage(Component.literal(percentLabel("Eat anytime below health %", aeLowHealthPercent)));
            }
        }).pos(centerX + 172, y).size(20, 20).build());
    }

    private static String ticksLabel(String prefix, int ticks) {
        return prefix + ": " + ticks + " ticks (" + (ticks / 20.0) + "s)";
    }

    private static String percentLabel(String prefix, int percent) {
        return prefix + ": " + percent + "%";
    }

    private void saveAutoEatTab() {
        if (aeEnabledBox == null) return;
        AutoEatConfig.ENABLED.set(aeEnabledBox.selected());
        AutoEatConfig.EAT_CHECK_INTERVAL_TICKS.set(aeCheckIntervalTicks);
        AutoEatConfig.LOW_HUNGER_PERCENT.set(aeLowHungerPercent);
        AutoEatConfig.LOW_HEALTH_PERCENT.set(aeLowHealthPercent);
        AutoEatConfig.ENABLED.save();
    }

    // ===================================================================
    // Auto Transfer tab
    // ===================================================================

    private Checkbox atHotbarBox;
    private int atMaxSlots;
    private Button atMaxSlotsBtn;

    private void buildAutoTransferTab() {
        atMaxSlots = AutoTransferConfig.MAX_TRANSFER_SLOTS.get();

        int left = 16;
        int centerX = this.width / 2;
        int y = CONTENT_TOP;

        atHotbarBox = addContent(Checkbox.builder(Component.literal("Include hotbar (slots 0-8) when transferring"), this.font)
                .pos(left, y)
                .selected(AutoTransferConfig.TRANSFER_HOTBAR.get())
                .build());

        y += 32;
        addContent(Button.builder(Component.literal("-"), b -> {
            if (atMaxSlots > 1) {
                atMaxSlots--;
                atMaxSlotsBtn.setMessage(Component.literal("Max container slots: " + atMaxSlots));
            }
        }).pos(centerX - 76, y).size(20, 20).build());
        atMaxSlotsBtn = addContent(Button.builder(Component.literal("Max container slots: " + atMaxSlots), b -> {})
                .pos(centerX - 52, y).size(150, 20).build());
        addContent(Button.builder(Component.literal("+"), b -> {
            if (atMaxSlots < 256) {
                atMaxSlots++;
                atMaxSlotsBtn.setMessage(Component.literal("Max container slots: " + atMaxSlots));
            }
        }).pos(centerX + 102, y).size(20, 20).build());

        y += 32;
        addContent(new net.minecraft.client.gui.components.MultiLineTextWidget(left, y,
                Component.literal("Whitelist items are managed via the config file (autotransfer whitelistItems)."),
                this.font).setMaxWidth(this.width - left * 2));
    }

    private void saveAutoTransferTab() {
        if (atHotbarBox == null) return;
        AutoTransferConfig.TRANSFER_HOTBAR.set(atHotbarBox.selected());
        AutoTransferConfig.MAX_TRANSFER_SLOTS.set(atMaxSlots);
        AutoTransferConfig.TRANSFER_HOTBAR.save();
    }

    // ===================================================================
    // Exp From Nature tab
    // ===================================================================

    private Checkbox enEnabledBox;
    private int enXpPerStone;
    private int enXpPerTree;
    private Button enXpStoneBtn;
    private Button enXpTreeBtn;

    private void buildExpFromNatureTab() {
        enXpPerStone = ExpFromNatureConfig.XP_PER_STONE_BLOCK.get();
        enXpPerTree = ExpFromNatureConfig.XP_PER_TREE_BLOCK.get();

        int left = 16;
        int centerX = this.width / 2;
        int y = CONTENT_TOP;

        enEnabledBox = addContent(Checkbox.builder(Component.literal("Enable Exp From Nature"), this.font)
                .pos(left, y)
                .selected(ExpFromNatureConfig.ENABLED.get())
                .build());

        y += 32;
        addContent(Button.builder(Component.literal("-"), b -> {
            if (enXpPerStone > 0) {
                enXpPerStone--;
                enXpStoneBtn.setMessage(Component.literal("XP per stone block: " + enXpPerStone));
            }
        }).pos(centerX - 76, y).size(20, 20).build());
        enXpStoneBtn = addContent(Button.builder(Component.literal("XP per stone block: " + enXpPerStone), b -> {})
                .pos(centerX - 52, y).size(150, 20).build());
        addContent(Button.builder(Component.literal("+"), b -> {
            if (enXpPerStone < 10) {
                enXpPerStone++;
                enXpStoneBtn.setMessage(Component.literal("XP per stone block: " + enXpPerStone));
            }
        }).pos(centerX + 102, y).size(20, 20).build());

        y += 26;
        addContent(Button.builder(Component.literal("-"), b -> {
            if (enXpPerTree > 0) {
                enXpPerTree--;
                enXpTreeBtn.setMessage(Component.literal("XP per tree block: " + enXpPerTree));
            }
        }).pos(centerX - 76, y).size(20, 20).build());
        enXpTreeBtn = addContent(Button.builder(Component.literal("XP per tree block: " + enXpPerTree), b -> {})
                .pos(centerX - 52, y).size(150, 20).build());
        addContent(Button.builder(Component.literal("+"), b -> {
            if (enXpPerTree < 10) {
                enXpPerTree++;
                enXpTreeBtn.setMessage(Component.literal("XP per tree block: " + enXpPerTree));
            }
        }).pos(centerX + 102, y).size(20, 20).build());
    }

    private void saveExpFromNatureTab() {
        if (enEnabledBox == null) return;
        ExpFromNatureConfig.ENABLED.set(enEnabledBox.selected());
        ExpFromNatureConfig.XP_PER_STONE_BLOCK.set(enXpPerStone);
        ExpFromNatureConfig.XP_PER_TREE_BLOCK.set(enXpPerTree);
        ExpFromNatureConfig.ENABLED.save();
    }

    // ===================================================================
    // Combine Enchanted Items Exp tab
    // ===================================================================

    private int eiXpCostPercent;
    private Button eiXpCostBtn;

    private void buildCombineEnchantedItemsExpTab() {
        eiXpCostPercent = CombineEnchantedItemsConfig.XP_COST_PERCENT.get();

        int left = 16;
        int centerX = this.width / 2;
        int y = CONTENT_TOP;

        addContent(new net.minecraft.client.gui.components.MultiLineTextWidget(left, y,
                Component.literal("Percentage of vanilla anvil XP cost to charge for repairs, renaming, and combining items."),
                this.font).setMaxWidth(this.width - left * 2));

        y += 42;
        addContent(Button.builder(Component.literal("↓"), b -> {
            eiXpCostPercent = Math.max(0, eiXpCostPercent - 5);
            eiXpCostBtn.setMessage(Component.literal("Anvil XP cost: " + eiXpCostPercent + "%"));
        }).pos(centerX - 76, y).size(20, 20).build());
        eiXpCostBtn = addContent(Button.builder(
                Component.literal("Anvil XP cost: " + eiXpCostPercent + "%"), b -> {})
                .pos(centerX - 52, y).size(150, 20).build());
        addContent(Button.builder(Component.literal("↑"), b -> {
            eiXpCostPercent = Math.min(100, eiXpCostPercent + 5);
            eiXpCostBtn.setMessage(Component.literal("Anvil XP cost: " + eiXpCostPercent + "%"));
        }).pos(centerX + 102, y).size(20, 20).build());
    }

    private void saveCombineEnchantedItemsExpTab() {
        CombineEnchantedItemsConfig.XP_COST_PERCENT.set(eiXpCostPercent);
        CombineEnchantedItemsConfig.XP_COST_PERCENT.save();
    }

    // ===================================================================
    // Mob Customizer tab
    // ===================================================================

    /** Per-mob GUI row state: checkbox + live rate value + rate label button, indexed like MobConfigs.ALL. */
    private final Checkbox[] mcAllowBoxes = new Checkbox[MobConfigs.count()];
    private final int[] mcRatePercent = new int[MobConfigs.count()];
    private final Button[] mcRateBtns = new Button[MobConfigs.count()];

    private static String mobLabel(int index) {
        return MobConfigs.get(index).entityClass().getSimpleName();
    }

    private static String rateLabel(int percent) {
        return percent + "%";
    }

    private void buildMobCustomizerTab() {
        int left = 16;
        int centerX = this.width / 2;
        int y = CONTENT_TOP;
        int rowH = 26;

        for (int i = 0; i < MobConfigs.count(); i++) {
            MobConfigs.MobDef mob = MobConfigs.get(i);
            final int idx = i;

            mcAllowBoxes[idx] = addContent(Checkbox.builder(Component.literal("Allow " + mobLabel(idx) + " spawn"), this.font)
                    .pos(left, y).selected(mob.allowSpawn().get()).build());

            mcRatePercent[idx] = mob.spawnRatePercent().get();

            addContent(Button.builder(Component.literal("-"), b -> {
                if (mcRatePercent[idx] > 1) {
                    mcRatePercent[idx] -= 5;
                    if (mcRatePercent[idx] < 1) mcRatePercent[idx] = 1;
                    mcRateBtns[idx].setMessage(Component.literal(rateLabel(mcRatePercent[idx])));
                }
            }).pos(centerX + 60, y).size(20, 20).build());

            mcRateBtns[idx] = addContent(Button.builder(Component.literal(rateLabel(mcRatePercent[idx])), b -> {})
                    .pos(centerX + 84, y).size(50, 20).build());

            addContent(Button.builder(Component.literal("+"), b -> {
                if (mcRatePercent[idx] < 300) {
                    mcRatePercent[idx] += 5;
                    if (mcRatePercent[idx] > 300) mcRatePercent[idx] = 300;
                    mcRateBtns[idx].setMessage(Component.literal(rateLabel(mcRatePercent[idx])));
                }
            }).pos(centerX + 138, y).size(20, 20).build());

            y += rowH;
        }

        y += 8;
        addContent(new net.minecraft.client.gui.components.MultiLineTextWidget(left, y,
                Component.literal("Spawn rate %: 100% = vanilla amount. Below 100% = chance to skip a spawn " +
                        "(fewer mobs). Above 100% = extra copies spawn alongside the original (more mobs, up to 300%). " +
                        "Other advanced settings (speed, damage, follow range) can be adjusted in the config file: " +
                        "tan_tan_tools-mobcustomizer.toml"),
                this.font).setMaxWidth(this.width - left * 2));
    }

    private void saveMobCustomizerTab() {
        if (mcAllowBoxes[0] == null) return;
        for (int i = 0; i < MobConfigs.count(); i++) {
            MobConfigs.MobDef mob = MobConfigs.get(i);
            mob.allowSpawn().set(mcAllowBoxes[i].selected());
            mob.spawnRatePercent().set(mcRatePercent[i]);
        }
        MobCustomizerConfig.ALLOW_ZOMBIE_SPAWN.save();
        SpawnEventHandler.refreshCache();
    }

    // ===================================================================
    // Lifecycle
    // ===================================================================

    private void saveCurrentTab() {
        if (currentTab == null) return;
        switch (currentTab) {
            case AUTO_DELETE -> saveAutoDeleteTab();
            case AUTO_EAT -> saveAutoEatTab();
            case AUTO_TRANSFER -> saveAutoTransferTab();
            case EXP_FROM_NATURE -> saveExpFromNatureTab();
            case COMBINE_ENCHANTED_ITEMS_EXP -> saveCombineEnchantedItemsExpTab();
            case MOB_CUSTOMIZER -> saveMobCustomizerTab();
        }
    }

    @Override
    public void onClose() {
        saveCurrentTab();
        super.onClose();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
        if (currentTab == null) {
            guiGraphics.centeredText(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
            guiGraphics.centeredText(this.font, Component.literal("Author: Tran Ngoc Tan"), this.width / 2, 25, 0xFFAAAAAA);
            guiGraphics.centeredText(this.font, Component.literal("info@tantn.com"), this.width / 2, 38, 0xFFAAAAAA);
        } else {
            guiGraphics.centeredText(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
        }

        if (currentTab == Tab.AUTO_DELETE && adViewItems != null) {
            int total = adViewItems.size();
            int maxPage = total == 0 ? 0 : (total - 1) / adPageSize;
            String info = "Items: " + total + "  Page: " + (Math.min(adPage, maxPage) + 1) + "/" + (maxPage + 1);
            guiGraphics.text(this.font, info, 16, CONTENT_TOP + 66, 0xFFAAAAAA);
        }
    }
}
