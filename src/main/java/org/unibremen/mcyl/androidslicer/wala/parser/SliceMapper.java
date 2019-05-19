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

public class SliceMapper {

	private BufferedReader br;
	private BufferedReader br2;

	public SliceMapper() {
	}

	public String markLinesOfCode(final String javaFileName, final Set<Integer> lineNumbers) {
		try {
			String line = null;
			Map<Integer, String> javaFile = new HashMap<>();
			br = new BufferedReader(new FileReader(javaFileName));
			int i = 1;
			while ((line = br.readLine()) != null) {
				javaFile.put(i, line);
				i++;
			}
			List<Integer> listOfLineNumbers = new ArrayList<>();
			listOfLineNumbers.addAll(lineNumbers);
			Collections.sort(listOfLineNumbers);
			StringBuilder sb = new StringBuilder();

			for (Integer row : javaFile.keySet()) {
				if (listOfLineNumbers.contains(row)) {
					String fileline = javaFile.get(row);
					if (fileline != null) {
						sb.append("@");
						sb.append(fileline);
						sb.append("\n");
					}
				} else {
					String fileline = javaFile.get(row);
					if (fileline != null) {
						sb.append(fileline);
						sb.append("\n");
					}
				}
			}
			return sb.toString();

		} catch (IOException e) {
			// System.err.println(e);
		}

		return null;
	}

	public String getLinesOfCode(final String javaFileName, final Set<Integer> lineNumbers) {
		try {
			String line = null;
			Map<Integer, String> javaFile = new HashMap<>();
			br2 = new BufferedReader(new FileReader(javaFileName));
			int i = 1;
			while ((line = br2.readLine()) != null) {
				javaFile.put(i, line);
				i++;
			}
			List<Integer> listOfLineNumbers = new ArrayList<>();
			listOfLineNumbers.addAll(lineNumbers);
			Collections.sort(listOfLineNumbers);
			StringBuilder sb = new StringBuilder();
			for (Integer number : listOfLineNumbers) {
				String fileline = javaFile.get(number);
				if (fileline != null) {
					sb.append(fileline);
					sb.append("\n");
				}
			}
			// System.out.println("String sb: " + sb.toString());
			return sb.toString();

		} catch (IOException e) {
			// System.err.println(e);
		}

		return "";
	}
}
