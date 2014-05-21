package com.coupang.search.plugin.queryparser;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;

public class PayloadQueryNodeBuilder implements StandardQueryBuilder {

  @Override
  public Query build(QueryNode queryNode) throws QueryNodeException {
    FieldQueryNode node = (FieldQueryNode) queryNode;
    String fieldName = node.getFieldAsString();
    PayloadTermQuery payloadTermQuery = new PayloadTermQuery(new Term(fieldName, node.getTextAsString()),
        new AveragePayloadFunction(), false );

    return payloadTermQuery;
  }
}
