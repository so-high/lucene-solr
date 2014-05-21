package com.coupang.search.solr;

import com.coupang.search.plugin.queryparser.PayloadQParserPlugin;
import org.apache.solr.common.SolrException;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.fail;

public class SolrTest {
  private static ArrayList<SolrCore> solrCores;

  @BeforeClass
  public static void beforeClasss()throws IOException, SAXException, ParserConfigurationException{
    String solrHome = "coupang-ext/solr-conf";

    File home = new File(solrHome);
    File solrXml = new File(home, "solr.xml");
    CoreContainer container = new CoreContainer(solrHome);
    container.load(solrHome, solrXml);

    solrCores = new ArrayList<>(container.getCores());
  }

  @Test
  public void shouldFindMyQueryPlugin() throws Exception{
    String expectedQueryPlugin = "payloadParser";

    for(SolrCore core: solrCores){
      try{
        PayloadQParserPlugin qp = (PayloadQParserPlugin)core.getQueryPlugin(expectedQueryPlugin);
      }catch(SolrException e){
        e.printStackTrace();
        fail("Query parser plugin not configured for core:" + core.getName());
      }
    }
  }
}
