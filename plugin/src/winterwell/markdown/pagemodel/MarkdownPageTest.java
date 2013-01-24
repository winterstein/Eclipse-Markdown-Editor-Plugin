package winterwell.markdown.pagemodel;

import java.io.File;
import java.util.List;

import winterwell.markdown.pagemodel.MarkdownPage.Header;
import winterwell.utils.io.FileUtils;



public class MarkdownPageTest //extends TestCase 
{

	public static void main(String[] args) {
		MarkdownPageTest mpt = new MarkdownPageTest();
		mpt.testGetHeadings();
	}
	
	public void testGetHeadings() {
		// problem caused by a line beginning --, now fixed
		String txt = FileUtils.read(new File(
				"/home/daniel/winterwell/companies/DTC/projects/DTC-bayes/report1.txt")); 
		MarkdownPage p = new MarkdownPage(txt);
		List<Header> h1s = p.getHeadings(null);
		Header h1 = h1s.get(0);
		List<Header> h2s = h1.getSubHeaders();
		assert h2s.size() > 2;
	}

}
