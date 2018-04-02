/*******************************************************************************
 * Copyright 2018 HungPV
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.keepmycoin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimeoutManager {
	public static interface ITimedOutListener {
		void doNotify();
	}
	
	private static final Timer timer = new Timer();
	private static Date lastAction = Calendar.getInstance().getTime();
	private static List<ITimedOutListener> timedoutListeners = new ArrayList<>();

	public static void start(ITimedOutListener listener) {
		timedoutListeners.add(listener);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Date now = Calendar.getInstance().getTime();
				Calendar expire = Calendar.getInstance();
				synchronized (lastAction) {
					expire.setTime(lastAction);
				}
				expire.add(Calendar.SECOND, Configuration.TIME_OUT_SEC);
				if (now.after(expire.getTime())) {
					synchronized (timedoutListeners) {
						timedoutListeners.stream().forEach(l -> l.doNotify());
						System.exit(0);
					}
				}
			}
		}, 1000, 1000);
	}
	
	public static void renew() {
		synchronized (lastAction) {
			lastAction = Calendar.getInstance().getTime();
		}
	}
}
