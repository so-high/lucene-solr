package com.coupang.search.plugin.queryparser;

import org.apache.lucene.queryparser.flexible.core.QueryParserHelper;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.parser.StandardSyntaxParser;
import org.apache.lucene.queryparser.flexible.standard.processors.StandardQueryNodeProcessorPipeline;

public class PayloadQueryParser extends QueryParserHelper {

  public PayloadQueryParser() {
    super(new StandardQueryConfigHandler() ,
        new StandardSyntaxParser(),
        new StandardQueryNodeProcessorPipeline(null),
        new PayloadQueryTreeBuilder());
  }
}
