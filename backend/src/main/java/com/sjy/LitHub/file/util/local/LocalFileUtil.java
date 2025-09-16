package com.sjy.LitHub.file.util.local;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

import com.sjy.LitHub.file.util.FileConstant;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalFileUtil {

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
	public static void delete(String pathStr) {
		Path path = Path.of(pathStr);
		if (!Files.exists(path)) return;

		if (Files.isDirectory(path)) {
			try (Stream<Path> walk = Files.walk(path)) {
				// 파일부터 지우고 마지막에 디렉토리 삭제
				walk.sorted(Comparator.reverseOrder())
					.forEach(LocalFileUtil::safeDelete);
			}
		} else {
			safeDelete(path);
		}
	}

	private static void safeDelete(Path p) {
		try {
			Files.deleteIfExists(p);
		} catch (IOException e) {
			try {
				// 재시도 한 번
				Thread.sleep(100);
				Files.deleteIfExists(p);
			} catch (Exception ex) {
				log.warn("파일 삭제 최종 실패: {}", p, ex);
			}
		}
	}

	public static String getOriginalFileName(String path) {
		String name = Path.of(path).getFileName().toString();
		return name.contains(FileConstant.ORIGINAL_FILE_NAME_SEPARATOR)
			? name.substring(name.indexOf(FileConstant.ORIGINAL_FILE_NAME_SEPARATOR)
			+ FileConstant.ORIGINAL_FILE_NAME_SEPARATOR.length())
			: name;
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