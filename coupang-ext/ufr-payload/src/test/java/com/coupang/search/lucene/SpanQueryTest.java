package com.coupang.search.lucene;

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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

public class    SpanQueryTest {

	private RAMDirectory directory;
	private IndexSearcher searcher;
	private IndexReader reader;
	private Analyzer analyzer;

	private SpanTermQuery quick, brown, red, fox, lazy, sleepy, dog, cat;

	final private String fieldName = "f";

	@Before
	public void setUp() throws Exception{

		directory = new RAMDirectory();
		analyzer = new WhitespaceAnalyzer(Version.LUCENE_41);
		IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_41, analyzer);
		IndexWriter writer = new IndexWriter(directory, writerConfig);

		FieldType defaultFieldType = new FieldType();
		defaultFieldType.setIndexed(true);
		defaultFieldType.setStored(true);
		defaultFieldType.setTokenized(true);

		Document doc = new Document();
		doc.add(
			new Field (fieldName, "the quick brown fox jumps over the lazy dog and the another fox whose elder brother fox", defaultFieldType)
		);
		writer.addDocument(doc);

		doc = new Document();
		doc.add(
			new Field(fieldName, "the quick red fox jumps over the sleepy cat",defaultFieldType)
		);
		writer.addDocument(doc);

		doc = new Document();
		doc.add(
			new Field(fieldName, "the quick white fox jumps over the slow horse",defaultFieldType)
		);
		writer.addDocument(doc);
		writer.close();

		reader = DirectoryReader.open(directory);
		searcher = new IndexSearcher(reader);

		quick = new SpanTermQuery(new Term(fieldName, "quick"));
		brown = new SpanTermQuery(new Term(fieldName, "brown"));
		red = new SpanTermQuery(new Term(fieldName, "red"));
		fox = new SpanTermQuery(new Term(fieldName, "fox"));
		lazy = new SpanTermQuery(new Term(fieldName, "lazy"));
		sleepy = new SpanTermQuery(new Term(fieldName, "sleepy"));
		dog = new SpanTermQuery(new Term(fieldName, "dog"));
		cat = new SpanTermQuery(new Term(fieldName, "cat"));
	}

	@Test
	public void testSpanTermQuery() throws Exception {
		//assertOnlyBrownFox();
		dumpSpans(fox);

	}


	private void assertOnlyBrownFox(Query query) throws Exception{
		TopDocs hits = searcher.search(query, 10);
		assertEquals(1, hits.totalHits);
		assertEquals("wrong doc", 0, hits.scoreDocs[0].doc);
	}

	private void assertBothFoxes(Query query)throws Exception{
		TopDocs hits = searcher.search(query, 10);
		assertEquals(2, hits.totalHits);
	}

	private void assertNoMatches(Query query) throws Exception{
		TopDocs hits = searcher.search(query, 10 );
		assertEquals(0, hits.totalHits);
	}

	private void dumpSpans(SpanQuery query) throws IOException {

		//get scores of docs
		TopDocs hits = searcher.search(query, 10);
		float[] scores = new float[hits.totalHits];
		for(ScoreDoc sd : hits.scoreDocs){
			scores[sd.doc] = sd.score;
		}

		//find atomic reader context
		AtomicReaderContext atomicReaderCtx=null;
		for(IndexReaderContext ctx : reader.getContext().children()){
			if( ctx instanceof AtomicReaderContext ){
				atomicReaderCtx=(AtomicReaderContext)ctx;
				break;
			}
		}

		//get spans
		Spans spans = query.getSpans(atomicReaderCtx, null, new HashMap<Term,TermContext>());
		System.out.println();
		System.out.println(" "+ query);

		//iterate each span
		int numSpans = 0;
		while(spans.next()){
			numSpans++;

			//get the matched document
			int id = spans.doc();
			Document doc = reader.document(id);

			//원문을 다시 분석
			TokenStream stream = analyzer.tokenStream("WhateverItCanBe", new StringReader(doc.get(fieldName)));
			CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);

			StringBuilder buf = new StringBuilder();
			buf.append(" ").append(id).append(" - ").append(spans).append(" : ");
			int i = 0;
			while(stream.incrementToken()){
				System.out.println("");
				if(i == spans.start()){
					buf.append("<");
				}

				buf.append(term.buffer(), 0, term.length());

				if(i+1==spans.end()){
					buf.append(">");
				}

				buf.append(" ");
				i++;
			}
			buf.append("(").append(scores[id]).append(") ");
			System.out.println(buf.toString());

			if(numSpans == 0){
				System.out.println("   No spans");
			}
		}
		System.out.println();
	}
}
