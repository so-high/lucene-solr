package com.coupang.search.plugin.queryparser;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.MaxPayloadFunction;
import org.apache.lucene.search.payloads.MinPayloadFunction;
import org.apache.lucene.search.payloads.PayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;
import org.apache.solr.search.QueryParsing;

/**
 * refer to : https://issues.apache.org/jira/browse/SOLR-1485
 */
public class PayloadQParserPlugin extends QParserPlugin {

  public void init(NamedList args) {
  }

  @Override
  public QParser createParser(String qstr, SolrParams localParams, SolrParams params, SolrQueryRequest req) {

    return new QParser(qstr, localParams, params, req) {
      @Override
      public Query parse() {
        return new PayloadTermQuery(
            new Term(localParams.get(QueryParsing.F), localParams.get(QueryParsing.V)),
            createPayloadFunction("avg" /*localParams.get("func")*/ ),
            false);
      }
    };
  }

  private PayloadFunction createPayloadFunction(String func) {
    // TODO: refactor so that payload functions are registered as plugins and loaded
    //       through SolrResourceLoader.

    PayloadFunction payloadFunction = null;
    if ("min".equals(func)) {
      payloadFunction = new MinPayloadFunction();
    } else if ("avg".equals(func)) {
      payloadFunction = new AveragePayloadFunction();
    } else if ("max".equals(func)) {
      payloadFunction = new MaxPayloadFunction();
    }

    if (payloadFunction == null) {
      throw new SolrException( SolrException.ErrorCode.BAD_REQUEST, "unknown PayloadFunction: " + func);
    }

    return payloadFunction;
  }
}
