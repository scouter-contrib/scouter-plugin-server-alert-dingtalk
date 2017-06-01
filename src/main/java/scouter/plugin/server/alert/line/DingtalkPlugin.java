/*
 *  Copyright 2016 Scouter Project.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  
 *  @author Sang-Cheon Park
 */
package scouter.plugin.server.alert.line;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import scouter.lang.AlertLevel;
import scouter.lang.pack.AlertPack;
import scouter.lang.pack.ObjectPack;
import scouter.lang.plugin.PluginConstants;
import scouter.lang.plugin.annotation.ServerPlugin;
import scouter.server.Configure;
import scouter.server.Logger;
import scouter.server.core.AgentManager;

import java.io.IOException;

/**
 * Scouter server plugin to send alert via dingtalk to group webhook bot
 *
 * @author Gun Lee (gunlee01@gmail.com) on 2017. 06. 01.
 */
public class DingtalkPlugin {

	private static final String DING_BOT_SEND_URL = "https://oapi.dingtalk.com/robot/send?access_token=";
    Configure conf = Configure.getInstance();

    @ServerPlugin(PluginConstants.PLUGIN_SERVER_ALERT)
    public void alert(final AlertPack pack) {
        if (conf.getBoolean("ext_plugin_dingtalk_send_alert", false)) {
        	
        	// Get log level (0 : INFO, 1 : WARN, 2 : ERROR, 3 : FATAL)
        	int level = conf.getInt("ext_plugin_dingtalk_level", 0);
        	
        	if (level <= pack.level) {
        		new Thread() {
        			public void run() {
                        try {
	                        String token = conf.getValue("ext_plugin_dingtalk_access_token");
                            assert token != null;

                        	// Get the agent Name
                        	String name = AgentManager.getAgentName(pack.objHash) == null ? "N/A" : AgentManager.getAgentName(pack.objHash);
                        	
                        	if (name.equals("N/A") && pack.message.endsWith("connected.")) {
                    			int idx = pack.message.indexOf("connected");
                        		if (pack.message.indexOf("reconnected") > -1) {
                        			name = pack.message.substring(0, idx - 6);
                        		} else {
                        			name = pack.message.substring(0, idx - 4);
                        		}
                        	}
                            
                            String title = pack.title;
                            String msg = pack.message;
                            if (title.equals("INACTIVE_OBJECT")) {
                            	title = "An object has been inactivated.";
                            	msg = pack.message.substring(0, pack.message.indexOf("OBJECT") - 1);
                            }
                          
                        	// Make message contents
                            String contents = "[TYPE] : " + pack.objType.toUpperCase() + "\n" + 
                                           	  "[NAME] : " + name + "\n" + 
                                              "[LEVEL] : " + AlertLevel.getName(pack.level) + "\n" +
                                              "[TITLE] : " + title + "\n" + 
                                              "[MESSAGE] : " + msg;


	                        sendToDing(contents, token);

                        } catch (Exception e) {
                        	println("[Error] : " + e.getMessage());
                        	
                        	if(conf._trace) {
                                e.printStackTrace();
                            }
                        }
        			}
        		}.start();
            }
        }
    }
    
	@ServerPlugin(PluginConstants.PLUGIN_SERVER_OBJECT)
	public void object(ObjectPack pack) {
    	if (pack.version != null && pack.version.length() > 0) {
			AlertPack ap = null;
			ObjectPack op = AgentManager.getAgent(pack.objHash);
	    	
			if (op == null && pack.wakeup == 0L) {
				// in case of new agent connected
				ap = new AlertPack();
		        ap.level = AlertLevel.INFO;
		        ap.objHash = pack.objHash;
		        ap.title = "An object has been activated.";
		        ap.message = pack.objName + " is connected.";
		        ap.time = System.currentTimeMillis();
		        
		        if (AgentManager.getAgent(pack.objHash) != null) {
		        	ap.objType = AgentManager.getAgent(pack.objHash).objType;
		        } else {
		        	ap.objType = "scouter";
		        }
				
		        alert(ap);
	    	} else if (op.alive == false) {
				// in case of agent reconnected
				ap = new AlertPack();
		        ap.level = AlertLevel.INFO;
		        ap.objHash = pack.objHash;
		        ap.title = "An object has been activated.";
		        ap.message = pack.objName + " is reconnected.";
		        ap.time = System.currentTimeMillis();
		        ap.objType = AgentManager.getAgent(pack.objHash).objType;
				
		        alert(ap);
	    	}
			// inactive state can be handled in alert() method.
    	}
	}

	public void sendToDing(String contents, String token) throws IOException {
		DingtalkPushFormat pushFormat = new DingtalkPushFormat();
		pushFormat.setText(contents);

		String body = new Gson().toJson(pushFormat);

		String url = DING_BOT_SEND_URL + token;

		HttpPost post = new HttpPost(url);
		post.addHeader("Content-Type","application/json");
		post.setEntity(new StringEntity(body));

		CloseableHttpClient client = HttpClientBuilder.create().build();

		// send the post request
		HttpResponse response = client.execute(post);

		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			println("Line message sent to [" + token + "] successfully.");
		} else {
			println("Line message sent failed. Verify below information.");
			println("[URL] : " + url);
			println("[Message] : " + body);
			println("[Reason] : " + EntityUtils.toString(response.getEntity(), "UTF-8"));
		}
	}

    private void println(Object o) {
        if (conf.getBoolean("ext_plugin_dingtalk_debug", false) || conf._trace) {
            Logger.println(o);
        }
    }
}
