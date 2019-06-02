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

import org.unibremen.mcyl.androidslicer.service.SliceLogger;

//TODO source: Philip,
// mcyl refractor for higher code quality
// mcy fix breaking changes since 1.5
public class SliceMapper {

	public String getLinesOfCode(final String javaFileName, final Set<Integer> lineNumbers, SliceLogger logger) {
		try {
			String line = null;
			Map<Integer, String> javaFile = new HashMap<>();
			BufferedReader reader = new BufferedReader(new FileReader(javaFileName));
			int i = 1;
			while ((line = reader.readLine()) != null) {
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
			reader.close();
			return sb.toString();

		} catch (IOException e) {
            logger.log(e.getMessage());
            return null;
        }
	}
}
