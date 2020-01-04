import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author fongfiafia
 *z作用：去根目录下，读取srcFile 按照规则改变其内容，最后输出到resultFile中
 *z其中 “xxxxxxxx” 这些是代表要操作的内容
 */
public class MainMethod {
	
	private static String PRESTR = "<!--";
	private static String POSTSTR = "-->";
	
	public static void main(String[] args) {
		MainMethod mainMethod = new MainMethod();
		URL resource = mainMethod.getClass().getResource("");
		
		File file = new File(resource.getFile()+"srcFile/");
		File[] fileList = file.listFiles();
		
		if (fileList != null) {
			for (File tempFile : fileList) {
				tempFile = new File(resource.getFile()+"srcFile/"+tempFile.getName());
				changeFiles(tempFile,resource,tempFile.getName());
			}
		}
		
	}

	/**
	 * g 改变文件内容
	 * @param tempFile
	 * @param resource
	 * @param name
	 */
	private static void changeFiles(File tempFile, URL resource, String name) {
		Writer writer = null;
		BufferedReader reader = null;
		String readLine = null;
		int line = 0;
		int notePostFlag = 0;
		Map<Integer,String> map = new HashMap<Integer, String>();
		
		try {
			FileInputStream fis = new FileInputStream(tempFile);
			reader = new BufferedReader(new InputStreamReader(fis));
			
			while((readLine = reader.readLine()) != null) {
				// 添加后缀
				if (line == notePostFlag) { 
					readLine = contactPostStrMethod(readLine);
				}
				
				// 添加单行注释
				readLine = noteSingleLine(readLine);
				
				// 给多行进行注释
				Map<String,Integer> tempMap = noteMultiLine(readLine,map,line,notePostFlag);
				line = tempMap.get("line");
				notePostFlag = tempMap.get("notePostFlag");
				
				// 放在map中缓存
				map.put(line, readLine);
				line++;
			}
			
			File outFile = new File(resource.getFile()+"resultFile/"+tempFile.getName());
			FileOutputStream fos = new FileOutputStream(outFile);
			writer = new BufferedWriter(new OutputStreamWriter(fos));
			
			Set<Integer> keySet = map.keySet();
			for (Integer index : keySet) {
				String outStr = map.get(index);
				writer.write(outStr+"\r\n");
			}
			
			writer.flush();
		} catch (Exception e) {
			System.out.println("文件没找到"+e.getMessage());
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (Exception e2) {
				System.out.println("流关闭失败"+e2.getMessage());
			}
			
		}
		
		
	}

	private static Map<String, Integer> noteMultiLine(String readLine, Map<Integer, String> map, int line,
			int notePostFlag) {
		Map<String,Integer> result = new HashMap<String,Integer>();
		if (readLine.contains("xxxxxxxx")) {
			noteSpecifiedNumLine(2,map,line); // 这个2 是你给的参数，想要注释到这一行的前面n行
			notePostFlag = line + 2; // 这个2也是你给的参数，想要注释到这一行的后面n行
		}
		result.put("line",line);
		result.put("notePostFlag",notePostFlag);
		return result;
	}

	/**
	 * z指定注释掉多少行，这个使用在多行注释的时候
	 * @param pre
	 * @param map
	 * @param line
	 */
	private static void noteSpecifiedNumLine(int pre, Map<Integer, String> map, int line) {
		String preMapStr = (String) map.get(line - pre);
		preMapStr = PRESTR + preMapStr;
		map.put(line - pre, preMapStr);
		
	}

	/**
	 * z给一行注释掉
	 * @param readLine
	 * @return
	 */
	private static String noteSingleLine(String readLine) {
		if (readLine.contains("xxxxxxxx")) {
			readLine = contactBothStrMethod(readLine);
		}
		return readLine;
	}

	private static String contactBothStrMethod(String readLine) {
		readLine = PRESTR + readLine + POSTSTR; 
		return readLine;
	}

	private static String contactPostStrMethod(String readLine) {
		readLine = readLine + POSTSTR; 
		return readLine;
	}
}
