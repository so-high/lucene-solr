package com.coupang.search.plugin.filter;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.FloatEncoder;
import org.apache.lucene.analysis.payloads.IdentityEncoder;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.payloads.PayloadEncoder;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ExternalPayloadTokenFilterFactory extends TokenFilterFactory
    implements ResourceLoaderAware {
  public static final String ENCODER_ATTR = "encoder";
  private PayloadEncoder encoder;
  private Map<String, Float> weightByTokenMap = null;

  @Override
  public ExternalPayloadTokenFilter create(TokenStream input) {
    ExternalPayloadTokenFilter coupangPayloadTokenFilter = new ExternalPayloadTokenFilter(input, encoder);
    coupangPayloadTokenFilter.setPayloadMap(weightByTokenMap);
    return coupangPayloadTokenFilter;
  }

  @Override
  public void init(Map<String, String> args) {
    super.init(args);
    try{
      readKeywordWeight();
    }catch(Exception e){
      throw new RuntimeException("Failed to load the UFR into payload attribute.",e);
    }
  }

  public void readKeywordWeight() throws Exception {
    Connection conn;
    Statement stmt;
    ResultSet rs;

    weightByTokenMap = new HashMap<>();

    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new Exception("Failed to register mysql JDBC driver class.", e);
    }

    try {
      String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/solr?sessionVariables=group_concat_max_len=10000000";
      String userId = "coupang";
      String userPass = "coupang123";
      conn = DriverManager.getConnection(jdbcUrl, userId, userPass); // get Connection Object
      System.out.println("Connection Success!");
    }catch(Exception e){
      throw new Exception("Failed to connect the database.", e);
    }

    stmt = null;
    rs = null;
    try{
      stmt = conn.createStatement(); // get Statement Object
      //stmt.executeUpdate("use DB명");
      rs = stmt.executeQuery("SELECT concat(deal_id, \"##\", query) as akey, (click_cnt * res_cnt) / srch_cnt as value FROM tbl_query_feed_back");

      while(rs.next()){
        weightByTokenMap.put(rs.getString("akey"), rs.getFloat("value"));
      }

      rs.close();
      stmt.close();
    } catch (SQLException e) {
      new Exception("Failed to fetch data from database.", e);
    } finally{
      if(rs!=null)rs.close();
      if(stmt!=null)stmt.close();
      if(conn!=null)conn.close();
    }
  }

  @Override
  public void inform(ResourceLoader loader) {
    String encoderClass = args.get(ENCODER_ATTR);
    if (encoderClass == null) {
      throw new IllegalArgumentException("Parameter " + ENCODER_ATTR + " is mandatory");
    }
    if (encoderClass.equals("float")){
      encoder = new FloatEncoder();
    } else if (encoderClass.equals("integer")){
      encoder = new IntegerEncoder();
    } else if (encoderClass.equals("identity")){
      encoder = new IdentityEncoder();
    } else {
      encoder = loader.newInstance(encoderClass, PayloadEncoder.class);
    }
  }
}
