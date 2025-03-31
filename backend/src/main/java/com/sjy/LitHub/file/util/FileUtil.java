package com.sjy.LitHub.file.util;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtil {

	private static final String ORIGINAL_FILE_NAME_SEPARATOR = "--originalFileName_";

	public static String getFileExtTypeCode(String ext) {
		return switch (ext) {
			case "jpeg", "jpg", "gif", "png", "svg", "webp" -> "img";
			case "mp4", "avi", "mov" -> "video";
			case "mp3", "m4a" -> "audio";
			default -> "etc";
		};
	}

	public static String getFileExtension(String filePath) {
		String name = getOriginalFileName(filePath);
		return name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : "";
	}

	@SneakyThrows
	public static void copy(String from, String to) {
		Path toPath = Path.of(to);
		mkdir(toPath.getParent().toString());
		Files.copy(Path.of(from), toPath, StandardCopyOption.REPLACE_EXISTING);
	}

	@SneakyThrows
	public static void delete(String dirPath) {
		Path dir = Path.of(dirPath);
		if (!Files.exists(dir)) return;

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path entry : stream) {
				Files.deleteIfExists(entry);
			}
		}
	}

	public static String getOriginalFileName(String path) {
		String name = Path.of(path).getFileName().toString();
		return name.contains(ORIGINAL_FILE_NAME_SEPARATOR)
			? name.substring(name.indexOf(ORIGINAL_FILE_NAME_SEPARATOR) + ORIGINAL_FILE_NAME_SEPARATOR.length())
			: name;
	}

	@SneakyThrows
	public static long getFileSize(String path) {
		return Files.size(Path.of(path));
	}

	@SneakyThrows
	public static void mkdir(String dirPath) {
		Path path = Path.of(dirPath);
		if (!Files.exists(path)) {
			Files.createDirectories(path);
		}
	}

	public static String firstLowerCase(String str) {
		return Character.toLowerCase(str.charAt(0)) + str.substring(1);
	}
}