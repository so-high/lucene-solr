package com.coupang.search.plugin.queryparser;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.search.Query;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

public class PayloadQParser extends QParser {

	public PayloadQParser(String qstr, SolrParams localParams, SolrParams params,
	                      SolrQueryRequest req) {


		super(qstr, localParams, params, req);
		req.getSearcher().setSimilarity(new PayloadSimilarity());
	}

	@Override
	public Query parse() throws SyntaxError {
		PayloadQueryParser parser = new PayloadQueryParser();
		Query query=null;
		try {
			query = (Query) parser.parse(qstr, null);
		} catch (QueryNodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query;
	}

}
