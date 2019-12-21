package cn.cerc.ui.docs;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MarkdownDocTest {
    private MarkdownDoc doc;

    @Before
    public void setUp() throws Exception {
        doc = new MarkdownDoc();
    }

    @Test
    public void testConvertHtml() {
        String html = doc.mdToHtml("### title");
        assertEquals(html, "<h3>title</h3>\n");
    }
}
