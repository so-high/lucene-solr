package com.coupang.search.plugin.filter;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.FloatEncoder;
import org.apache.lucene.analysis.payloads.IdentityEncoder;
import org.apache.lucene.analysis.payloads.IntegerEncoder;
import org.apache.lucene.analysis.payloads.PayloadEncoder;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.sql.Connection;
import java.sql.Statement;

public class CoupangPayloadTokenFilterFactory extends TokenFilterFactory
		implements ResourceLoaderAware {
	public static final String ENCODER_ATTR = "encoder";
	public static final String DELIMITER_ATTR = "delimiter";

	private PayloadEncoder encoder;
	private char delimiter = '|';
	private Map<String, Float> mapWeight = null;
	
	@Override
	public CoupangPayloadTokenFilter create(TokenStream input) {
		CoupangPayloadTokenFilter coupangPayloadTokenFilter = new CoupangPayloadTokenFilter(input, delimiter, encoder);
		coupangPayloadTokenFilter.setMapWeight(mapWeight);
		return coupangPayloadTokenFilter;
	}

	@Override
	public void init(Map<String, String> args) {
		super.init(args);
		readKeywordWeight();
	}

	public void readKeywordWeight(){
		Connection conn; 
		Statement stmt; 
		ResultSet rs; 
        
        mapWeight = new HashMap<String, Float>();
        
        try { 
            Class.forName("com.mysql.jdbc.Driver"); 
        } catch (ClassNotFoundException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        } 
         
         
        try { 
            String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/solr?sessionVariables=group_concat_max_len=10000000"; 
            String userId = "coupang"; 
            String userPass = "coupang123"; 
             
            conn = DriverManager.getConnection(jdbcUrl, userId, userPass); // get Connection Object 
            stmt = conn.createStatement(); // get Statement Object 
             
            System.out.println("Connection Success!"); 
             
            //stmt.executeUpdate("use DB명");             
            rs = stmt.executeQuery("SELECT concat(deal_id, \"##\", query) as akey, (click_cnt * res_cnt) / srch_cnt as value FROM tbl_query_feed_back"); 
             
            while(rs.next()){ 
                //System.out.print(rs.getString("akey") + " "); 
                //System.out.println(rs.getFloat("value") + " "); 
                mapWeight.put(rs.getString("akey"), rs.getFloat("value"));
            } 
             
            rs.close(); 
            stmt.close(); 
            conn.close(); 
        } catch (SQLException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
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

	    String delim = args.get(DELIMITER_ATTR);
	    if (delim != null){
	      if (delim.length() == 1) {
	        delimiter = delim.charAt(0);
	      } else{
	        throw new IllegalArgumentException("Delimiter must be one character only");
	      }
	    }
	  }
}
