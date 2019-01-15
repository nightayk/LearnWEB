


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class REQUEST1 {

	private static final String IMGURL_REGEX = "<img.*src=(.*?)[^>]*?>";
	private static final String IMGSRC_REGEX = "https:(.*?)jpg";
	private static final String NEXTPAGE_REGEX = "<a class=\"next page-numbers\"(.*?)><i";
	private static String getHtml(String url,String charset) throws Exception{//��ȡhtml����
		URL websiteUrl = new URL(url);
		URLConnection connection = websiteUrl.openConnection();//������
        connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		InputStream in = connection.getInputStream();//����������
		InputStreamReader inr = new InputStreamReader(in,Charset.forName(charset));
		BufferedReader br = new BufferedReader(inr);
		String line;
		StringBuffer sb = new StringBuffer();
		while((line=br.readLine())!=null) {
			sb.append(line,0,line.length());
			sb.append("\n");
		}
		br.close();
		inr.close();
		in.close();
		return sb.toString();
	}
	
	private static List<String> getImageUrl(String html){//��ȡͼƬ��url��ַ
		Matcher m = Pattern.compile(IMGURL_REGEX).matcher(html);
		List<String> imageUrl = new ArrayList<String>();
		while(m.find()) {
			imageUrl.add(m.group());
		}
		return imageUrl;
	}
	
	private static List<String> getImageSrc(List<String> imageUrl){
		List<String> imageSrc = new ArrayList<String>();
		for(String img:imageUrl) {
			Matcher m = Pattern.compile(IMGSRC_REGEX).matcher(img);
			while(m.find()) {
				imageSrc.add(m.group().substring(m.group().indexOf("\"")+1,m.group().length()));
			}
		}
		return imageSrc;
	}
	
	private static void DownloadImage(List<String> imageSrc) throws IOException {

			for(String url: imageSrc) {
				Date begindate = new Date();//��ʼʱ��
				String imageName = url.substring(url.lastIndexOf("/")+1,url.length());
				URL uri = new URL(url);
				URLConnection connection = uri.openConnection();
				connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
				InputStream in = connection.getInputStream();
				FileOutputStream out = new FileOutputStream(new File("D:\\Picture\\"+imageName));
				byte[] buf = new byte[1024];
				int len = 0;
				System.out.println("��ʼ����:"+url);
				while((len = in.read(buf,0,buf.length))!=-1) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				System.out.println("�������");
				Date overdate =new Date();
				double time = overdate.getTime()-begindate.getTime();
				System.out.println("��ʱ:"+time/1000+"s");
			}
		
	}
	
	private static StringBuffer nextPage(String html) {
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile(NEXTPAGE_REGEX).matcher(html);
		if(m.find()) {
			sb.append(m.group().substring(m.group().indexOf("ht"), m.group().lastIndexOf("\"")));
			return sb;
		}
		else return null;
	}
	
	 public static void main(String[] args) throws Exception {
		StringBuffer pageUrl = new StringBuffer();
		pageUrl.append("https://gratisography.com");
		Date allBegindate = new Date();//�ܿ�ʼʱ��
		for(int i = 1;i<=10;i++) {
	         //���html�ı�����
	         String HTML = getHtml(pageUrl.toString(),"UTF-8");
	         //��ȡͼƬ��ǩ
	         List<String> imageUrl = getImageUrl(HTML);
	         //��ȡͼƬsrc��ַ
	         List<String> imageSrc = getImageSrc(imageUrl);
	         //����ͼƬ
	         DownloadImage(imageSrc);
	         //��ȡ��һҳ��ַ
	         String nextUrl = nextPage(HTML).toString();
	         if(nextUrl==null) break;
	         pageUrl.delete(0,pageUrl.length());
	         pageUrl.append(nextUrl);
		}
		Date allOverdate = new Date();
		double allTime = allOverdate.getTime()-allBegindate.getTime();
		System.out.println("�ܺ�ʱ:"+allTime/1000+"s");
	 }
	     


}
