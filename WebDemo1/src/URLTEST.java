

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class URLTEST {
	
	public static String getURLContent(String urlStr,String charset) {
		StringBuilder sb = new StringBuilder();
		try {
			URL url = new URL(urlStr);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(),Charset.forName(charset)));
			String temp = "";
			while((temp=reader.readLine())!=null) {
				sb.append(temp);
			}
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static List<String> getMatherSubstrs(String destStr,String regexStr){
		Pattern p = Pattern.compile(regexStr);
		Matcher m = p.matcher(destStr);
		List<String> result = new ArrayList<String>();
		while(m.find()) {
			result.add("\n"+m.group(1));
		}
		return result;
	}
	
	
	
	public static void main(String[] args) throws IOException{
		String destStr = getURLContent("https://www.163.com","gbk");
		List<String> result = getMatherSubstrs(destStr,"href=\"([\\w\\s./:]+?)\"");
		System.out.println(result);
	}
}
