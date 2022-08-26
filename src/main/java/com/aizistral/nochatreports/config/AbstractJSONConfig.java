package com.aizistral.nochatreports.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.aizistral.nochatreports.NoChatReports;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public abstract class AbstractJSONConfig {
	protected static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
	protected static final Gson GSON = new GsonBuilder().setPrettyPrinting()
			.setExclusionStrategies(JSONExclusions.INSTANCE).create();
	protected final String fileName;
	protected final Path filePath;

	protected AbstractJSONConfig(String file) {
		this.fileName = file;
		this.filePath = CONFIG_DIR.resolve(this.fileName);
	}

	public Path getFile() {
		return this.filePath;
	}

	protected void save() {
		NoChatReports.LOGGER.info("Writing config file {}...", this.fileName);
		writeFile(this.fileName, this);
	}

	protected static <T extends AbstractJSONConfig> T loadConfig(Class<T> configClass, Supplier<T> freshInstance, String fileName) {
		NoChatReports.LOGGER.info("Reading config file {}...", fileName);
		return readFile(fileName, configClass).orElseGet(freshInstance);
	}

	private static <T extends AbstractJSONConfig> Optional<T> readFile(String fileName, Class<T> configClass) {
		Path file = CONFIG_DIR.resolve(fileName);

		if (!Files.isRegularFile(file))
			return Optional.empty();

		try (BufferedReader reader = Files.newBufferedReader(file)) {
			return Optional.of(GSON.fromJson(reader, configClass));
		} catch (IOException ex) {
			NoChatReports.LOGGER.fatal("Could not read config file: {}", file);
			ex.printStackTrace();
			return null;
		}
	}

	private static <T> void writeFile(String fileName, T config) {
		Path file = CONFIG_DIR.resolve(fileName);

		try {
			Files.createDirectories(file.getParent());
			try (BufferedWriter writer = Files.newBufferedWriter(file)) {
				GSON.toJson(config, writer);
			}
		} catch (Exception ex) {
			NoChatReports.LOGGER.fatal("Could not write config file: {}", file);
			throw new RuntimeException(ex);
		}
	}

	private static final class JSONExclusions implements ExclusionStrategy {
		private static final JSONExclusions INSTANCE = new JSONExclusions();

		private JSONExclusions() {
			// TODO NO-OP
		}

		@Override
		public boolean shouldSkipField(FieldAttributes field) {
			return field.getDeclaringClass() == AbstractJSONConfig.class;
		}

		@Override
		public boolean shouldSkipClass(Class<?> theClass) {
			return false;
		}

	}

}
