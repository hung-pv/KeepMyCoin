package com.keepmycoin.tools.project;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class AddDirectPrjDependency {

	public static void main(String[] args) throws Exception {
		linkProject(args[0], args[1]);
	}

	private static void linkProject(String lib, String target) throws Exception {
		o("Linking %s into %s", lib, target);
		Path targetClassPath = Paths.get(".", target, ".classpath");
		List<String> lines = Files.readAllLines(targetClassPath, StandardCharsets.UTF_8);
		lines = lines.stream().filter(l -> !l.trim().isEmpty()).collect(Collectors.toList());

		String entry = String.format("<classpathentry combineaccessrules=\"false\" kind=\"src\" path=\"/%s\"/>", lib);
		if (!lines.stream().anyMatch(l -> l.trim().contains(entry))) {
			lines.add(lines.size() - 1, "  " + entry);
			Files.write(targetClassPath, lines, StandardCharsets.UTF_8);
			o("\tLinked successfully");
		} else {
			o("\tAlready linked before");
		}
	}

	private static void o(String format, Object... args) {
		System.out.println(String.format(format, args));
	}

}
