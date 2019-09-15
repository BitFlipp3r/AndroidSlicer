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

/**
 * This is an implementation of the of the MethodVisitor-pattern for the JavaParser described in their ebook.
 * The code based on the work by Markus Gulman (Masterthesis 2014) and Philip Phu Dang Hoan Nguyen (Masterthesis 2018) but has been 
 * heavily altered by Michael Cyl with bug fixed, improvements and refactorings. Most notable changes are the update from Version 1.5
 * to 3.14.9 along with the fix of all breaking changes.
 */
public class Parser {

	public static CompilationUnit getCu(final String javaPath, SliceLogger logger) throws IOException {
		try {
			FileInputStream in = new FileInputStream(new File(javaPath));
			CompilationUnit cu = null;
			cu = StaticJavaParser.parse(in);
			in.close();
			return cu;
		} catch (FileNotFoundException e) {
			logger.log(e.getMessage());
		}
		return null;
	}

	public static Set<Integer> getModifiedSlice(final String javaPath, final Set<Integer> sliceLines, String androidClassName, SliceLogger logger) {
		MethodVisitor visitor = new MethodVisitor(sliceLines, androidClassName);

		try {
			CompilationUnit compilationUnit = getCu(javaPath, logger);
			if (compilationUnit != null) {
				visitor.visit(compilationUnit, null);
				return visitor.getSlice();
			} else {
				logger.log(javaPath + " file not found! Skipping!\n");
				return null;
			}
		} catch (IOException e) {
			logger.log(e.getMessage());
		}

		Set<Integer> emptyResult = new HashSet<>();
		return emptyResult;
	}
}
