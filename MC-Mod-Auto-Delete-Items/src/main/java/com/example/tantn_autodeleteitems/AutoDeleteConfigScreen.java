package com.example.tantn_autodeleteitems;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class AutoDeleteConfigScreen extends Screen {
    private EditBox searchBox;
    private java.util.List<String> allItems;
    private java.util.List<String> allItemsLower;
    private java.util.List<String> viewItems;
    private final java.util.List<Button> itemButtons = new java.util.ArrayList<>();
    private int page = 0;
    private int pageSize = 30;
    private Button prevBtn;
    private Button nextBtn;
    private int intervalMinutes;
    private Button intervalDownBtn;
    private Button intervalUpBtn;
    private Button intervalValueBtn;

    public AutoDeleteConfigScreen() {
        super(Component.literal("Auto Delete Items Config"));
    }

    @Override
    protected void init() {
        Config.startBatch();

        // Build the complete list of item ids once per screen open
        allItems = new java.util.ArrayList<>();
        allItemsLower = new java.util.ArrayList<>();
        var items = BuiltInRegistries.ITEM;
        for (var item : items) {
            Identifier id = items.getKey(item);
            if (id != null) {
                String s = id.toString();
                allItems.add(s);
                Config.addKnownEphemeral(s);
            }
        }
        allItems.sort(String::compareTo);
        for (String s : allItems) allItemsLower.add(s.toLowerCase());

        updateView();

        int margin = 12;
        int centerX = this.width / 2;

        searchBox = new EditBox(this.font, margin, 20, this.width - margin * 2 - 180, 20, Component.literal("Search"));
        searchBox.setMaxLength(256);
        searchBox.setResponder(this::applyFilter);
        addRenderableWidget(searchBox);

        prevBtn = addRenderableWidget(Button.builder(Component.literal("< Prev"), b -> {
            if (page > 0) { page--; rebuildButtons(); }
        }).pos(this.width - 170, 20).size(70, 20).build());

        nextBtn = addRenderableWidget(Button.builder(Component.literal("Next >"), b -> {
            int size = viewItems == null ? 0 : viewItems.size();
            int maxPage = Math.max(0, (size - 1) / pageSize);
            if (page < maxPage) { page++; rebuildButtons(); }
        }).pos(this.width - 95, 20).size(70, 20).build());

        // Done button
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .pos(centerX - 40, this.height - 28).size(80, 20).build());

        // Interval controls
        intervalMinutes = Config.getDeleteIntervalMinutes();
        intervalDownBtn = addRenderableWidget(Button.builder(Component.literal("-"), b -> {
            if (intervalMinutes > 1) {
                intervalMinutes--;
                updateIntervalButtons();
            }
        }).pos(centerX - 76, 47).size(20, 20).build());
        intervalValueBtn = addRenderableWidget(Button.builder(
                Component.literal(" " + intervalMinutes + " min "), b -> {})
                .pos(centerX - 52, 47).size(64, 20).build());
        intervalUpBtn = addRenderableWidget(Button.builder(Component.literal("+"), b -> {
            if (intervalMinutes < 60) {
                intervalMinutes++;
                updateIntervalButtons();
            }
        }).pos(centerX + 16, 47).size(20, 20).build());

        rebuildButtons();
    }

    private void updateIntervalButtons() {
        intervalDownBtn.active = intervalMinutes > 1;
        intervalUpBtn.active = intervalMinutes < 60;
        intervalValueBtn.setMessage(Component.literal(" " + intervalMinutes + " min "));
    }

    private void applyFilter(String query) {
        updateView();
        page = 0;
        rebuildButtons();
    }

    private void updateView() {
        String q = searchBox == null ? "" : (searchBox.getValue() == null ? "" : searchBox.getValue().toLowerCase());
        java.util.Set<String> deleteList = Config.getDeleteList();
        java.util.ArrayList<String> list = new java.util.ArrayList<>();
        if (q.isEmpty()) {
            list.addAll(deleteList);
        } else {
            int n = allItems.size();
            var lower = allItemsLower;
            var ids = allItems;
            for (int i = 0; i < n; i++) {
                if (lower.get(i).contains(q)) list.add(ids.get(i));
            }
        }
        viewItems = list;
    }

    private void rebuildButtons() {
        for (Button btn : itemButtons) {
            this.removeWidget(btn);
        }
        itemButtons.clear();

        int columns = 3;
        int rows = 10;
        pageSize = columns * rows;
        int gridLeft = 16;
        int gridRight = this.width - 16;
        int gridTop = 82;
        int cellW = (gridRight - gridLeft - (columns - 1) * 6) / columns;
        int cellH = 18;

        java.util.List<String> list = viewItems == null ? java.util.List.of() : viewItems;
        int start = page * pageSize;
        int end = Math.min(list.size(), start + pageSize);
        java.util.Set<String> deleteList = Config.getDeleteList();

        for (int idx = start; idx < end; idx++) {
            String id = list.get(idx);
            int rel = idx - start;
            int row = rel / columns;
            int col = rel % columns;
            int x = gridLeft + col * (cellW + 6);
            int y = gridTop + row * (cellH + 4);

            boolean isMarked = deleteList.contains(id);
            String display = id.startsWith("minecraft:") ? id.substring("minecraft:".length()) : id;
            Component label = Component.literal((isMarked ? "[X] " : "[ ] ") + display);
            Button btn = Button.builder(label, b -> {
                Config.toggleDelete(id);
                updateView();
                rebuildButtons();
            }).pos(x, y).size(cellW, cellH).build();
            itemButtons.add(addRenderableWidget(btn));
        }

        int maxPage = Math.max(0, ((viewItems == null ? 0 : viewItems.size()) - 1) / pageSize);
        prevBtn.active = page > 0;
        nextBtn.active = page < maxPage;
    }

    @Override
    public void onClose() {
        Config.setDeleteIntervalMinutes(intervalMinutes);
        Config.endBatch();
        super.onClose();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 6, 0xFFFFFF);
        guiGraphics.text(this.font, Component.literal("Delete Interval (min):"), 16, 49, 0xAAAAAA);

        int total = viewItems == null ? 0 : viewItems.size();
        int maxPage = total == 0 ? 0 : (total - 1) / pageSize;
        String info = "Items: " + total + "  Page: " + (Math.min(page, maxPage) + 1) + "/" + (maxPage + 1);
        guiGraphics.text(this.font, info, 16, 71, 0xAAAAAA);
    }
}
