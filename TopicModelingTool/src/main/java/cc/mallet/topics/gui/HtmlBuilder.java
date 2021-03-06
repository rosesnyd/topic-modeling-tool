package cc.mallet.topics.gui;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import static cc.mallet.topics.gui.TopicModelingTool.TOPIC_WORDS;
import static cc.mallet.topics.gui.TopicModelingTool.DOCS_IN_TOPICS;
import static cc.mallet.topics.gui.TopicModelingTool.TOPICS_IN_DOCS;

public class HtmlBuilder {
    public static final String GUI_CSS = "malletgui.css"; 

    String CSV_DEL = ",";
	ArrayList<String> docNames = new ArrayList<String>();
	ArrayList<String> topics = new ArrayList<String>();
	int[][] Ntd;
	File input;
	
	
	public HtmlBuilder(int[][] value, File f, String csvDelim)
	{
		Ntd = value;
		input = f;
        CSV_DEL = csvDelim;
	}
	
	void createCss(File htmlDir, InputStream cssResource) throws IOException{
	    
	    OutputStream out = new FileOutputStream(new File(htmlDir, GUI_CSS));

	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = cssResource.read(buf)) > 0) {
	        out.write(buf, 0, len);
	    }
	    cssResource.close();
	    out.close();
	}

	
	
	void writeHtmlHeader(BufferedWriter b, String title, String cssPath){
		String str = "<html><head><meta content=\"text/html; charset=ISO-8859-1\"http-equiv=\"Content-Type\"><head><link rel=\"stylesheet\" type=\"text/css\" href=\""+cssPath+"\" />";
		try {
			b.write(str);
			writeTitle(b, title);
			b.write("</head>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	void writeHtmlFooter(BufferedWriter b){
		String str = "</body></html>";
		try {
			b.write(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void writeReturnLink(BufferedWriter b){
		String str = "<br><br><br><br>"+makeUrl("../all_topics.html", "<b>[Index]</b>");
		try {
			b.write(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void writeTitle(BufferedWriter b, String title){ 
		String str = "<title>"+title+"</title>";
		try {
			b.write(str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	String makeUrl(String url, String text)
	{	
		
		return(String.format("<a href=%s>%s</a>", url, text));
	}
	
	void writeFileExcerpt(File f, BufferedWriter out) throws IOException{
		

		String line;
		FileReader fread = new FileReader(f);
        BufferedReader in = new BufferedReader(fread);
        int PRINTCHARS = 500;
        int charsPrinted=0;	
        out.write("<textarea style=\"width: 50%; height: 150px;\">");
			while ((line = in.readLine()) != null){
				if(charsPrinted+line.length()>PRINTCHARS)
				{
					out.write(line.substring(0, PRINTCHARS - charsPrinted));
					break;
				}
				else{
					out.write(line+"\n");
					charsPrinted++;				
				}				
			}
			out.write("...</textarea>");

	}
	
	void writeLineExcerpt(String str, BufferedWriter out) throws IOException{
		
		int PRINTCHARS = 500;
        out.write("<textarea style=\"width: 50%; height: 150px;\">");
        out.write(str.substring(0, Math.min(str.length(), PRINTCHARS)));				
		out.write("...</textarea>");
	}
	
	public void buildHtml1(File inputCsv, File outputDir)
	{	
		try{
		String FILE_NAME = "all_topics.html";
		FileWriter fwrite = new FileWriter(new File(outputDir, FILE_NAME));  
    	BufferedWriter out = new BufferedWriter(fwrite);			
        FileReader fread = new FileReader(inputCsv);
        BufferedReader in = new BufferedReader(fread);
		writeHtmlHeader(out, "Topic Index", GUI_CSS);
		
		out.write("<body><h4>List of Topics </h4>");
        
        String line = "";
		String[] st = null;	
		
		in.readLine();			//ignore header
		int n =1;
		out.write("<table style=\" text-align: left;\" border=\"0\" cellpadding=\"2\" cellspacing=\"2\"><tbody>");
        while ((line = in.readLine()) != null)
        {
        	st = line.split(CSV_DEL);
        	out.write(String.format("<tr><td>%d. </td><td>%s</td></tr>", n, makeUrl("Topics/Topic"+st[0]+".html", st[1])));

        	topics.add(st[1]);
        	n++;
        }
        writeHtmlFooter(out);
        out.flush();
        
		}catch(Exception e){
			System.err.println(e);
		}
	}
	
	public void buildHtml2(File inputCsv, File outputDir)
	{
		try{

			BufferedReader br = null;
	        FileReader fread = new FileReader(inputCsv);
	        BufferedReader in = new BufferedReader(fread);
			in.readLine();			//ignore header
			
	        String line = "";
			String[] st = null;
			if(input.isFile())
			{
				br  = new BufferedReader(new FileReader(input));
				
			}
	
		    while ((line = in.readLine()) != null)
	        {
	        	st = line.split(CSV_DEL);
	        	// values always have to be arranged pairwise -> if mod 2 not 0 this indicates prolems 
	        	if (st.length % 2 != 0) {
	        		System.out.println(line);
	        	}
	        	String FILE_NAME = "Doc"+st[0]+".html";
				FileWriter fwrite = new FileWriter(new File(outputDir, FILE_NAME)); 
		    	BufferedWriter out = new BufferedWriter(fwrite);	
				writeHtmlHeader(out, FILE_NAME, "../" + GUI_CSS);
				
				docNames.add(st[1]);
				
				out.write("<table style=\" text-align: left;\" border=\"0\" cellpadding=\"2\" cellspacing=\"2\"><tbody>");
				String tdocname = st[1];
	        	if(input.isDirectory()){
	        		URI furi = new URI(st[1]);
	        		File df = new File(furi);
					out.write("<body><h4><u>DOC</u> :"+df.getName()+"</h4><br>"); 
					try{writeFileExcerpt(df, out);} catch (Exception e){}
				}
	        	else{
					out.write("<body><h4><u>DOC</u> : "+"doc "+st[0]+"</h4><br>");
					String abc = br.readLine();
	        		writeLineExcerpt(abc, out);					
				}
				out.write("<br><br>Top topics in this doc (% words in doc assigned to this topic) <br>");
				for(int i =2;i<st.length-1;i=i+2){	
					try{out.write(String.format("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td>(%.0f%%)</td><td>", new Float(st[i+1])*100));}catch(Exception e){}
	        		try{
	        			// create <a href=""> based on the values in the st array 
	        			out.write(makeUrl("../Topics/Topic"+st[i]+".html", topics.get(Integer.parseInt(st[i]))));
        			}catch(Exception e){
        				System.out.println();
        			}
	        		out.write(" ...</td></tr>");
	        	}
				out.write("</tbody></table>");
				writeReturnLink(out);
	        	writeHtmlFooter(out);
	        	out.flush();
	        	out.close();	
	        }
		    
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void buildHtml3(File inputCsv, File outputDir)
	{
		
		try{			
	        FileReader fread = new FileReader(inputCsv);
	        BufferedReader in = new BufferedReader(fread);
			in.readLine();			//ignore header
			
	        String line = "";
			String[] st = null;
			String prevId = "-1";
			BufferedWriter out = null;
			
		    while ((line = in.readLine()) != null)
	        {
	        	st = line.split(CSV_DEL);
	        	
	        	if(!st[0].equals(prevId))
	        	{	if(!prevId.equals("-1"))
		        	{	out.write("</tbody></table>");
		        		writeReturnLink(out);
		        		writeHtmlFooter(out);
			        	out.flush();
			        	out.close();
		        	}
	        	String FILE_NAME = "Topic"+st[0]+".html";
				FileWriter fwrite = new FileWriter(new File(outputDir, FILE_NAME)); 
			   	out = new BufferedWriter(fwrite);	
				writeHtmlHeader(out, FILE_NAME, "../" + GUI_CSS);
	
				out.write("<body><h4><u>TOPIC</u> : "+topics.get(Integer.parseInt(st[0]))+" ...</h4>");
				prevId = st[0];
				out.write("<br>top-ranked docs in this topic (#words in doc assigned to this topic)<br>");
				out.write("<table style=\" text-align: left;\" border=\"0\" cellpadding=\"2\" cellspacing=\"2\"><tbody>");
	        	}
	        	
	        	String tdocname = new File(st[st.length-1]).getName();
	        	if(tdocname.equals("null-source")){
	        		tdocname = "doc "+st[st.length-2];
	        	}
	        	
	        	String doc_name = makeUrl("../Docs/Doc"+st[st.length-2]+".html ", tdocname);	        	
	        	try{
	        	out.write(String.format("<tr><td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td><td>%d.</td><td>%s</td><td>%s</td></tr>", Integer.parseInt(st[1])+1, "("+Ntd[Integer.parseInt(st[0])][Integer.parseInt(st[st.length-2])]+")", doc_name));
	        	} catch (Exception e){}
	        	//String temStr = String.format("        %d. %6s    ", "("+Ntd[Integer.parseInt(st[0])][Integer.parseInt(st[st.length-2])]+")");	        	
	        	//out.write(temStr.replace(" ", "&nbsp;"));        	
        		//out.write(makeUrl("../Docs/Doc"+st[st.length-2]+".html ", new File(st[st.length-1]).getName()));
        		//out.write(" <br>");
        	
	        }	
		    out.write("</tbody></table>");	  
		    writeReturnLink(out);
    		writeHtmlFooter(out);
        	out.flush();
        	out.close();
	
	        
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
	}
	
	public void createHtmlFiles(File outputDir)
	{ 
		
		File htmlDir = new File(outputDir, "output_html");
		htmlDir.mkdir();							//FIXME case when folder already exists
		try {
			createCss(htmlDir, TopicModelingTool.class.getResourceAsStream("/css/" + GUI_CSS));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File topicsHtmlDir = new File(htmlDir, "Topics");
		topicsHtmlDir.mkdir();
		File docsHtmlDir = new File(htmlDir, "Docs");
		docsHtmlDir.mkdir();
		File csvDir = new File(outputDir, "output_csv");
		buildHtml1(new File(csvDir, TOPIC_WORDS), htmlDir);
		buildHtml2(new File(csvDir, TOPICS_IN_DOCS), docsHtmlDir);
		buildHtml3(new File(csvDir, DOCS_IN_TOPICS), topicsHtmlDir);
		
	}
	
	

}
