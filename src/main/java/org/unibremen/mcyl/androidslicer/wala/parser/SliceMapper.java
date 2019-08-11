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
 * This is an implementation of a mapper class to get the actual source code lines from their corresponding line numbers.
 * The code based on the work by Markus Gulman (Masterthesis 2014) and Philip Phu Dang Hoan Nguyen (Masterthesis 2018) but has been 
 * heavily altered by Michael Cyl with bug fixed, improvements and refactorings. Most notable changes the ability to extract source
 * code comments and better formatting of closing brackets for code blocks.
 */
public class SliceMapper {

	private static final Pattern commentsPattern = Pattern.compile("^\\s*[\\/\\/.*|\\*.*].*");
	private static final Pattern closingBracketPattern = Pattern.compile("\\s*\\)*};?\\s*");

	public String getLinesOfCode(final String sourceCodeFileName, final Set<Integer> sliceLineNumbers, SliceLogger logger) {
		try {

			String sourceCodeLine = null;
			int sourceCodeLineNumber = 1;

			Map<Integer, String> sourceCodeFileMap = new HashMap<>();
			BufferedReader reader = new BufferedReader(new FileReader(sourceCodeFileName));

			while ((sourceCodeLine = reader.readLine()) != null) {
				sourceCodeFileMap.put(sourceCodeLineNumber++, sourceCodeLine);
			}

			List<Integer> listOfLineNumbers = new ArrayList<>();
			listOfLineNumbers.addAll(sliceLineNumbers);
			Collections.sort(listOfLineNumbers);

			StringBuilder builder = new StringBuilder();
			for (int sliceLineNumber : listOfLineNumbers) {

				addAllCommentLines(sliceLineNumber, builder, sourceCodeFileMap);

				sourceCodeLine = sourceCodeFileMap.get(sliceLineNumber);
				if (sourceCodeLine != null) {
					builder.append(sourceCodeLine);
					builder.append("\n");
					
					// add extra line break after "}" if the next line is not "}"
					String nextSourceCodeLine = sourceCodeFileMap.get(sliceLineNumber + 1);
					if(nextSourceCodeLine != null &&
					   closingBracketPattern.matcher(sourceCodeLine).matches() &&
					   !closingBracketPattern.matcher(nextSourceCodeLine).matches()){
							builder.append("\n");
					}
				}
			}
			
			reader.close();
			return builder.toString();

		} catch (IOException e) {
            logger.log(e.getMessage());
            return null;
        }
	}

	private void addAllCommentLines(int instructionStartLineNumber, StringBuilder builder, Map<Integer, String> sourceCodeFileMap){

		// first find comment starting line (if any)
		int commentStartLineNumber = instructionStartLineNumber;
		while(sourceCodeFileMap.get(commentStartLineNumber - 1) != null && 
			commentsPattern.matcher(sourceCodeFileMap.get(commentStartLineNumber - 1)).matches()){
			commentStartLineNumber--;
		}

		if (commentStartLineNumber < instructionStartLineNumber){
			// add all lines between comment start and instruction start
			for(int i = commentStartLineNumber; i < instructionStartLineNumber; i++){
				builder.append(sourceCodeFileMap.get(i));
				builder.append("\n");
			}
		}
	}
}
