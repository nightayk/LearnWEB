package three;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reques3 {
	private final static String Name_REGEX = "</font>(.*?)</em>";
	private final static String Price_REGEX = "<em>��</em><i>(.*?)</i></strong>";
	private final static String Describe_REGEX = "<i class=\"promo-words\" id=\"(.*?)\">";
	private final static String Assess_REGEX = "<strong><a id=\"J_comment_([\\S\\s]*?)target=\"_blank\">(.*?)</a>������</strong>";
	private final static String Shop_REGEX = "target=\"_blank\">(.*?)</a><b title=\"��ϵ�ͷ�\"";
	
	private static String getHtml(String url,String charset) throws Exception{//��ȡhtml����
		URL websiteUrl = new URL(url);
		URLConnection connection = websiteUrl.openConnection();//������
		InputStream in = connection.getInputStream();//����������
		InputStreamReader inr = new InputStreamReader(in,Charset.forName(charset));
		BufferedReader br = new BufferedReader(inr);
		String line;
		StringBuffer sb = new StringBuffer();
		while((line=br.readLine())!=null) {
			sb.append(line,0,line.length());
		}
		br.close();
		inr.close();
		in.close();
		return sb.toString();
		
	
		

	}
	
	private static List<String> getName(String html){
		Matcher m = Pattern.compile(Name_REGEX).matcher(html);
		List<String> name = new ArrayList<String>();
		while(m.find()) {
			name.add(m.group());
			
		}
		return name;
	}
	
	public static void main(String[] args) throws Exception {
		
	}
}
