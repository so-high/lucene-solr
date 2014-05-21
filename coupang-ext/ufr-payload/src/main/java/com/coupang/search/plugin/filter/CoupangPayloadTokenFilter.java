package com.coupang.search.plugin.filter;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.NumberUtils;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
//import org.apache.lucene.analysis.kr.syn.KoreanSynonymFilter;
import org.apache.lucene.analysis.payloads.PayloadEncoder;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//import org.apache.lucene.util.fst.FST.BytesReader;

public class CoupangPayloadTokenFilter extends TokenFilter {

	public static final char DEFAULT_DELIMITER = '|';
	private final char delimiter;
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final PayloadAttribute payAtt = addAttribute(PayloadAttribute.class);
	private final PayloadEncoder encoder;
	Map<String, Float> mapWeight = null; 
	private int deal_id = -1;
	
	
	public void setMapWeight(Map<String, Float> mapWeight) {
		this.mapWeight = mapWeight;
	}


	protected CoupangPayloadTokenFilter(TokenStream input, char delimiter, PayloadEncoder encoder) {
	    super(input);
	    this.delimiter = delimiter;
	    this.encoder = encoder;
	}


	@Override
	public boolean incrementToken() throws IOException {
		//(KoreanSynonymFilter ) input.
//		BytesReader fst = ((KoreanSynonymFilter)input).getFstReader();
//		System.out.println(fst.toString());

	    if (input.incrementToken()) {
	    	String query = termAtt.toString();
	    	String key = "";
	    	if(query.length() == 8 && NumberUtils.isNumber(query)){
	    		deal_id = Integer.parseInt(query);
	    	}
	    	key = deal_id + "##" + query;
	    	Float val = mapWeight.get(key);
	    	if(val == null) val = 0.0f;
//	    	System.out.println(query + ":" + val );
	    	termAtt.setLength(termAtt.length());
	    	payAtt.setPayload(encoder.encode(val.toString().toCharArray()));
	    	
	    	return true;
	    } else return false;
	  }

}
