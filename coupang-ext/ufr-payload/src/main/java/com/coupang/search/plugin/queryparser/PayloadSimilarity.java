package com.coupang.search.plugin.queryparser;

import org.apache.lucene.analysis.payloads.PayloadHelper;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.util.BytesRef;


public class PayloadSimilarity extends DefaultSimilarity {
	@Override
	public float scorePayload(int docId, int start, int end, BytesRef payload) {
		if (payload != null) {
	    	return PayloadHelper.decodeFloat(payload.bytes, payload.offset);
		} else {
	    	return 1.0F;
		}
	}

	@Override
	public float coord(int overlap, int maxOverlap){
		return 1.0F;
	}

	@Override
	public float queryNorm(float sumofSquerWeights){
		return 1.0F;
	}

	@Override
	public float lengthNorm(FieldInvertState state){
		return state.getBoost();
	}

	@Override
	public float sloppyFreq(int distance){
		return 1.0F;
	}

	@Override
	public float idf(long docFreg, long numDocs){
		return 1.0F;
	}

}
