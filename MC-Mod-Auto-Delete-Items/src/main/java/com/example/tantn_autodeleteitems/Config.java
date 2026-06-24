package com.example.tantn_autodeleteitems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Config {

    private static final Logger LOGGER = LogManager.getLogger("AutoDeleteItems-Config");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Config file path: <minecraft_dir>/config/autodeleteitems.json
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("autodeleteitems.json");
    private static final File CONFIG_FILE = CONFIG_PATH.toFile();

    // Set of all item IDs ever seen (for UI display)
    private static final Set<String> KNOWN_ITEMS = new ConcurrentSkipListSet<>();
    // Set of item IDs to auto-delete from inventory
    private static final Set<String> DELETE_LIST = new ConcurrentSkipListSet<>();

    // Global enable switch
    private static final AtomicBoolean ENABLED = new AtomicBoolean(true);

    // Delete interval in minutes (default: 5)
    private static int DELETE_INTERVAL_MINUTES = 5;

    // Batch flags
    private static final AtomicBoolean BATCH_SAVING = new AtomicBoolean(false);
    private static final AtomicBoolean DIRTY = new AtomicBoolean(false);

    private Config() {
        // no-op
    }

    private static final class ConfigData {
        @SerializedName("known")
        List<String> known = new ArrayList<>();
        @SerializedName("delete_list")
        List<String> deleteList = new ArrayList<>();
        @SerializedName("delete_interval_minutes")
        int deleteIntervalMinutes = 5;
    }

    /** Load from disk (thread-safe) */
    public static void load() {
        synchronized (Config.class) {
            KNOWN_ITEMS.clear();
            DELETE_LIST.clear();
            DELETE_INTERVAL_MINUTES = 5;
            if (CONFIG_FILE.exists()) {
                try (FileReader reader = new FileReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
                    final ConfigData data = GSON.fromJson(reader, ConfigData.class);
                    if (data != null) {
                        if (data.known != null) KNOWN_ITEMS.addAll(data.known);
                        if (data.deleteList != null) DELETE_LIST.addAll(data.deleteList);
                        if (data.deleteIntervalMinutes > 0) DELETE_INTERVAL_MINUTES = data.deleteIntervalMinutes;
                    }
                } catch (Exception e) {
                    LOGGER.warn("Failed to load config: {}", CONFIG_FILE.getAbsolutePath(), e);
                }
            }
            DIRTY.set(false);
        }
    }

    /** Save to disk (thread-safe, atomic move) */
    public static void save() {
        if (BATCH_SAVING.get()) {
            DIRTY.set(true);
            return;
        }

        final List<String> knownSnapshot = new ArrayList<>(KNOWN_ITEMS);
        final List<String> deleteSnapshot = new ArrayList<>(DELETE_LIST);
        final int intervalSnapshot = DELETE_INTERVAL_MINUTES;

        synchronized (Config.class) {
            final File parent = CONFIG_FILE.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                LOGGER.warn("Could not create config directory: {}", parent.getAbsolutePath());
            }

            final ConfigData data = new ConfigData();
            data.known = knownSnapshot;
            data.deleteList = deleteSnapshot;
            data.deleteIntervalMinutes = intervalSnapshot;

            final Path tempPath = CONFIG_PATH.resolveSibling(CONFIG_PATH.getFileName() + ".tmp");

            try (Writer writer = new BufferedWriter(Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8))) {
                GSON.toJson(data, writer);
            } catch (IOException e) {
                LOGGER.error("Failed to write temp config file: {}", tempPath, e);
                return;
            }

            try {
                Files.move(tempPath, CONFIG_PATH, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                try {
                    Files.move(tempPath, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    LOGGER.error("Failed to replace config file (non-atomic): {}", CONFIG_PATH, e);
                    return;
                }
            } catch (IOException e) {
                LOGGER.error("Failed to move temp config to final: {} -> {}", tempPath, CONFIG_PATH, e);
                return;
            }

            DIRTY.set(false);
        }
    }

    /** Add known + save immediately */
    public static boolean addKnown(final String item) {
        final boolean added = KNOWN_ITEMS.add(item);
        if (added) save();
        return added;
    }

    /** Remove known + clean deleteList + save */
    public static boolean removeKnown(final String item) {
        final boolean removed = KNOWN_ITEMS.remove(item);
        if (removed) {
            DELETE_LIST.remove(item);
            save();
        }
        return removed;
    }

    /** Unmodifiable view of known items */
    public static Set<String> getKnown() {
        return Collections.unmodifiableSet(KNOWN_ITEMS);
    }

    /** Unmodifiable view of delete list */
    public static Set<String> getDeleteList() {
        return Collections.unmodifiableSet(DELETE_LIST);
    }

    /** Replace entire delete list */
    public static void setDeleteList(final Collection<String> items) {
        synchronized (Config.class) {
            DELETE_LIST.clear();
            DELETE_LIST.addAll(items);
            save();
        }
    }

    /** Check if an item is in the delete list */
    public static boolean isInDeleteList(final String item) {
        return DELETE_LIST.contains(item);
    }

    /** Check if the mod is globally enabled */
    public static boolean isEnabled() {
        return ENABLED.get();
    }

    /** Toggle an item in the delete list + save */
    public static void toggleDelete(final String item) {
        synchronized (Config.class) {
            KNOWN_ITEMS.add(item);
            if (!DELETE_LIST.add(item)) {
                DELETE_LIST.remove(item);
            }
            save();
        }
    }

    /** Add item to delete list + save */
    public static void addToDeleteList(final String item) {
        synchronized (Config.class) {
            KNOWN_ITEMS.add(item);
            DELETE_LIST.add(item);
            save();
        }
    }

    /** Remove item from delete list + save */
    public static void removeFromDeleteList(final String item) {
        synchronized (Config.class) {
            KNOWN_ITEMS.add(item);
            DELETE_LIST.remove(item);
            save();
        }
    }

    /**
     * Add known ephemerally (hot path).
     * - No immediate disk I/O.
     * - If in batch mode, marks DIRTY; save happens at endBatch if needed.
     */
    public static void addKnownEphemeral(final String item) {
        if (KNOWN_ITEMS.add(item) && BATCH_SAVING.get()) {
            DIRTY.set(true);
        }
    }

    /** Start batch mode (defer saves until endBatch) */
    public static void startBatch() {
        BATCH_SAVING.set(true);
    }

    /** End batch mode: save once if dirty */
    public static void endBatch() {
        BATCH_SAVING.set(false);
        if (DIRTY.get()) {
            save();
        }
    }

    /** Get delete interval in minutes */
    public static int getDeleteIntervalMinutes() {
        return DELETE_INTERVAL_MINUTES;
    }

    /** Set delete interval in minutes (1-60) */
    public static void setDeleteIntervalMinutes(int minutes) {
        if (minutes < 1) minutes = 1;
        if (minutes > 60) minutes = 60;
        DELETE_INTERVAL_MINUTES = minutes;
        save();
    }

    /** Get delete interval in ticks (20 ticks = 1 second) */
    public static int getDeleteIntervalTicks() {
        return DELETE_INTERVAL_MINUTES * 20 * 60;
    }
}
