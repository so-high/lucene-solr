package com.coupang.search.plugin.filter;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.payloads.PayloadEncoder;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;

import java.io.IOException;
import java.util.Map;

public class ExternalPayloadTokenFilter extends TokenFilter {

  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private final PayloadAttribute payAtt = addAttribute(PayloadAttribute.class);
  private final PayloadEncoder encoder;
  Map<String, Float> payloadMap = null;


  protected ExternalPayloadTokenFilter(TokenStream input, PayloadEncoder encoder) {
    super(input);
    this.encoder = encoder;
  }

  public void setPayloadMap(Map<String, Float> payloadMap) {
    this.payloadMap = payloadMap;
  }

  @Override
  public boolean incrementToken() throws IOException {
	//TODO : 특정 document와 특정 term을 식별해야 한다. 특정 문서라는 정보는 어떻게 알지?

    if (input.incrementToken()) {
      String token = termAtt.toString();

      payAtt.setPayload(null);
      if(!payloadMap.containsKey(token))
        return true;

      Float boostWeight = payloadMap.get(token);
      if(boostWeight!= null)
        payAtt.setPayload(encoder.encode(token.toString().toCharArray()));

      return true;
    }
    return false;
  }
}
