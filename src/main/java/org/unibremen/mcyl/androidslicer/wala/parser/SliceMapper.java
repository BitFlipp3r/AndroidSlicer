package org.unibremen.mcyl.androidslicer.wala.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.unibremen.mcyl.androidslicer.service.SliceLogger;

/**
 * This is an implementation of a mapper class to get the actual source code
 * lines from their corresponding line numbers. The code based on the work by
 * Markus Gulman (Masterthesis 2014) and Philip Phu Dang Hoan Nguyen
 * (Masterthesis 2018) but has been heavily altered by Michael Cyl with bug
 * fixed, improvements and refactorings. Most notable changes the ability to
 * extract source code comments and better formatting of closing brackets for
 * code blocks.
 */
public class SliceMapper {

	private static final Pattern singeLineCommentPattern = Pattern.compile("^\\s*//.*");
	private static final Pattern openingMultlineCommentPattern = Pattern.compile("^\\s*/\\*.*");
	private static final Pattern closingMultlineCommentPattern = Pattern.compile(".*\\*+/$");
	private static final Pattern closingBracketPattern = Pattern.compile(".*}+;?$");

	public String getLinesOfCode(final String sourceCodeFileName, final Set<Integer> sliceLineNumbers,
			SliceLogger logger) {
		try {

			String sourceCodeLine = null;
			int sourceCodeLineNumber = 1;

			Map<Integer, String> sourceCodeFileMap = new HashMap<>();
			BufferedReader reader = new BufferedReader(new FileReader(sourceCodeFileName));
			StringBuilder builder = new StringBuilder();

			while ((sourceCodeLine = reader.readLine()) != null) {
				// mcyl: always add package declaration
				if (sourceCodeLine.startsWith("package")) {
					builder.append(sourceCodeLine);
					builder.append("\n");
					builder.append("\n");
				}
				sourceCodeFileMap.put(sourceCodeLineNumber++, sourceCodeLine);
			}

			List<Integer> listOfLineNumbers = new ArrayList<>();
			listOfLineNumbers.addAll(sliceLineNumbers);
			Collections.sort(listOfLineNumbers);

			for (int sliceLineNumber : listOfLineNumbers) {

				addAllCommentLines(sliceLineNumber, builder, sourceCodeFileMap);

				sourceCodeLine = sourceCodeFileMap.get(sliceLineNumber);
				if (sourceCodeLine != null) {
					builder.append(sourceCodeLine);
					builder.append("\n");

					// add extra line break after "}" (if the next line is not "}")
					// this adds an empty line between methods and makes the code more readable
					String nextSourceCodeLine = sourceCodeFileMap.get(sliceLineNumber + 1);
					if (nextSourceCodeLine != null && closingBracketPattern.matcher(sourceCodeLine).matches()
							&& !closingBracketPattern.matcher(nextSourceCodeLine).matches()) {
						builder.append("\n");
					}
				}
			}

			reader.close();
			return builder.toString();

		} catch (IOException e) {
			logger.log(e.getMessage());
			return "";
		}
	}

	/**
	 * This method searches for source code comments based on regex expressions and adds all lines to the slice 
	 * between the starting line of the comment and the starting line of the corresponding instruction.
	 * @param instructionStartLineNumber
	 * @param builder
	 * @param sourceCodeFileMap
	 */
	private void addAllCommentLines(int instructionStartLineNumber, StringBuilder builder,
			Map<Integer, String> sourceCodeFileMap) {

		// use stating line number of instruction to search upwards for comments
		int commentStartLineNumber = instructionStartLineNumber;
		// find comment starting line (if any)
		if (sourceCodeFileMap.get(commentStartLineNumber - 1) != null
				&& singeLineCommentPattern.matcher(sourceCodeFileMap.get(commentStartLineNumber - 1)).matches()) {
			// single line comments starting with "//" detected
			while (sourceCodeFileMap.get(commentStartLineNumber - 1) != null
					&& singeLineCommentPattern.matcher(sourceCodeFileMap.get(commentStartLineNumber - 1)).matches()) {
				commentStartLineNumber--;
			}
		} else if (sourceCodeFileMap.get(commentStartLineNumber - 1) != null
				&& closingMultlineCommentPattern.matcher(sourceCodeFileMap.get(commentStartLineNumber - 1)).matches()) {
			// closing multiline comment with "*/" detected -> find opening line with "/**""
			while (sourceCodeFileMap.get(commentStartLineNumber - 1) != null
					&& !openingMultlineCommentPattern.matcher(sourceCodeFileMap.get(commentStartLineNumber)).matches()) {
				commentStartLineNumber--;
			}
		}

		// add all lines between comment start and instruction start
		if (commentStartLineNumber < instructionStartLineNumber) {
			for (int i = commentStartLineNumber; i < instructionStartLineNumber; i++) {
				builder.append(sourceCodeFileMap.get(i));
				builder.append("\n");
			}
		}
	}
}
