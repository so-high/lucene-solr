package com.coupang.search.plugin.queryparser;

import org.apache.lucene.queryparser.flexible.core.nodes.BoostQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.BoostQueryNodeBuilder;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryTreeBuilder;

public class PayloadQueryTreeBuilder extends StandardQueryTreeBuilder {
	public PayloadQueryTreeBuilder(){
		setBuilder( FieldQueryNode.class, new PayloadQueryNodeBuilder() );
		setBuilder( BoostQueryNode.class, new BoostQueryNodeBuilder());
	}
}
