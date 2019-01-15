package two;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class REQUEST2 {
	private static final String Info_REGEX = "<p class=\"pl\">(.*?)</p>";
	private static final String Name_REGEX = "<div class=\"pl2\">(.*?)href=\"https://book.douban.com/subject/(.*?)</a>";
	private static final String AssessScore_REGEX = "<span class=\"rating_nums\">\\d\\.\\d</span>";
	private static final String AssessCount_REGEX = "<span class=\"pl\">\\(                    (\\d*?)人评价(\\s*?)\\)</span>";
	private static final String GetEnName_REGEX = "<div class=\"pl2\">([\\s\\S]*?)</div>";
	private static final String EnName_REGEX = "<span style=\"font-size:12px;\">[a-zA-Z](.*?)</span>";
	private static final String GetQuote_REGEX = "<td valign=\"top\">([\\s\\S]*?)</td>";
	private static final String Quote_REGEX = "<p class=\"quote\"([\\s\\S]*?)<span class=\"inq\">(.*?)</span";
	private static final String Page_REGEX = "<span class=\"thispage\">1</span>([\\s\\S]*?)<span class=\"next\">";
	private static final String Url_REGEX = "<a href=([\\s\\S]*?)</a>";
	
	private static List<String> getUrl(String html){
		Matcher m1 = Pattern.compile(Page_REGEX).matcher(html);
		List<String> listUrl = new ArrayList<String>();
		if(m1.find()) {
			String allUrl = m1.group();
			Matcher m2 = Pattern.compile(Url_REGEX).matcher(allUrl);
			while(m2.find()) {
				listUrl.add(m2.group().substring(m2.group().indexOf("<a href=")+9, m2.group().lastIndexOf("\"")));
			}
			System.out.println(listUrl);
			return listUrl;
		}
		else return null;
	}
	
	private static String getHtml(String url,String charset) throws Exception{//获取html内容
		URL websiteUrl = new URL(url);
		URLConnection connection = websiteUrl.openConnection();//打开链接
		InputStream in = connection.getInputStream();//建立输入流
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
			String str1 = m.group().replace(" ", "");//去掉空格
			String str2 = str1.replaceAll("<spanstyle=\"font-size:12px;\">", "");
			String str3 = str2.replaceAll("</span>", "");
			name.add(str3.substring(str3.lastIndexOf("\">")+2,str3.lastIndexOf("<")));
		}
		return name;
	}
	
	private static List<String> getEnName(String html){
		Matcher m1 = Pattern.compile(GetEnName_REGEX).matcher(html);
		List<String> enName = new ArrayList<String>();
		while(m1.find()) {//截取整段
			Matcher m2 = Pattern.compile(EnName_REGEX).matcher(m1.group());
			if(m2.find()) {//发现英文名
				enName.add(m2.group().substring(m2.group().indexOf(">")+1,m2.group().lastIndexOf("<")));
			}
			else {
				enName.add("无");
			}
		}
		return enName;
	}
	
	private static List<String> getInfo(String html){
		Matcher m = Pattern.compile(Info_REGEX).matcher(html);
		List<String> info = new ArrayList<String>();
		while(m.find()) {
			info.add(m.group().substring(m.group().indexOf(">")+1, m.group().lastIndexOf("<")));
		}
		return info;
	}
	
	private static List<String> getAssess(String html){
		Matcher m1 = Pattern.compile(AssessScore_REGEX).matcher(html);
		Matcher m2 = Pattern.compile(AssessCount_REGEX).matcher(html);
		List<String> assess = new ArrayList<String>();
		while(m1.find()) {
			m2.find();
			assess.add(m1.group().substring(m1.group().indexOf(">")+1, m1.group().lastIndexOf("<"))+","+m2.group().substring(m2.group().indexOf("(")+21, m2.group().lastIndexOf("价")+1));

		}
		return assess;
	}
	
	private static List<String> getQuote(String html){
		Matcher m1 = Pattern.compile(GetQuote_REGEX).matcher(html);
		List<String> quote = new ArrayList<String>();
		while(m1.find()) {
			Matcher m2 = Pattern.compile(Quote_REGEX).matcher(m1.group());
			if(m2.find()) {
				quote.add(m2.group().substring(m2.group().lastIndexOf(">")+1, m2.group().lastIndexOf("<")));
			}
			else quote.add("无");
		}
		return quote;
	}
	
	private static void insertDate(List<String> name,List<String> enName,List<String> info,List<String> assess,List<String> quote) {
		//建立连接
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8","root","512380");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0;i<name.size();i++) {
		      String sql = "insert into book values(?,?,?,?,?)";
		      PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement(sql);
				ps.setString(1,name.get(i));
				ps.setString(2,enName.get(i));
				ps.setString(3,info.get(i));
				ps.setString(4,assess.get(i));
				ps.setString(5,quote.get(i));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      try {
				ps.execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			String firstHtml = getHtml("https://book.douban.com/top250?icn=index-book250-all","utf-8");
			
			List<String> listUrl = new ArrayList<String>();
			listUrl.add("https://book.douban.com/top250?icn=index-book250-all");
			listUrl.addAll(getUrl(firstHtml));
			
			for(String url:listUrl) {
			String html = getHtml(url,"utf-8");
			List<String> name = getName(html);
			List<String> enName = getEnName(html);
			List<String> info = getInfo(html);
			List<String> assess = getAssess(html);
			List<String> quote = getQuote(html);
			//加载驱动
			Class.forName("com.mysql.jdbc.Driver");
			//将数据放入数据库
			insertDate(name,enName,info,assess,quote);
			}
			System.out.println("over");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
