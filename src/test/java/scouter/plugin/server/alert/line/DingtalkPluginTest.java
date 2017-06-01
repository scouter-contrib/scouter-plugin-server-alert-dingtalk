package scouter.plugin.server.alert.line;

import org.junit.Test;

/**
 * Copyright (C) 2017 SNOW Corporation. All rights Reserved.
 * SNOW Corporation PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Created by gunlee on 2017. 6. 1.
 */
public class DingtalkPluginTest {

	@Test
	public void send_test() throws Exception {
		String token = "68dee2cc3129528129778b36de34ac10aec70ea4c22d97a5aa6741fe13aa1e76";

		DingtalkPlugin plugin = new DingtalkPlugin();
		plugin.sendToDing("my test alert message !!", token);

		System.out.println("finish");
	}
}
