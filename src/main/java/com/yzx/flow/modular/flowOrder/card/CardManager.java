package com.yzx.flow.modular.flowOrder.card;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CardManager{
    private static final Logger LOGGER = LoggerFactory.getLogger(CardManager.class);
    
	public static final int CARD_NUM_LENGTH = 8;
	public static final int CARC_PASSWD_LENGTH = 10;
	public static final BlockingQueue<ICreateCard> CREATECARDQUEUE = new LinkedBlockingQueue<ICreateCard>();
	public static final ExecutorService executorService = Executors.newSingleThreadExecutor();
	public static final String BASE_STR = "abcdefghijklmnopqrstuvwxyz1234567890";
	public static final Map<Integer, String> WEISHU = new HashMap<Integer, String>();

	static {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < CARD_NUM_LENGTH; i++) {
			for (int j = i; j < CARD_NUM_LENGTH; j++) {
				sb.append("0");
			}
			WEISHU.put(i, sb.toString());
			sb.setLength(0);
		}

	}

	public void addCreateCard(ICreateCard createCard) {
	    CREATECARDQUEUE.add(createCard);
		try {
			for (int i = 0; i < CREATECARDQUEUE.size(); i++) {
				executorService.execute(CREATECARDQUEUE.take());
			}
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}


}
