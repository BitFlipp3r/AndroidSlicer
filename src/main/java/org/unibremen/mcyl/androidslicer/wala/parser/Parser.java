package org.unibremen.mcyl.androidslicer.wala.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import org.unibremen.mcyl.androidslicer.service.SliceLogger;

//TODO source philip, fix for breaking changes since 1.5
public class Parser {

	public static CompilationUnit getCu(final String javaPath) throws IOException {
		try {
			FileInputStream in = new FileInputStream(new File(javaPath));
			CompilationUnit cu = null;
			cu = StaticJavaParser.parse(in);
			in.close();
			return cu;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Set<Integer> getModifiedSlice(final String javaPath, final Set<Integer> sliceLines, SliceLogger logger) {
		MethodVisitor visitor = new MethodVisitor(sliceLines);

		try {
			CompilationUnit compilationUnit = getCu(javaPath);
			if (compilationUnit != null) {
				visitor.visit(compilationUnit, "");
				return visitor.getSlice();
			} else {
				logger.log(javaPath + " file not found! Skipping!\n");
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Set<Integer> emptyResult = new HashSet<>();
		return emptyResult;
	}
}
